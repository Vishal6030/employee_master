package com.employee.employee_master.serviceImpl;

import com.employee.employee_master.entity.Department;
import com.employee.employee_master.repository.DepartmentRepo;
import com.employee.employee_master.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "departs")
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    DepartmentRepo departmentRepo;

    @Override
    @CacheEvict(value = "departmentList",allEntries = true)
    public Department addDepartment(Department department) {
        //This method is used to add department by taking departmentName,ManagerId and cost center(basically turnover).
        try {
            return departmentRepo.save(department);
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            throw new RuntimeException("Error adding department: " + errorMessage, e);
        }
    }

    @Override
    @Cacheable(value = "departmentList")
    public List<Department> viewDepartmentList() {
        //This method is used to view all available departments.
        try {
            return departmentRepo.findAll();
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            throw new RuntimeException("Error fetching department list: "+ errorMessage, e);
        }
    }
}
