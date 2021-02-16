package com.fajar.arabicclub.service.entity;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.arabicclub.dto.model.BaseModel;
import com.fajar.arabicclub.entity.BaseEntity;
import com.fajar.arabicclub.entity.User;
import com.fajar.arabicclub.entity.setting.EntityProperty;
import com.fajar.arabicclub.service.ProgressService;
import com.fajar.arabicclub.service.SessionValidationService;
import com.fajar.arabicclub.util.EntityPropertyBuilder;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EntityReportService {
 
	@Autowired
	private ProgressService progressService;
	@Autowired
	private SessionValidationService sessionValidationService;

	public CustomWorkbook getEntityReport(List<? extends BaseModel> entities, Class<? extends BaseEntity> entityClass,
			HttpServletRequest httpRequest) throws Exception {
		log.info("Generate entity report: {}", entityClass); 
		User currentUser = sessionValidationService.getLoggedUser(httpRequest);
		String requestId = currentUser.getRequestId();
		Class modelClass = BaseEntity.getTypeArgumentOfGenericSuperClass(entityClass);
		
		EntityProperty entityProperty = EntityPropertyBuilder.createEntityProperty(modelClass, null);
//		ReportData reportData = ReportData.builder().entities(entities).entityProperty(entityProperty).requestId(requestId).build(); 
	
		EntityReportBuilder reportBuilder = new EntityReportBuilder( entityProperty, entities, requestId);
		reportBuilder.setProgressService(progressService);
		
		progressService.sendProgress(1, 1, 10, false, httpRequest);

		CustomWorkbook file = reportBuilder.buildReport(); 
		
		log.info("Entity Report generated");

		return file;
	}

}
