package com.employee.employee_master.service;

import com.employee.employee_master.entity.Employee;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EmployeeService {

    public Employee addEmployee(Employee employee);
    public List<Employee> viewAllEmployees();
    public List<Employee> viewEmployeesByCompanyId(Long companyId);
    public Object findEmployeeById(Long empId);
    public Object updateEmployee(Employee employee);

}
