package com.employee.employee_master.service;

import com.employee.employee_master.dto.ChangePasswordDTO;
import com.employee.employee_master.dto.ForgetPasswordDTO;
import com.employee.employee_master.dto.OtpValidateDTO;
import com.employee.employee_master.entity.Employee;
import com.employee.employee_master.entity.OtpValidation;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface EmployeeService {

    public Employee addEmployee(Employee employee);
    public List<Employee> viewAllEmployees();
    public List<Employee> viewEmployeesByCompanyId(Long companyId);
    public Object findEmployeeById(Long empId);
    public Object updateEmployee(Employee employee);
    ResponseEntity<Object> changePassword(String email, ChangePasswordDTO changePasswordDTO);
    ResponseEntity<Object> sentEmail(String email);
    ResponseEntity<Object> forgetPassword(String email, ForgetPasswordDTO forgetPasswordDTO);
    OtpValidation validateOtp(OtpValidateDTO otpValidateDTO);

}
