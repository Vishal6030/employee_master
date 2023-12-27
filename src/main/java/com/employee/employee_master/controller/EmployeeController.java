package com.employee.employee_master.controller;

import com.employee.employee_master.dto.SignupRequestDTO;
import com.employee.employee_master.entity.SignupRequest;
import com.employee.employee_master.service.SignupService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class EmployeeController {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    SignupService signupService;

    @GetMapping("/test")
    public String test(){
        return "test";
    }

    @PostMapping("/signup")
    public ResponseEntity<Object> employeeLogin(@RequestBody SignupRequestDTO signupRequestDTO){
        return signupService.employeeSignup(modelMapper.map(signupRequestDTO, SignupRequest.class));
    }
}
