package com.employee.employee_master.serviceImpl;

import com.employee.employee_master.entity.Department;
import com.employee.employee_master.repository.DepartmentRepo;
import com.employee.employee_master.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    DepartmentRepo departmentRepo;

    @Override
    public Department addDepartment(Department department) {
        return departmentRepo.save(department);
    }

    @Override
    public List<Department> viewDepartmentList() {
        return departmentRepo.findAll();
    }
}
