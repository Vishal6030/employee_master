package com.employee.employee_master.serviceImpl;

import com.employee.employee_master.dto.*;
import com.employee.employee_master.entity.*;
import com.employee.employee_master.repository.*;
import com.employee.employee_master.service.*;
import jakarta.persistence.*;
import org.modelmapper.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.crypto.password.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.time.*;
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
    public ResponseEntity<Object> viewAllEmployees() {
        // This method is giving a complete list of employees.
        List<Employee> employeeList = employeeRepo.findAll();
        if (!employeeList.isEmpty()) {
            return new ResponseEntity<>(employeeList, HttpStatus.OK);
        } else {
            ResponseDTO response = new ResponseDTO();
            response.setMessage("Employee not found!");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    @Cacheable(value = "'allUsers'")
    public Page<Employee> viewAllEmployeePagination(int page, int size) { //The url has been changed due to pagination
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Order.desc("emp_id"))); // Default to page 0
        return employeeRepo.findAll(pageable);
    }

    @Override
    @Cacheable(value = "#empCompanyId")
    public ResponseEntity<Object> viewEmployeesByCompanyId(Long companyId) {
        //This method is used to find employee List by companyId.
        List<Employee> employee = employeeRepo.findByCompanyId(companyId);
        if (!employee.isEmpty()) {
            return new ResponseEntity<>(employee, HttpStatus.OK);
        } else {
            ResponseDTO response = new ResponseDTO();
            response.setMessage("No Employee found!");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    @Cacheable(value = "#empId")
    public ResponseEntity<Object> findEmployeeById(Long empId) {
        //This method is used to find employee by employeeId.
        Optional<Employee> employee = employeeRepo.findById(empId);

        if (employee.isPresent()) {
            return new ResponseEntity<>(employee.get(), HttpStatus.OK);
        } else {
            ResponseDTO response = new ResponseDTO();
            response.setMessage("Employee Not found with given Id ");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
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

    public ResponseEntity<Object> addAndUpdateEmployeeProcedure(Employee employee, String procedureName) {
        //This method is compatible to add and update both from a single method.
        //pass procedure name as an argument
        StoredProcedureQuery query = entityManager.createNamedStoredProcedureQuery(procedureName);//
        ResponseDTO response = new ResponseDTO();
        if (procedureName.equalsIgnoreCase("update_employee")) {
            query.setParameter("p_emp_id", employee.getEmpId());
            response.setMessage("Employee Updated Successfully");
        } else {
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
