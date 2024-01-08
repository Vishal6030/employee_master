package com.employee.employee_master.serviceImpl;

import com.employee.employee_master.dto.EmployerDetailsDTO;
import com.employee.employee_master.dto.ResponseDTO;
import com.employee.employee_master.entity.Department;
import com.employee.employee_master.exception.DepartmentNotFoundException;
import com.employee.employee_master.repository.DepartmentRepo;
import com.employee.employee_master.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@CacheConfig(cacheNames = "departs")
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    DepartmentRepo departmentRepo;

    @Override
    @CacheEvict(value = "departmentList",allEntries = true)
    public ResponseEntity<Object> addDepartment(Department department) {
        //This method is used to add department by taking departmentName,ManagerId and cost center(basically turnover).
        try {
            return new ResponseEntity<>(departmentRepo.save(department), HttpStatus.OK);
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            ResponseDTO response = new ResponseDTO();
            response.setMessage("An error occurred while processing the request.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Cacheable(value = "departmentList")
    public Map<String, Object> viewDepartmentList() {
        // This method is used to view all available departments.
        List<Department> departments = departmentRepo.findAll();

        Map<String, Object> response = new HashMap<>();

        if (!departments.isEmpty()) {
            response.put("data", departments);
        } else {
            response.put("message", "No data Found");
        }

        return response;
    }
}
