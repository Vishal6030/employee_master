package com.employee.employee_master.service;

import com.employee.employee_master.entity.Department;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DepartmentService {
    public Department addDepartment(Department department);
    public List<Department> viewDepartmentList();
}
