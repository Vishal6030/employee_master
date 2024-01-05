package com.employee.employee_master.controller;

import com.employee.employee_master.dto.*;
import com.employee.employee_master.entity.Employee;
import com.employee.employee_master.entity.OtpValidation;
import com.employee.employee_master.security.JWTUtility;
import com.employee.employee_master.service.EmployeeService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/api/v1")
public class EmployeeController {

    @Autowired
    EmployeeService employeeService;
    @Autowired
    JWTUtility jwtUtility;
    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/addEmployee")
    public ResponseEntity<Object> addEmployee(@RequestBody Employee employee){
        return employeeService.addEmployee(employee);
    }

    @GetMapping("/viewAllEmployees")
    public ResponseEntity<Object> viewAllEmployees(){
        return employeeService.viewAllEmployees();
    }

    @GetMapping("/viewAllEmployees/{size}")
    public ResponseEntity<Object> viewAllEmployees(@RequestParam(defaultValue = "0") int page,
                                                         @PathVariable int size){
        Page<Employee> usersPage = employeeService.viewAllEmployeePagination(page, size);
        return ResponseEntity.ok(usersPage);
    }

    @GetMapping("/viewEmployeeListByCompanyId/{companyId}")
    public ResponseEntity<Object> viewEmployeesByCompanyId(@PathVariable Long companyId){
        return employeeService.viewEmployeesByCompanyId(companyId);
    }

    @GetMapping("/findEmployeeById/{empId}")
    public Object findEmployeeById(@PathVariable Long empId){
             return employeeService.findEmployeeById(empId);
    }

    @PutMapping("/updateEmployee")
    public ResponseEntity<Object> updateEmployee(@RequestBody Employee employee){
        return employeeService.updateEmployee(employee);
    }

    //Generate and return JWT token
    @PostMapping("/userLogin")
    public ResponseEntity<Object> userLogin(@RequestBody LoginDTO loginDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginDTO.getEmail(), loginDTO.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            ResponseDTO er = new ResponseDTO();
            er.setMessage("INVALID CREDENTIALS");
            return new ResponseEntity<>(er, HttpStatus.BAD_REQUEST);
        }
        return generateToken(loginDTO.getEmail());
    }

    @PostMapping("/changePassword")
    public ResponseEntity<Object> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO, @RequestHeader("Authorization") String bearerToken) {
        bearerToken = bearerToken.substring(7);
        Claims claims = jwtUtility.getAllClaimsFromToken(bearerToken);
        String email = claims.get("email").toString();
        return employeeService.changePassword(email, changePasswordDTO);
    }

    @GetMapping("/forgetPassword/{email}")
    public ResponseEntity<Object> sendEmail(@PathVariable("email") String email) {
        return employeeService.sentEmail(email);
    }

    @PostMapping("/otpValidate")
    public ResponseEntity<Object> otpValidate(@RequestBody OtpValidateDTO otpValidateDTO) {
        OtpValidation otpValidation = employeeService.validateOtp(otpValidateDTO);
        if(otpValidation!=null){
            return generateToken(otpValidation.getEmail());
        }else{
            ResponseDTO response = new ResponseDTO();
            response.setMessage("Invalid or Expired otp!");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/forgetPassword")
    public ResponseEntity<Object> forgetPassword(@RequestBody ForgetPasswordDTO forgetPasswordDTO, @RequestHeader("Authorization") String bearerToken) {
        bearerToken = bearerToken.substring(7, bearerToken.length());
        Claims claims = jwtUtility.getAllClaimsFromToken(bearerToken);
        String email = claims.get("email").toString();
        return employeeService.forgetPassword(email, forgetPasswordDTO);
    }

    public ResponseEntity<Object> generateToken(String email) {
        HashMap<String, String> data = new HashMap<>();
        data.put("email", email);
        ArrayList<AppUserRole> roles = new ArrayList<>();
        roles.add(AppUserRole.SUPER_ADMIN);

        String token = jwtUtility.createToken(email, roles, data);
        JWTTokenResponseDTO dto = new JWTTokenResponseDTO();
        dto.setToken(token);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }


}
