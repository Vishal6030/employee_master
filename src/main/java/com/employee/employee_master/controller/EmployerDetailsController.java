package com.employee.employee_master.controller;

import com.employee.employee_master.dto.EmployerDetailsDTO;
import com.employee.employee_master.dto.ResponseDTO;
import com.employee.employee_master.encryption.AESEncryption;
import com.employee.employee_master.entity.EmployerDetails;
import com.employee.employee_master.entity.SignupRequest;
import com.employee.employee_master.repository.SignupRepository;
import com.employee.employee_master.security.JWTUtility;
import com.employee.employee_master.service.EmployerDetailsService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employer")
public class EmployerDetailsController {

    @Autowired
    EmployerDetailsService employerDetailsService;
    @Autowired
    SignupRepository signupRepository;
    @Autowired
    JWTUtility jwtUtility;
    @Autowired
    AESEncryption aesEncryption;

    @PostMapping("/addEmployerDetails")
    public ResponseEntity<Object> addEmployerDetails(@RequestBody EmployerDetailsDTO employerDetailsDTO, @RequestHeader("Authorization") String bearerToken) {
        bearerToken = bearerToken.substring(7, bearerToken.length());
        Claims claims = jwtUtility.getAllClaimsFromToken(bearerToken);
        String email = claims.get("email").toString();
        SignupRequest existingUser = signupRepository.findByEmail(email);
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
        SignupRequest existingUser = signupRepository.findByEmail(email);
        if (existingUser != null) {
            EmployerDetails details = employerDetailsService.getEmployeeDetailsById(id);

            EmployerDetailsDTO newResponse = new EmployerDetailsDTO(details.getId(), details.getEmpId(), details.getEmpName(), details.getSalary());
            if (details != null) {
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
        bearerToken = bearerToken.substring(7, bearerToken.length());
        Claims claims = jwtUtility.getAllClaimsFromToken(bearerToken);
        String email = claims.get("email").toString();
        SignupRequest existingUser = signupRepository.findByEmail(email);
        if (existingUser != null) {
            EmployerDetails details = employerDetailsService.getEmployeeDetailsById(id);

            EmployerDetailsDTO newResponse = new EmployerDetailsDTO(details.getId(), details.getEmpId(), details.getEmpName(),
                    aesEncryption.decrypt(details.getSalary(), details.getSecretKey()));
            if (details != null) {
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
}
