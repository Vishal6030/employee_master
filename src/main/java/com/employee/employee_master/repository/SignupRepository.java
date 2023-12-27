package com.employee.employee_master.repository;

import com.employee.employee_master.entity.SignupRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SignupRepository extends JpaRepository<SignupRequest, Long> {

    SignupRequest findByEmail(String email);
}
