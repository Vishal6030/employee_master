package com.employee.employee_master.service;

import com.employee.employee_master.dto.*;
import com.employee.employee_master.entity.Employee;
import com.employee.employee_master.entity.OtpValidation;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface EmployeeService {

    ResponseEntity<Object> addEmployee(Employee employee);
    ResponseEntity<Object> viewAllEmployees();
    Page<Employee> viewAllEmployeePagination(int page, int size);
    ResponseEntity<Object> viewEmployeesByCompanyId(Long companyId);
    Object findEmployeeById(Long empId);
    ResponseEntity<Object> updateEmployee(Employee employee);
    ResponseEntity<Object> changePassword(String email, ChangePasswordDTO changePasswordDTO);
    ResponseEntity<Object> sentEmail(String email);
    ResponseEntity<Object> forgetPassword(String email, ForgetPasswordDTO forgetPasswordDTO);
    OtpValidation validateOtp(OtpValidateDTO otpValidateDTO);

}
