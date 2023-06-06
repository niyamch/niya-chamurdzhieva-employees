package com.employment.dto;

public class ProbationDto {
	private String empId;
	private String projectId;
	private String dateFrom;
	private String dateTo;

	public ProbationDto() {
	}

	public ProbationDto(String empId, String projectId, String dateFrom, String dateTo) {
		this.empId = empId;
		this.projectId = projectId;
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public String getDateTo() {
		return dateTo;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}
}
