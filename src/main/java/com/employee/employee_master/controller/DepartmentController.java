package com.employee.employee_master.controller;

import com.employee.employee_master.dto.ResponseDTO;
import com.employee.employee_master.entity.Department;
import com.employee.employee_master.entity.Employee;
import com.employee.employee_master.repository.EmployeeRepo;
import com.employee.employee_master.security.JWTUtility;
import com.employee.employee_master.service.DepartmentService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/depart")
public class DepartmentController {

    @Autowired
    JWTUtility jwtUtility;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    EmployeeRepo employeeRepo;

    @PostMapping("/addDepartment")
    public ResponseEntity<Object> addEmployee(@RequestBody Department department,@RequestHeader("Authorization") String bearerToken){
        bearerToken = bearerToken.substring(7, bearerToken.length());
        Claims claims = jwtUtility.getAllClaimsFromToken(bearerToken);
        String email = claims.get("email").toString();
        Employee existingUser = employeeRepo.findByWorkEmail(email);
        if(existingUser!=null) {
            return new ResponseEntity<>(departmentService.addDepartment(department), HttpStatus.OK);
        }else {
            ResponseDTO response = new ResponseDTO();
            response.setMessage("Unauthorized token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/viewDepartmentList")
    public ResponseEntity<Object> viewDepartmentList(@RequestHeader("Authorization") String bearerToken){
        bearerToken = bearerToken.substring(7, bearerToken.length());
        Claims claims = jwtUtility.getAllClaimsFromToken(bearerToken);
        String email = claims.get("email").toString();
        Employee existingUser = employeeRepo.findByWorkEmail(email);
        if(existingUser!=null) {
            return new ResponseEntity<>(departmentService.viewDepartmentList(), HttpStatus.OK);
        }else {
            ResponseDTO response = new ResponseDTO();
            response.setMessage("Unauthorized token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

}
