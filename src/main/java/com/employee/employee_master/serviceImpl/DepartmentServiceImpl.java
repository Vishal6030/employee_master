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
        return departmentRepo.save(department);
    }

    @Override
    @Cacheable(value = "departmentList")
    public List<Department> viewDepartmentList() {
        return departmentRepo.findAll();
    }
}
