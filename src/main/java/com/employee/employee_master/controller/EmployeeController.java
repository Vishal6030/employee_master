package com.employee.employee_master.controller;

import com.employee.employee_master.dto.*;
import com.employee.employee_master.entity.Employee;
import com.employee.employee_master.entity.OtpValidation;
import com.employee.employee_master.security.JWTUtility;
import com.employee.employee_master.service.EmployeeService;
import com.employee.employee_master.service.SignupService;
import io.jsonwebtoken.Claims;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
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

    @Autowired
    JWTUtility jwtUtility;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/test")
    public String test(){
        return "test";
    }

    @PostMapping("/signup")
    public ResponseEntity<Object> employeeLogin(@RequestBody SignupRequestDTO signupRequestDTO){

        return signupService.employeeSignup(signupRequestDTO);
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

    //Generate and return JWT token
    @PostMapping("/userLogin")
    public ResponseEntity<Object> userLogin(@RequestBody LoginDTO loginDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginDTO.getEmail(), loginDTO.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (BadCredentialsException e) {
            ResponseDTO er = new ResponseDTO();
            HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
            er.setMessage("INVALID CREDENTIALS");
            return new ResponseEntity<>(er, httpStatus);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return generateToken(loginDTO.getEmail());
    }

    //implement jwt token authentication
    @PostMapping("/changePassword")
    public ResponseEntity<Object> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO, @RequestHeader("Authorization") String bearerToken) {
        //ResponseDTO er = new ResponseDTO();
        bearerToken = bearerToken.substring(7, bearerToken.length());
        Claims claims = jwtUtility.getAllClaimsFromToken(bearerToken);
        String email = claims.get("email").toString();
        System.out.println("email:" + email);
        return signupService.changePassword(email, changePasswordDTO);
    }

    //This controller is for otp validation
    @GetMapping("/forgetPassword/{email}")
    public ResponseEntity<Object> sendEmail(@PathVariable("email") String email) {
        return signupService.sentEmail(email);
    }

    @PostMapping("/otpValidate")
    public ResponseEntity<Object> otpValidate(@RequestBody OtpValidateDTO otpValidateDTO) {
        OtpValidation otpValidation = signupService.validateOtp(otpValidateDTO);
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
        return signupService.forgetPassword(email, forgetPasswordDTO);
    }

    //add email id in claims at the time of generating JWT token
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
