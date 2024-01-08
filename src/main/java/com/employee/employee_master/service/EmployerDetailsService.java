package com.employee.employee_master.service;

import com.employee.employee_master.dto.EmployerDetailsDTO;
import com.employee.employee_master.entity.*;
import org.springframework.http.ResponseEntity;

public interface EmployerDetailsService {
    ResponseEntity<Object> addEmployeeDetails(EmployerDetailsDTO employerDetailsDTO);
    EmployerDetails getEmployeeDetailsById(Long id);

    ResponseEntity<Object> updateEmployeeDetails(EmployerDetailsDTO employerDetailsDTO);
}
