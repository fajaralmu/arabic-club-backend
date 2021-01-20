package com.fajar.arabicclub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.arabicclub.entity.ApplicationProfile;

public interface AppProfileRepository extends JpaRepository<ApplicationProfile, Long> {
 

	ApplicationProfile findByAppCode(String appCode); 

}
