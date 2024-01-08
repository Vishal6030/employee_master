package com.employee.employee_master.service;

import com.employee.employee_master.entity.Department;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface DepartmentService {
    public ResponseEntity<Object> addDepartment(Department department);
    public Map<String, Object> viewDepartmentList();
}
