package com.employee.employee_master.service;

import com.employee.employee_master.entity.SignupRequest;
import org.springframework.http.ResponseEntity;

public interface SignupService {

    ResponseEntity<Object> employeeSignup(SignupRequest signupRequest);
}
