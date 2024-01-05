package com.employee.employee_master.controller;

import com.employee.employee_master.dto.EmployerDetailsDTO;
import com.employee.employee_master.dto.ResponseDTO;
import com.employee.employee_master.encryption.AESEncryption;
import com.employee.employee_master.entity.Employee;
import com.employee.employee_master.entity.EmployerDetails;
import com.employee.employee_master.repository.EmployeeRepo;
import com.employee.employee_master.security.JWTUtility;
import com.employee.employee_master.service.EmployerDetailsService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.*;
import java.util.*;

@RestController
@RequestMapping("/employer")
public class EmployerDetailsController {

    @Autowired
    EmployerDetailsService employerDetailsService;
    @Autowired
    EmployeeRepo employeeRepo;
    @Autowired
    JWTUtility jwtUtility;
    @Autowired
    AESEncryption aesEncryption;

    @PostMapping("/addEmployerDetails")
    public ResponseEntity<Object> addEmployerDetails(@RequestBody EmployerDetailsDTO employerDetailsDTO, @RequestHeader("Authorization") String bearerToken) {
        bearerToken = bearerToken.substring(7, bearerToken.length());
        Claims claims = jwtUtility.getAllClaimsFromToken(bearerToken);
        String email = claims.get("email").toString();
        Employee existingUser = employeeRepo.findByWorkEmail(email);
        if (existingUser != null) {
            return employerDetailsService.addEmployeeDetails(employerDetailsDTO);
        } else {
            ResponseDTO response = new ResponseDTO();
            response.setMessage("Unauthorized");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/getEmployerDetailsByEmpId/{id}")
    public ResponseEntity<Object> getEmployerDetails(@PathVariable("id") Long id, @RequestHeader("Authorization") String bearerToken) {
        bearerToken = bearerToken.substring(7, bearerToken.length());
        Claims claims = jwtUtility.getAllClaimsFromToken(bearerToken);
        String email = claims.get("email").toString();
        Employee existingUser = employeeRepo.findByWorkEmail(email);
        if (existingUser != null) {
            EmployerDetails details = employerDetailsService.getEmployeeDetailsById(id);

            if (details != null) {
                EmployerDetailsDTO newResponse = new EmployerDetailsDTO(details.getId(), details.getEmpId(), details.getEmpName(), details.getSalary());
                return new ResponseEntity<>(newResponse, HttpStatus.OK);
            } else {
                ResponseDTO response = new ResponseDTO();
                response.setMessage("Employee details not found!");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } else {
            ResponseDTO response = new ResponseDTO();
            response.setMessage("Unauthorized");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/getEmployerDetailsByEmpIdDecrypt/{id}")
    public ResponseEntity<Object> getEmployerDetailsDecryption(@PathVariable("id") Long id, @RequestHeader("Authorization") String bearerToken) throws Exception {
        bearerToken = bearerToken.substring(7);
        Claims claims = jwtUtility.getAllClaimsFromToken(bearerToken);
        String email = claims.get("email").toString();
        Employee existingUser = employeeRepo.findByWorkEmail(email);
        if (existingUser != null) {
            EmployerDetails details = employerDetailsService.getEmployeeDetailsById(id);

            if (details != null) {
                EmployerDetailsDTO newResponse = new EmployerDetailsDTO(details.getId(), details.getEmpId(), details.getEmpName(),
                        aesEncryption.decrypt(details.getSalary(), details.getSecretKey()));
                return new ResponseEntity<>(newResponse, HttpStatus.OK);
            } else {
                ResponseDTO response = new ResponseDTO();
                response.setMessage("Employee details not found!");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } else {
            ResponseDTO response = new ResponseDTO();
            response.setMessage("Unauthorized");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    private static String keyToString(SecretKey secretKey) {
        byte[] keyBytes = secretKey.getEncoded();
        return Base64.getEncoder().encodeToString(keyBytes);
    }
}
