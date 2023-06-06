package com.employment.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.employment.dto.ProbationDto;
import com.opencsv.CSVReader;

@Component
public class ProbationService {

	public String[] processCsvFile(@RequestParam("file") MultipartFile file) {
		if (file.isEmpty()) {
			return new String[] { "The file is empty" };
		} else {
			try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
					CSVReader csvReader = new CSVReader(reader);) {

				List<String[]> records = csvReader.readAll();

				csvReader.close();
				reader.close();

				List<ProbationDto> probationDtos = mapProbationDtos(records);

				String[] errorResponse = validateProbationDtos(probationDtos);
				if (errorResponse != null) {
					return errorResponse;
				}

				String[] response = findEmployeesWithMaxCommonTimeInProject(probationDtos);
				if (response == null) {
					return new String[] { "No employees have worked in the same projects at the same time" };
				}

				return response;
			} catch (Exception e) {
				e.printStackTrace();
				return new String[] { "A problem has occured, please contact support." };
			}
		}
	}

	private static String[] findEmployeesWithMaxCommonTimeInProject(List<ProbationDto> probationDtos) {
		String empId1 = null;
		String empId2 = null;
		String projectId = null;

		long maxTimeInOneProject = 0;

		Calendar now = Calendar.getInstance();

		for (int i = 0; i < probationDtos.size() - 1; i++) {
			Calendar dateFrom1 = getCalendarFromString(probationDtos.get(i).getDateFrom(), now);
			Calendar dateTo1 = getCalendarFromString(probationDtos.get(i).getDateTo(), now);

			for (int j = i + 1; j < probationDtos.size(); j++) {
				if (probationDtos.get(i).getEmpId().equals(probationDtos.get(j).getEmpId())) {
					continue;
				}

				Calendar dateFrom2 = getCalendarFromString(probationDtos.get(j).getDateFrom(), now);
				Calendar dateTo2 = getCalendarFromString(probationDtos.get(j).getDateTo(), now);

				if (probationDtos.get(i).getProjectId().equals(probationDtos.get(j).getProjectId())) {
					long commonTimeBetweenCalendars = calculateCommonTimeBetweenCalendars(dateFrom1, dateTo1, dateFrom2,
							dateTo2);
					if (commonTimeBetweenCalendars > maxTimeInOneProject) {
						maxTimeInOneProject = commonTimeBetweenCalendars;
						empId1 = probationDtos.get(i).getEmpId();
						empId2 = probationDtos.get(j).getEmpId();
						projectId = probationDtos.get(i).getProjectId();
					}
				}
			}
		}

		if (maxTimeInOneProject == 0) {
			return null;
		}

		return new String[] { empId1, empId2, projectId };
	}

	private static long calculateCommonTimeBetweenCalendars(Calendar dateFrom1, Calendar dateTo1, Calendar dateFrom2,
			Calendar dateTo2) {
		Calendar biggerCalendarFrom = dateFrom1.compareTo(dateFrom2) > 0 ? dateFrom1 : dateFrom2;
		Calendar lowerCalendarTo = dateTo1.compareTo(dateTo2) < 0 ? dateTo1 : dateTo2;

		return lowerCalendarTo.getTimeInMillis() - biggerCalendarFrom.getTimeInMillis();
	}

	private static Calendar getCalendarFromString(String calendarString, Calendar now) {
		if (calendarString.toLowerCase().equals("null") || calendarString.isEmpty()) {
			return now;
		}

		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			calendar.setTime(sdf.parse(calendarString));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return calendar;
	}

	private String[] validateProbationDtos(List<ProbationDto> probationDtos) {
		if (probationDtos.size() < 2) {
			return new String[] { "The number of data rows must be at least two." };
		}

		for (int i = 0; i < probationDtos.size(); i++) {
			ProbationDto probationDto = probationDtos.get(i);
			if (probationDto.getEmpId() == null || probationDto.getEmpId().isEmpty()) {
				return new String[] { "The EmpID of row " + (i + 1) + " is not valid." };
			}

			if (probationDto.getProjectId() == null || probationDto.getProjectId().isEmpty()) {
				return new String[] { "The ProjectID of row " + (i + 1) + " is not valid." };
			}

			if (probationDto.getDateFrom() == null || probationDto.getDateFrom().isEmpty()
					|| !isDateStringValid(probationDto.getDateFrom())) {
				return new String[] { "The DateFrom of row " + (i + 1) + " is not valid." };
			}

			if (probationDto.getDateTo() != null && !probationDto.getDateTo().isEmpty()
					&& !isDateStringValid(probationDto.getDateFrom())) {
				return new String[] { "The DateTo of row " + (i + 1) + " is not valid." };
			}
		}

		return null;
	}

	public boolean isDateStringValid(String dateStr) {
		try {
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd");
			LocalDate.parse(dateStr, dateFormatter);
		} catch (DateTimeParseException e) {
			return false;
		}
		return true;
	}

	public List<ProbationDto> mapProbationDtos(List<String[]> records) {
		List<ProbationDto> probationDtos = new ArrayList<ProbationDto>();
		// iterate through the list of records
		for (String[] record : records) {
			ProbationDto probationDto = new ProbationDto(record[0], record[1], record[2], record[3]);
			probationDtos.add(probationDto);
		}

		return probationDtos;
	}
}
