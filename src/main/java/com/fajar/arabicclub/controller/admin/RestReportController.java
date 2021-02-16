package com.fajar.arabicclub.controller.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fajar.arabicclub.annotation.CustomRequestInfo;
import com.fajar.arabicclub.config.LogProxyFactory;
import com.fajar.arabicclub.dto.WebRequest;
import com.fajar.arabicclub.exception.ApplicationException;
import com.fajar.arabicclub.service.ExcelReportService;
import com.fajar.arabicclub.service.entity.CustomWorkbook;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@Controller
@RequestMapping("/api/app/report")
@Slf4j
public class RestReportController {

	@Autowired
	private ExcelReportService reportService;

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	public RestReportController() {
		log.info("------------------RestReportController----------------------");
	}

	@PostMapping(value = "/records", consumes = MediaType.APPLICATION_JSON_VALUE)
	@CustomRequestInfo(withRealtimeProgress = true)
	public void recordsReport(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {
		try {
			 
			log.info("entityreport {}", request);

			CustomWorkbook result = reportService.generateEntityReport(request, httpRequest);

			writeXSSFWorkbook(httpResponse, result);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new ApplicationException(e);
		}
	}

	public static void writeXSSFWorkbook(HttpServletResponse httpResponse, CustomWorkbook xwb) throws Exception {
		httpResponse.setContentType("text/xls");
		httpResponse.setHeader("content-disposition", "attachment;filename=" + xwb.getFileName());

		try (OutputStream outputStream = httpResponse.getOutputStream()) {
			xwb.write(outputStream);
		}
	}

	public static void writeFileReponse(HttpServletResponse httpResponse, File file) throws Exception {
		httpResponse.setHeader("content-disposition", "attachment; filename=" + file.getName());
		FileInputStream in = new FileInputStream(file);
		OutputStream out = httpResponse.getOutputStream();

		byte[] buffer = new byte[8192]; // use bigger if you want
		int length = 0;

		while ((length = in.read(buffer)) > 0) {
			out.write(buffer, 0, length);
		}
		in.close();
		out.close();
	}

}
