package com.employee.employee_master.controller;

import com.employee.employee_master.entity.Department;
import com.employee.employee_master.entity.Employee;
import com.employee.employee_master.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/depart")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @PostMapping("/addDepartment")
    public Department addEmployee(@RequestBody Department department){
        return departmentService.addDepartment(department);
    }

    @GetMapping("/viewDepartmentList")
    public List<Department> viewDepartmentList(){
        return departmentService.viewDepartmentList();
    }

}
