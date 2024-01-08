package com.employee.employee_master.controller;

import com.employee.employee_master.dto.*;
import com.employee.employee_master.entity.*;
import com.employee.employee_master.repository.*;
import com.employee.employee_master.security.*;
import com.employee.employee_master.service.*;
import io.jsonwebtoken.*;
import org.modelmapper.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1")
public class EmployeeController {

    @Autowired
    ModelMapper modelMapper;
    @Autowired
    EmployeeService employeeService;
    @Autowired
    JWTUtility jwtUtility;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    EmployeeRepo employeeRepo;

    @Value("${api.response.error}")
    private String apiResponse;

    @PostMapping("/addEmployee")
    public ResponseEntity<Object> addEmployee(@RequestBody Employee employee, @RequestHeader("Authorization") String bearerToken) {
        bearerToken = bearerToken.substring(7);
        Claims claims = jwtUtility.getAllClaimsFromToken(bearerToken);
        String email = claims.get("email").toString();
        Employee exist = employeeRepo.findByWorkEmail(email);
        if (exist != null) {
            return employeeService.addEmployee(employee);
        }else{
            ResponseDTO response = new ResponseDTO();
            response.setMessage(apiResponse);
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/viewAllEmployees")
    public ResponseEntity<Object> viewAllEmployees(@RequestHeader("Authorization") String bearerToken) {
        bearerToken = bearerToken.substring(7);
        Claims claims = jwtUtility.getAllClaimsFromToken(bearerToken);
        String email = claims.get("email").toString();
        Employee exist = employeeRepo.findByWorkEmail(email);
        if (exist != null) {
            return employeeService.viewAllEmployees();
        }else{
            ResponseDTO response = new ResponseDTO();
            response.setMessage(apiResponse);
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/viewAllEmployees/{size}")
    public ResponseEntity<Object> viewAllEmployees(@RequestParam(defaultValue = "0") int page,
                                                   @PathVariable int size, @RequestHeader("Authorization") String bearerToken) {
        bearerToken = bearerToken.substring(7);
        Claims claims = jwtUtility.getAllClaimsFromToken(bearerToken);
        String email = claims.get("email").toString();
        Employee exist = employeeRepo.findByWorkEmail(email);
        if (exist != null) {
            Page<Employee> usersPage = employeeService.viewAllEmployeePagination(page, size);
            return ResponseEntity.ok(usersPage);
        }else{
            ResponseDTO response = new ResponseDTO();
            response.setMessage(apiResponse);
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/viewEmployeeListByCompanyId/{companyId}")
    public ResponseEntity<Object> viewEmployeesByCompanyId(@PathVariable Long
                                                                   companyId, @RequestHeader("Authorization") String bearerToken) {
        bearerToken = bearerToken.substring(7);
        Claims claims = jwtUtility.getAllClaimsFromToken(bearerToken);
        String email = claims.get("email").toString();
        Employee exist = employeeRepo.findByWorkEmail(email);
        if (exist != null) {
            return employeeService.viewEmployeesByCompanyId(companyId);
        }else{
            ResponseDTO response = new ResponseDTO();
            response.setMessage(apiResponse);
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/findEmployeeById/{empId}")
    public Object findEmployeeById(@PathVariable Long empId, @RequestHeader("Authorization") String bearerToken) {
        bearerToken = bearerToken.substring(7);
        Claims claims = jwtUtility.getAllClaimsFromToken(bearerToken);
        String email = claims.get("email").toString();
        Employee exist = employeeRepo.findByWorkEmail(email);
        if (exist != null) {
            return employeeService.findEmployeeById(empId);
        }else{
            ResponseDTO response = new ResponseDTO();
            response.setMessage(apiResponse);
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/updateEmployee")
    public ResponseEntity<Object> updateEmployee(@RequestBody Employee
                                                         employee, @RequestHeader("Authorization") String bearerToken) {
        bearerToken = bearerToken.substring(7);
        Claims claims = jwtUtility.getAllClaimsFromToken(bearerToken);
        String email = claims.get("email").toString();
        Employee exist = employeeRepo.findByWorkEmail(email);
        if (exist != null) {
            return employeeService.updateEmployee(employee);
        }else{
            ResponseDTO response = new ResponseDTO();
            response.setMessage(apiResponse);
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
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
    public ResponseEntity<Object> changePassword(@RequestBody ChangePasswordDTO
                                                         changePasswordDTO, @RequestHeader("Authorization") String bearerToken) {
        bearerToken = bearerToken.substring(7, bearerToken.length());
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
        if (otpValidation != null) {
            return generateToken(otpValidation.getEmail());
        } else {
            ResponseDTO response = new ResponseDTO();
            response.setMessage("Invalid or Expired otp!");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/forgetPassword")
    public ResponseEntity<Object> forgetPassword(@RequestBody ForgetPasswordDTO
                                                         forgetPasswordDTO, @RequestHeader("Authorization") String bearerToken) {
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
