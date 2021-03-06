package com.fajar.arabicclub.querybuilder;

import static com.fajar.arabicclub.querybuilder.QueryUtil.getFieldByName;
import static com.fajar.arabicclub.util.EntityUtil.getDeclaredField;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.JoinColumn;
import javax.persistence.Transient;

import org.apache.commons.lang3.SerializationUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import com.fajar.arabicclub.annotation.FormField;
import com.fajar.arabicclub.config.HibernateSessionConfig;
import com.fajar.arabicclub.dto.Filter;
import com.fajar.arabicclub.dto.KeyValue;
import com.fajar.arabicclub.entity.BaseEntity;
import com.fajar.arabicclub.util.EntityUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CriteriaBuilder {

	private final Session hibernateSession;

	private int joinIndex = 1;
	private final boolean allItemExactSearch;

	private final Class<? extends BaseEntity> entityClass;
	private final Filter filter;
	private final Criteria criteria;
	private final Map<String, Object> fieldsFilter;
	private final Map<String, Integer> aliases = new HashMap<>();
	private final List<Field> entityDeclaredFields;

	private static final String THIS = "this";

	public CriteriaBuilder(Session hibernateSession, Class<? extends BaseEntity> _class, Filter filter) {
		this.hibernateSession = hibernateSession;
		this.entityClass = _class;
		this.filter = SerializationUtils.clone(filter);
		this.allItemExactSearch = filter.isExacts();
		this.criteria = this.hibernateSession.createCriteria(entityClass, entityClass.getSimpleName());
		this.fieldsFilter = filter.getFieldsFilter();
		this.entityDeclaredFields = EntityUtil.getDeclaredFields(entityClass);

		this.setJoinColumnAliases();
		log.info("=======CriteriaBuilder Field Filters: {}", fieldsFilter);
	}

	/**
	 * 
	 * @param keyName raw path name
	 * @param fieldValue
	 * @return
	 */
	private Criterion restrictionEquals( String keyName, Object fieldValue) {
		System.out.println("Rest EQ: "+entityClass+" key: "+keyName);
		String entityName = entityClass.getSimpleName();
		String columnName;
		boolean multiKey = keyName.contains(".");
		Field field;
		 
		if (multiKey) {
			System.out.println("multiKey: "+multiKey+" "+keyName);
			Field foreignField = EntityUtil.getDeclaredField(entityClass, keyName.split("\\.")[0]);
			field = EntityUtil.getDeclaredField(foreignField.getType(), keyName.split("\\.")[1]);
			
			if (field.getAnnotation(Transient.class)!=null) return null;
			
			String alias = getAlias(foreignField.getName()) + "." + QueryUtil.getColumnName(field);
			return Restrictions.sqlRestriction(alias + "='" + fieldValue + "'");
		} else {
			field = EntityUtil.getDeclaredField(entityClass, keyName);
			
			if (field==null||field.getAnnotation(Transient.class)!=null) return null;
			
			KeyValue joinColumnResult = QueryUtil.checkIfJoinColumn(keyName,field, field, true);
			
			if (null != joinColumnResult) {
				// process join column
				FormField formField = field.getAnnotation(FormField.class);
				keyName = getAlias(keyName) + "." + formField.optionItemName();
				return Restrictions.sqlRestriction(keyName + "='" + fieldValue + "'");
			}

			columnName = entityName + '.' + keyName;
		}

		if (field.getType().equals(String.class) == false) {
			return nonStringEqualsExp(keyName, fieldValue);
		}

		Object validatedValue = validateFieldValue(field, fieldValue);
		return Restrictions.naturalId().set(columnName, validatedValue);
	}

	private Criterion nonStringEqualsExp(String fieldName, Object value) {

		Criterion sqlRestriction = Restrictions.sqlRestriction("{alias}." + fieldName + " = '" + value + "'");
		return sqlRestriction;
	}

	private Object validateFieldValue(Field field, Object fieldValue) {
		if (null == fieldValue) {
			return 0;
		}

		Class<?> fieldType = field.getType();
		
		if (EntityUtil.isNumericField(field)) {
			if (fieldType.equals(Long.class) || fieldType.equals(long.class)) {
				long value = Long.valueOf(fieldValue.toString());
				return value;
			}
			if (fieldType.equals(Integer.class) || fieldType.equals(int.class)) {
				long value = Integer.valueOf(fieldValue.toString());
				return value;
			}
		}
		return fieldValue;
	}

	/**
	 * 
	 * @param aliasName must match fieldName of entityClass
	 */
	private void setCurrentAlias(String aliasName) {
		 
		if (null == aliasName || aliases.get(aliasName) != null)
			return;

		if (aliasName.equals(THIS)) {
			// this.currentAlias = "this_";
		} else {

			Field correspondingField = EntityUtil.getDeclaredField(entityClass, aliasName);
			if (null == correspondingField) {
				return;
			}
			aliases.put(aliasName, joinIndex);
			criteria.createAlias(entityClass.getSimpleName() + "." + aliasName, aliasName, JoinType.LEFT_OUTER_JOIN);
			if (aliasName.length() > 10) {
				aliasName = aliasName.substring(0, 10);
			}

			// this.currentAlias = aliasName.toLowerCase() + joinIndex + '_';

			joinIndex++;
		}
	}

	private String getAlias(final String aliasName) {
		if (THIS.equals(aliasName) || aliasName.toLowerCase().equals(entityClass.getSimpleName().toLowerCase())) {
			return "this_";
		}
		String tableName = aliasName;
		if (aliasName.length() > 10) {
			tableName = aliasName.substring(0, 10);
		}
		return tableName.toLowerCase() + aliases.get(aliasName) + "_";
	}

	private void setJoinColumnAliases() {
		List<Field> joinColumns = QueryUtil.getJoinColumnFields(entityClass);
		for (int i = 0; i < joinColumns.size(); i++) {
			setCurrentAlias(joinColumns.get(i).getName());
		}

	}

	public Criteria createRowCountCriteria() {

		Criteria criteria = createCriteria(true);
		criteria.setProjection(Projections.rowCount());
		return criteria;
	}

	public Criteria createCriteria() {
		return createCriteria(false);
	}

	private Criteria createCriteria(boolean onlyRowCount) {

		String entityName = entityClass.getSimpleName();
		setCurrentAlias(THIS);

		for (final String rawKey : fieldsFilter.keySet()) {
			setCurrentAlias(THIS);

			log.info("##" + rawKey + ":" + fieldsFilter.get(rawKey));
			if (fieldsFilter.get(rawKey) == null)
				continue;

			String currentKey = rawKey; 
			log.info("Raw key: {} Now KEY: {}", rawKey, currentKey);
			// check if date
			Criterion dateFilterSql = getDateFilter(rawKey, currentKey);

			if (null != dateFilterSql) {
				log.info(" {} is date ", rawKey);
				addCriteria(dateFilterSql);
				continue;
			}

			final boolean multiKey = rawKey.contains(".");
			final Field field, fieldRelativeToEntityClass;
			if (multiKey) {
				fieldRelativeToEntityClass = getDeclaredField(entityClass, rawKey.split("\\.")[0]);
				field = getDeclaredField(fieldRelativeToEntityClass.getType(), rawKey.split("\\.")[1]);
			} else {
				field = fieldRelativeToEntityClass = getFieldByName(currentKey, entityDeclaredFields);
			}

			if (field == null) {
				log.warn("Field Not Found :" + currentKey + " !");
				continue;
			}

			String fieldName = field.getName();
			KeyValue<String, String> joinColumnResult = QueryUtil.checkIfJoinColumn(currentKey, fieldRelativeToEntityClass,field, false);
			System.out.println("joinColumnResult: "+joinColumnResult);
			
			//TODO: process exact with join column
			if (null != joinColumnResult) {
				if (joinColumnResult.isValid()) {
					Class<?> _class = field.getType();
					if (joinColumnResult.isMultiKey()) {
						fieldName = joinColumnResult.getKey();
						_class = fieldRelativeToEntityClass.getType();
					}
					if (allItemExactSearch) {
						Criterion eq = restrictionEquals( fieldName+ "." + joinColumnResult.getValue(), fieldsFilter.get(rawKey));
						addCriteria(eq);
					} else {
						Criterion like = restrictionLike( fieldName+ "." + joinColumnResult.getValue(), _class,
								fieldsFilter.get(rawKey));
						addCriteria(like);
					}
				} else {
					continue;
				}
			} else {
				if (allItemExactSearch) {
					Criterion eq = restrictionEquals(currentKey, fieldsFilter.get(rawKey));
					addCriteria(eq); 
				} else {
					Criterion like = (restrictionLike(entityName + "." + currentKey, entityClass, fieldsFilter.get(rawKey)));
					addCriteria(like);
				}
			}

		}

		if (onlyRowCount) {
			return criteria;
		}
		
		addOrderOffsetLimit(filter);  
		return criteria;

	}
	
	private void addCriteria(Criterion c) {
		if (null != c ) {
			criteria.add(c );
		}
	}

	private void addOrderOffsetLimit(Filter filter) {
		if (filter.getLimit() > 0) {
			criteria.setMaxResults(filter.getLimit());
			if (filter.getPage() > 0) {
				criteria.setFirstResult(filter.getPage() * filter.getLimit());
			}
		}
		String orderBy = extractOrderByKey(filter);
		if (null != orderBy) {

			Order order;
			if (filter.getOrderType().toLowerCase().equals("desc")) {
				order = Order.desc(orderBy);
			} else {
				order = Order.asc(orderBy);
			}

			criteria.addOrder(order);
		}

	}

	private String extractOrderByKey(Filter filter) {
		String orderBy = filter.getOrderBy();
		Field field = EntityUtil.getDeclaredField(entityClass, orderBy);
		if (null == field) {
			log.info("{} is not {} field", orderBy, entityClass);
			return null;
		}
		if (field.getAnnotation(Transient.class) != null) {
			log.info("{} is TRANSIENT FIELD", orderBy);
			return null;
		}
		if (field.getAnnotation(JoinColumn.class) != null) {
			Class modelClass = BaseEntity.getModelClass(entityClass);
			Field modelField = EntityUtil.getDeclaredField(modelClass, field.getName());
			if (null == modelField) {
				log.info("model field : {} NOT FOUND", field.getName());
				return null;
			}
			FormField formField = modelField.getAnnotation(FormField.class);
			if (null == formField) {
				log.info("formField : {} NOT FOUND", modelField.getName());
				return null;
			}
			String foreginFieldName = formField.optionItemName();
			Field foreignField = EntityUtil.getDeclaredField(field.getType(), foreginFieldName);
			return orderBy+"."+foreginFieldName;
		}
		return orderBy;
	}

	private Criterion restrictionLike(final String fieldName, Class<?> _class, Object value) {
		String extractedFieldName = fieldName;
		if (fieldName.contains(".") && fieldName.split("\\.").length == 2) {
			extractedFieldName = fieldName.split("\\.")[1];
		}
		System.out.println("fieldName: "+fieldName);
		Field field = EntityUtil.getDeclaredField(_class, extractedFieldName);
		System.out.println("_class: "+_class);
		
		if (field==null || field.getAnnotation(Transient.class)!=null) return null;
		
		boolean stringTypeField = field.getType().equals(String.class);
		Object validatedValue = validateFieldValue(field, value); 
		if (!stringTypeField) {

			return nonStringLikeExp(fieldName,  _class, validatedValue);
		}

		Criterion likeExp = Restrictions.ilike(fieldName, String.valueOf(validatedValue), MatchMode.ANYWHERE);

		return likeExp;

	}

	private Criterion nonStringLikeExp(String fieldName, Class<?> _class,Object value) {
		String extractedFieldName = fieldName;
		String tableName = THIS;
		if (fieldName.contains(".") && fieldName.split("\\.").length == 2) {
			tableName = getAlias(fieldName.split("\\.")[0]);
			extractedFieldName = fieldName.split("\\.")[1];
			System.out.println("tableName: "+tableName);
		}
		System.out.println("fieldName: "+fieldName);
		Field field = EntityUtil.getDeclaredField(_class, extractedFieldName);
		
		if (field.getAnnotation(Transient.class)!=null) return null;
		String columnName = QueryUtil.getColumnName(field);
		 
		return likeRestriction(tableName+"."+columnName, value);
	}

	private Criterion likeRestriction(String field, Object value) {
		Criterion sqlRestriction = null;
		if (HibernateSessionConfig.isMysql()) {
			sqlRestriction = Restrictions.sqlRestriction(field + " LIKE '%" + value + "%'");
		} else if (HibernateSessionConfig.isPostgres()) {
			sqlRestriction = Restrictions.sqlRestriction(field+ "::varchar(255) ILIKE '%" + value + "%'");
		}
		

		return sqlRestriction;
	}

	private Criterion getDateFilter(String rawKey, String key ) {
		boolean dayFilter = rawKey.endsWith(QueryUtil.DAY_SUFFIX);
		boolean monthFilter = rawKey.endsWith(QueryUtil.MONTH_SUFFIX);
		boolean yearFilter = rawKey.endsWith(QueryUtil.YEAR_SUFFIX);

		if (dayFilter || monthFilter || yearFilter) {

			String fieldName = key;
			String mode = QueryUtil.FILTER_DATE_DAY;

			if (dayFilter) {
				fieldName = key.replace(QueryUtil.DAY_SUFFIX, "");
				mode = QueryUtil.FILTER_DATE_DAY;

			} else if (monthFilter) {
				fieldName = key.replace(QueryUtil.MONTH_SUFFIX, "");
				mode = QueryUtil.FILTER_DATE_MON1TH;

			} else if (yearFilter) {
				fieldName = key.replace(QueryUtil.YEAR_SUFFIX, "");
				mode = QueryUtil.FILTER_DATE_YEAR;

			}
			Field field = getDeclaredField(entityClass, fieldName);
			if (null ==field ) {
				return null;
			}
			Object value = fieldsFilter.get(key);
			String columnName = QueryUtil.getColumnName(field);
			log.info("mode: {}. value: {}", mode, value); 
			return dateRestriction(mode, getAlias(entityClass.getSimpleName())+"."+columnName , value);
		}

		return null;
	}

	private Criterion dateRestriction(String mode, String field, Object value) {
		if (HibernateSessionConfig.isMysql()) {
			return Restrictions.sqlRestriction(mode + "(" + field + ")=" + value);
		}
		if (HibernateSessionConfig.isPostgres()) {
			return Restrictions.sqlRestriction("date_part('"+mode+"', " + field+ ")=" + value);

		}
		return null;
	}

	 

}
