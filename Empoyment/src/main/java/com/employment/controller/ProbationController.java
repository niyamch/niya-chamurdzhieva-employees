package com.employment.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.employment.service.ProbationService;

@RestController("probation")
@RequestMapping("/api/probation")
public class ProbationController {
	@Autowired
	private ProbationService probationService;

	@RequestMapping(path = "/process-csv", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String[] processCsvFile(@RequestPart("file") MultipartFile file) {
		return this.probationService.processCsvFile(file);
	}
}
