package com.employee.employee_master.serviceImpl;

import com.employee.employee_master.dto.ChangePasswordDTO;
import com.employee.employee_master.dto.ForgetPasswordDTO;
import com.employee.employee_master.dto.OtpValidateDTO;
import com.employee.employee_master.dto.ResponseDTO;
import com.employee.employee_master.entity.Employee;
import com.employee.employee_master.entity.OtpValidation;
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
import java.util.List;
import java.util.Optional;
import java.util.Random;

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
    public Employee addEmployee(Employee employee) {
        return addAndUpdateEmployeeProcedure(employee, "add_employee");
    }

    @Override
    @Cacheable(value = "allEmployees")
    public List<Employee> viewAllEmployees() {
        return employeeRepo.findAll();
    }

    @Override
    @Cacheable(value = "#empCompanyId")
    public List<Employee> viewEmployeesByCompanyId(Long companyId) {
        return employeeRepo.findByCompanyId(companyId);
    }

    @Override
    @Cacheable(value = "#empId")
    public Object findEmployeeById(Long empId) {
        return employeeRepo.findById(empId);
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "'allEmployees'", allEntries = true),
                    @CacheEvict(value = "'#empCompanyId'", allEntries = true),
                    @CacheEvict(value = "'#empId'", allEntries = true)
            }
    )
    public Object updateEmployee(Employee employee) {
        Optional<Employee> employeeOptional = employeeRepo.findById(employee.getEmpId());
        if (employeeOptional.isPresent()) {
            return addAndUpdateEmployeeProcedure(employee, "update_employee");
        } else {
            return "Employee Id not found!";
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
        Random random = new Random();
        int number = random.nextInt(999999);
        return String.format("%06d", number);
    }

    public Employee addAndUpdateEmployeeProcedure(Employee employee, String procedure) {
        StoredProcedureQuery query = entityManager.createNamedStoredProcedureQuery(procedure);
        if (procedure.equalsIgnoreCase("update_employee")) {
            query.setParameter("p_emp_id", employee.getAddressId1());
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
        return employeeRepo.findByEmpId(employee.getEmpId());
    }
}
