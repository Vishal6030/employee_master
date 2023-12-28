package com.employee.employee_master.controller;

import com.employee.employee_master.dto.SignupRequestDTO;
import com.employee.employee_master.entity.Employee;
import com.employee.employee_master.entity.SignupRequest;
import com.employee.employee_master.service.EmployeeService;
import com.employee.employee_master.service.SignupService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EmployeeController {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    SignupService signupService;

    @Autowired
    EmployeeService employeeService;

    @GetMapping("/test")
    public String test(){
        return "test";
    }

    @PostMapping("/signup")
    public ResponseEntity<Object> employeeLogin(@RequestBody SignupRequestDTO signupRequestDTO){
        return signupService.employeeSignup(modelMapper.map(signupRequestDTO, SignupRequest.class));
    }

    @PostMapping("/addEmployee")
    public Employee addEmployee(@RequestBody Employee employee){
        return employeeService.addEmployee(employee);
    }

    @GetMapping("/viewAllEmployees")
    public List<Employee> viewAllEmployees(){
        return employeeService.viewAllEmployees();
    }

    @GetMapping("/viewEmployeeListByCompanyId/{companyId}")
    public List<Employee> viewEmployeesByCompanyId(@PathVariable Long companyId){
        return employeeService.viewEmployeesByCompanyId(companyId);
    }

    @GetMapping("/findEmployeeById/{empId}")
    public Object findEmployeeById(@PathVariable Long empId){
        return employeeService.findEmployeeById(empId);
    }

    @PutMapping("/updateEmployee")
    public Object updateEmployee(@RequestBody Employee employee){
        return employeeService.updateEmployee(employee);
    }


}
