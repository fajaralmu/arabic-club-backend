package com.fajar.arabicclub.service.entity;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import com.fajar.arabicclub.config.LogProxyFactory;
import com.fajar.arabicclub.dto.Filter;
import com.fajar.arabicclub.dto.WebRequest;
import com.fajar.arabicclub.dto.WebResponse;
import com.fajar.arabicclub.dto.model.BaseModel;
import com.fajar.arabicclub.entity.BaseEntity;
import com.fajar.arabicclub.entity.setting.EntityManagementConfig;
import com.fajar.arabicclub.entity.setting.EntityProperty;
import com.fajar.arabicclub.exception.ApplicationException;
import com.fajar.arabicclub.repository.CustomRepositoryImpl;
import com.fajar.arabicclub.repository.DatabaseProcessor;
import com.fajar.arabicclub.repository.EntityRepository;
import com.fajar.arabicclub.util.CollectionUtil;
import com.fajar.arabicclub.util.EntityUtil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MasterDataService {

	public static final String ORIGINAL_PREFFIX = "{ORIGINAL>>";

	@Autowired
	private CustomRepositoryImpl customRepository;
	@Autowired
	private EntityRepository entityRepository; 
	@Autowired
	private EntityManagementPageService entityManagementPageService;   
	
	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this); 
	}

	private EntityManagementConfig getEntityManagementConfig(String key) {
		return entityRepository.getConfiguration(key);
	}

	/**
	 * add & update entity
	 * 
	 * @param request
	 * @param servletRequest
	 * @param newRecord
	 * @return
	 */
	public WebResponse saveEntity(WebRequest request, HttpServletRequest servletRequest, boolean newRecord) {

			final String key = request.getEntity().toLowerCase();
			EntityManagementConfig entityConfig = getEntityManagementConfig(key);
			BaseEntityUpdateService updateService = entityConfig.getEntityUpdateService();
			String fieldName = entityConfig.getFieldName();
			BaseModel entityValue = null;

			try {
				Field entityField = EntityUtil.getDeclaredField(WebRequest.class, fieldName);
				entityValue = (BaseModel) entityField.get(request);

				log.info("save {}", entityField.getName());
				log.info("newRecord: {}", newRecord);
				
				if (entityValue != null) {
					 
					BaseEntity savedEntity = updateService.saveEntity(entityValue.toEntity(), newRecord, servletRequest); 
					 
					return WebResponse.builder().entity(savedEntity.toModel()).build();
				} else {
					return WebResponse.failed();
				}

			} catch (Exception e) {
				e.printStackTrace();
				throw new ApplicationException(e.getMessage());
			}

		 
	}
 
	/**
	 * get list of entities filtered
	 * 
	 * @param request
	 * @return
	 */
	public WebResponse filter(WebRequest request, HttpServletRequest httpRequest) {
		Class<? extends BaseEntity> entityClass = null;

		Filter filter = EntityUtil.cloneSerializable(request.getFilter());

		if (filter == null) {
			filter = new Filter();
		}
		if (filter.getFieldsFilter() == null) {
			filter.setFieldsFilter(new HashMap<String, Object>());
		}

		try {

			String entityName = request.getEntity().toLowerCase();
			EntityManagementConfig entityConfig = getEntityManagementConfig(entityName);
			log.info("entityName: {}, config: {}", entityName, entityConfig);
			if (null == entityConfig) {
				throw new Exception("Invalid entity:"+entityName);
			}
			BaseEntityUpdateService updateService = entityConfig.getEntityUpdateService();
			entityClass = entityConfig.getEntityClass();
			FilterResult entityResult = filterEntities(filter, entityClass);
			updateService.postFilter(entityResult.getEntities());
			return WebResponse.builder()
					.entityClass(entityClass)
					.entities(BaseModel.toModels(entityResult.entities))
					.totalData(entityResult.count).filter(request.getFilter()).build();

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ApplicationException(ex);
		}
	}

	public <T extends BaseEntity> FilterResult filterEntities(Filter filter, Class<T> entityClass) {
		final List<T> entities = new ArrayList<>();
		final Map<String, Long> count = new HashMap<>();
		DatabaseProcessor filterDatabaseProcessor = customRepository.createDatabaseProcessor(); 
		try {
			
			List<T> resultList = filterDatabaseProcessor.filterAndSortv2(entityClass, filter);
			entities.addAll(resultList); 
			long resultCount = filterDatabaseProcessor.getRowCount(entityClass, filter);
			count.put("value", resultCount);
		} catch (Exception e) {
			log.error("Error filterEntities: {}", e.getCause());
			count.put("value", 0L);
			e.printStackTrace();
		}
		 
		return FilterResult.builder().entities(CollectionUtil.convertList(entities))
				.count(count.get("value").intValue()).build();
	}

	/**
	 * delete entity
	 * 
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	public WebResponse delete(WebRequest request, HttpServletRequest httpRequest) throws Exception {
		DatabaseProcessor filterDatabaseProcessor = customRepository.createDatabaseProcessor();
		try {
			Map<String, Object> filter = request.getFilter().getFieldsFilter();
			Long id = Long.parseLong(filter.get("id").toString());
			String entityName = request.getEntity().toLowerCase();
			EntityManagementConfig entityConfig = getEntityManagementConfig(entityName);
			BaseEntityUpdateService updateService = entityConfig.getEntityUpdateService();
			return updateService.deleteEntity(id, entityConfig.getEntityClass(), httpRequest);
		} catch (Exception e) {
			throw new ApplicationException(e.getMessage());
		} finally {
			 
		}
	}
 

	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class FilterResult implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7627112916142073122L;
		List<? extends BaseEntity> entities;
		int count;
		
		public <T extends BaseEntity> List<T> getList() {
			return (List<T>) entities;
		}
	}
 

	public <T extends BaseEntity> List<T> findAll(Class<T> _class) {
		List<T> resultList = entityRepository.findAll(_class);

		if (null == resultList) {
			resultList = new ArrayList<T>();
		}

		return resultList;
	}

	public EntityProperty getConfig(WebRequest request, HttpServletRequest httpRequest) {
		try {
			final String key = request.getEntity().toLowerCase();
			Model model = entityManagementPageService.setModel(httpRequest, new ConcurrentModel(), key); 
			 
			return (EntityProperty) ((ConcurrentModel)model).get("entityProperty");
		}catch (Exception e) {
			
			return null;
		}
	}

}
