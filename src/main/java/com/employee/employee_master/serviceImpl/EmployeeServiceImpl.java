package com.employee.employee_master.serviceImpl;

import com.employee.employee_master.dto.ChangePasswordDTO;
import com.employee.employee_master.dto.ForgetPasswordDTO;
import com.employee.employee_master.dto.OtpValidateDTO;
import com.employee.employee_master.dto.ResponseDTO;
import com.employee.employee_master.entity.Employee;
import com.employee.employee_master.entity.OtpValidation;
import com.employee.employee_master.exception.EmployeeNotFoundException;
import com.employee.employee_master.repository.EmployeeRepo;
import com.employee.employee_master.repository.OtpValidationRepository;
import com.employee.employee_master.service.EmployeeService;
import jakarta.persistence.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@Service
@CacheConfig(cacheNames = "employees")
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    EmployeeRepo employeeRepo;
    @Autowired
    EmailService emailService;
    @Autowired
    OtpValidationRepository otpValidationRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    EntityManager entityManager;

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "allEmployees", allEntries = true),
                    @CacheEvict(value = "#empCompanyId", allEntries = true),
                    @CacheEvict(value = "#empId", allEntries = true)
            }
    )
    public ResponseEntity<Object> addEmployee(Employee employee) {
        //This method is used to add employee through procedure.
        //The below method(addAndUpdateEmployeeProcedure) is compatible to add and update both from a single method.
        return addAndUpdateEmployeeProcedure(employee, "add_employee");
    }

    @Override
    @Cacheable(value = "allEmployees")
    public List<Employee> viewAllEmployees() {
        // This method is giving a complete list of employees.
        List<Employee> employeeList= employeeRepo.findAll();
        if(employeeList.isEmpty()){
            throw new EmployeeNotFoundException("No employees Found!");
        }
        return employeeRepo.findAll();
    }

    @Override
    @Cacheable(value = "#empCompanyId")
    public List<Employee> viewEmployeesByCompanyId(Long companyId) {
        //This method is used to find employee List by companyId.
        Map<String, String> response = new HashMap<>();
        List<Employee> employee=employeeRepo.findByCompanyId(companyId);

        if (!employee.isEmpty()) {
            return employee;
        } else {
            throw new EmployeeNotFoundException("No Employee found with given ID");
        }
    }

    @Override
    @Cacheable(value = "#empId")
    public Object findEmployeeById(Long empId) {
        //This method is used to find employee by employeeId.
        Map<String, String> response = new HashMap<>();
        Optional<Employee> employee=employeeRepo.findById(empId);

        if(employee.isPresent()){
            return employee;
        }else{
            response.put("message :", "Employee Not found with given Id ");
            return response;
        }
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "'allEmployees'", allEntries = true),
                    @CacheEvict(value = "'#empCompanyId'", allEntries = true),
                    @CacheEvict(value = "'#empId'", allEntries = true)
            }
    )
    public ResponseEntity<Object> updateEmployee(Employee employee) {
        //This method is used to update details of an employee.
        ResponseDTO responseDTO = new ResponseDTO();
        Optional<Employee> employeeOptional = employeeRepo.findById(employee.getEmpId());
        if (employeeOptional.isPresent()) {
            return addAndUpdateEmployeeProcedure(employee, "update_employee");
        } else {
            responseDTO.setMessage("Employee Id not found!");
            return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "'allEmployees'", allEntries = true),
                    @CacheEvict(value = "'#empCompanyId'", allEntries = true),
                    @CacheEvict(value = "'#empId'", allEntries = true)
            }
    )
    public ResponseEntity<Object> changePassword(String email, ChangePasswordDTO changePasswordDTO) {
        //This method is used to change/set password by taking token.
        ResponseDTO response = new ResponseDTO();
        Employee existingUser = employeeRepo.findByWorkEmail(email);

        if (existingUser == null) {
            response.setMessage("Email not registered!");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } else {
            if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
                response.setMessage("Password mismatch!");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else {
                String pass = passwordEncoder(changePasswordDTO.getNewPassword());
                existingUser.setPassword(pass);
                employeeRepo.save(existingUser);
                response.setMessage("Password changes successfully!");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }
    }

    @Override
    public ResponseEntity<Object> sentEmail(String email) {
        //This method is used to send OTP through email.
        Employee existingUser = employeeRepo.findByWorkEmail(email);
        ResponseDTO response = new ResponseDTO();
        if (existingUser != null) {
            OtpValidation otpValidation = new OtpValidation();
            String otp = getRandomNumberString();
            otpValidation.setOtp(otp);
            otpValidation.setEmpId(existingUser.getEmpId());
            otpValidation.setEmail(existingUser.getWorkEmail());
            LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(15); // Set expiration time to 15 minutes
            otpValidation.setExpirationTime(expirationTime);
            otpValidationRepository.save(otpValidation);
            response.setMessage("Otp sent to email successfully!");
            emailService.sendSimpleMail(email, "This is your otp for verification", otp);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.setMessage("Incorrect Email!");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<Object> forgetPassword(String email, ForgetPasswordDTO forgetPasswordDTO) {
        //This method is used to reset password without taking token (without login).
        ResponseDTO response = new ResponseDTO();
        Employee existingUser = employeeRepo.findByWorkEmail(email);
        if (existingUser == null) {
            response.setMessage("Email not registered!");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } else {
            if (!forgetPasswordDTO.getNewPassword().equals(forgetPasswordDTO.getConfirmPassword())) {
                response.setMessage("Password mismatch!");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else {
                existingUser.setPassword(forgetPasswordDTO.getNewPassword());
                employeeRepo.save(existingUser);
                response.setMessage("Password changes successfully!");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }
    }

    @Override
    public OtpValidation validateOtp(OtpValidateDTO otpValidateDTO) {
        //This method is verifying the OTP from User.
        LocalDateTime now = LocalDateTime.now();
        OtpValidation otpValidation = otpValidationRepository.
                findByEmailAndOtpAndValidatedFalseAndExpirationTimeAfter(otpValidateDTO.getEmail(), otpValidateDTO.getOtp(), now);
        if (otpValidation != null) {
            otpValidation.setValidated(true);
            otpValidationRepository.save(otpValidation);
            System.out.println(otpValidation);
            return otpValidation;
        } else {
            return null;
        }
    }

    public String passwordEncoder(String password) {
        System.out.println("pass:" + password);
        String encodedPassword = passwordEncoder.encode(password);
        System.out.println("password:" + encodedPassword);
        return encodedPassword;
    }

    public String getRandomNumberString() {
        //This method is assigning some random value for encryption.
        Random random = new Random();
        int number = random.nextInt(999999);
        return String.format("%06d", number);
    }

    public ResponseEntity<Object> addAndUpdateEmployeeProcedure(Employee employee, String procedure) {
        //This method is compatible to add and update both from a single method.
        StoredProcedureQuery query = entityManager.createNamedStoredProcedureQuery(procedure);
        ResponseDTO response = new ResponseDTO();
        if (procedure.equalsIgnoreCase("update_employee")) {
            query.setParameter("p_emp_id", employee.getEmpId());
            response.setMessage("Employee Updated Successfully");
        }else{
            response.setMessage("Employee Added Successfully");
        }
        query.setParameter("p_address_id1", employee.getAddressId1());
        query.setParameter("p_address_id2", employee.getAddressId2());
        query.setParameter("p_birth_date", employee.getBirthDate());
        query.setParameter("p_company_id", employee.getCompanyId());
        query.setParameter("p_department_id", employee.getDepartmentId());
        query.setParameter("p_designation_id", employee.getDesignationId());
        query.setParameter("p_first_name", employee.getFirstName());
        query.setParameter("p_last_name", employee.getLastName());
        query.setParameter("p_gender", employee.getGender());
        query.setParameter("p_hire_date", employee.getHireDate());
        query.setParameter("p_home_number", employee.getHomeNumber());
        query.setParameter("p_personal_email", employee.getPersonalEmail());
        query.setParameter("p_phone", employee.getPhone());
        query.setParameter("p_status", employee.getStatus());
        query.setParameter("p_work_email", employee.getWorkEmail());
        String passwordEncoded = passwordEncoder(employee.getPassword());
        query.setParameter("p_password", passwordEncoded);
        employee.setPassword(passwordEncoded);
        query.execute();
        entityManager.clear();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
