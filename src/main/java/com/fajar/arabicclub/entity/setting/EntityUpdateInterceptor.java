package com.fajar.arabicclub.entity.setting;

import com.fajar.arabicclub.entity.BaseEntity;

public interface EntityUpdateInterceptor<T extends BaseEntity> {
	
	public T preUpdate(T baseEntity) ;

	
	/**
	 * =======================================
	 *          Static Methods
	 * =======================================
	 */
//	public static EntityUpdateInterceptor menuInterceptor() { 
//		return new EntityUpdateInterceptor() {
//			
//			@Override
//			public void preUpdate(BaseEntity baseEntity) { 
//				Menu menu = (Menu) baseEntity;
//				if(menu.getUrl().startsWith("/") == false) {
//					menu.setUrl("/"+menu.getUrl());
//				}
//			}
//		};
//	}
	
}
