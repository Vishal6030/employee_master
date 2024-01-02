package com.employee.employee_master.service;

import com.employee.employee_master.dto.ChangePasswordDTO;
import com.employee.employee_master.dto.ForgetPasswordDTO;
import com.employee.employee_master.dto.OtpValidateDTO;
import com.employee.employee_master.dto.SignupRequestDTO;
import com.employee.employee_master.entity.OtpValidation;
import org.springframework.http.ResponseEntity;

public interface SignupService {

    ResponseEntity<Object> employeeSignup(SignupRequestDTO signupRequestDTO);
    ResponseEntity<Object> changePassword(String email, ChangePasswordDTO changePasswordDTO);
    ResponseEntity<Object> sentEmail(String email);
    ResponseEntity<Object> forgetPassword(String email, ForgetPasswordDTO forgetPasswordDTO);
    OtpValidation validateOtp(OtpValidateDTO otpValidateDTO);
}
