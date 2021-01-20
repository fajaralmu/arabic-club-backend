package com.fajar.arabicclub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.arabicclub.entity.RegisteredRequest;

public interface RegisteredRequestRepository extends JpaRepository<RegisteredRequest, Long> {

	RegisteredRequest findTop1ByRequestId(String requestId);

}
