package com.fajar.arabicclub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.arabicclub.constants.AuthorityType;
import com.fajar.arabicclub.entity.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

	Authority findTop1ByName(AuthorityType type);
 

}
