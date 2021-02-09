package com.fajar.arabicclub.service.entity;

import static com.fajar.arabicclub.util.MvcUtil.constructCommonModel;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.math3.stat.descriptive.summary.Product;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.fajar.arabicclub.dto.WebResponse;
import com.fajar.arabicclub.dto.model.BaseModel;
import com.fajar.arabicclub.entity.BaseEntity;
import com.fajar.arabicclub.entity.setting.EntityManagementConfig;
import com.fajar.arabicclub.entity.setting.EntityProperty;
import com.fajar.arabicclub.repository.EntityRepository;
import com.fajar.arabicclub.util.CollectionUtil;
import com.fajar.arabicclub.util.EntityPropertyBuilder;
import com.fajar.arabicclub.util.EntityUtil; 

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EntityManagementPageService {

	@Autowired
	private EntityRepository entityRepository;
	 
	public Model setModel(HttpServletRequest request, Model model, String key) throws Exception {

		EntityManagementConfig entityConfig = entityRepository.getConfig(key);

		if (null == entityConfig) {
			throw new IllegalArgumentException("Invalid entity key (" + key + ")!");
		}

		HashMap<String, List<?>> additionalListObject = getFixedListObjects(entityConfig.getEntityClass());
		EntityProperty entityProperty = EntityPropertyBuilder.createEntityProperty(entityConfig.getModelClass(),
				additionalListObject);
		model = constructCommonModel(request, entityProperty, model, entityConfig.getEntityClass().getSimpleName(),
				"management"); 
		 
		return model;
	}

	private HashMap<String, List<?>> getFixedListObjects(Class<? extends BaseEntity> entityClass) {
		HashMap<String, List<?>> listObject = new HashMap<>();
		try {
			List<Field> fixedListFields = EntityUtil.getFixedListFields(entityClass);

			for (int i = 0; i < fixedListFields.size(); i++) {
				Field field = fixedListFields.get(i);
				Class<? extends BaseEntity> type;

				if (CollectionUtil.isCollectionOfBaseEntity(field)) {
					Type classType = CollectionUtil.getGenericTypes(field)[0];
					type = (Class<? extends BaseEntity>) classType;

				} else {
					type = (Class<? extends BaseEntity>) field.getType();
				}
				log.info("(populating fixed list values) findALL FOR type: {}", type);
				List<? extends BaseEntity> list = entityRepository.findAll(type);
				
				
				listObject.put(field.getName(), BaseModel.toModels(list));
//				listObject.put(field.getName(), CollectionUtil.convertList(list));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listObject;
	}

	 
	public WebResponse getManagementPages() {
		
		List<Object> result = new ArrayList<>(); 
		
		result.add(entityRepository.getConfig("lesson").setIconClassName("fas fa-book"));
		result.add(entityRepository.getConfig("lessoncategory").setIconClassName("fas fa-tags"));  
		result.add(entityRepository.getConfig("images").setIconClassName("fas fa-book"));
		result.add(entityRepository.getConfig("imagecategory").setIconClassName("fas fa-tags")); 
		result.add(entityRepository.getConfig("videos").setIconClassName("fas fa-book"));
		result.add(entityRepository.getConfig("videocategory").setIconClassName("fas fa-tags")); 
		result.add(entityRepository.getConfig("documents").setIconClassName("fas fa-book")); 
		result.add(entityRepository.getConfig("documentcategory").setIconClassName("fas fa-tags")); 
		result.add(entityRepository.getConfig("quiz").setIconClassName("fas fa-book"));  
		result.add(entityRepository.getConfig("user").setIconClassName("fas fa-users"));  
		
		return WebResponse.builder().generalList(result).build();
	}
	  void addConfig(List<Object> result, Class<?> _class, String iconClassName) {
		  try {
			  result.add(entityRepository.getConfig(_class.getSimpleName().toLowerCase()).setIconClassName(iconClassName));
		  }catch (Exception e) {
			  log.error("Error getting config for : {}",_class );
			  e.printStackTrace();
		}
	}

}
