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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
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
    @Override
    public Employee addEmployee(Employee employee) {
        return employeeRepo.save(employee);
    }

    @Override
    public List<Employee> viewAllEmployees() {
        return employeeRepo.findAll();
    }

    @Override
    public List<Employee> viewEmployeesByCompanyId(Long companyId) {
        return employeeRepo.findByCompanyId(companyId);
    }

    @Override
    public Object findEmployeeById(Long empId) {
        return employeeRepo.findById(empId);
    }

    @Override
    public Object updateEmployee(Employee employee) {
        Optional<Employee> employeeOptional= employeeRepo.findById(employee.getEmpId());
        if(employeeOptional.isPresent()){
            Employee employee1= employeeOptional.get();
            employee1.setFirstName(employee.getFirstName());
            employee1.setLastName(employee.getLastName());
            employee1.setWorkEmail(employee.getWorkEmail());
            employee1.setPersonalEmail(employee.getPersonalEmail());
            employee1.setPhone(employee.getPhone());
            employee1.setHomeNumber(employee.getHomeNumber());
            employee1.setGender(employee.getGender());
            employee1.setStatus(employee.getStatus());
            employee1.setHireDate(employee.getHireDate());
            employee1.setBirthDate(employee.getBirthDate());
            employee1.setCompanyId(employee.getCompanyId());
            employee1.setAddressId1(employee.getAddressId1());
            employee1.setAddressId2(employee.getAddressId2());
            employee1.setDepartmentId(employee.getDepartmentId());
            employee1.setDesignationId(employee.getDesignationId());

            return employeeRepo.save(employee1);
        }
        return "Employee Id not found!";
    }

    @Override
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
        if(otpValidation!=null){
            otpValidation.setValidated(true);
            otpValidationRepository.save(otpValidation);
            System.out.println(otpValidation);
            return otpValidation;
        }else {
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
}
