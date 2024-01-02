package com.employee.employee_master.serviceImpl;

import com.employee.employee_master.dto.*;
import com.employee.employee_master.entity.OtpValidation;
import com.employee.employee_master.entity.SignupRequest;
import com.employee.employee_master.repository.OtpValidationRepository;
import com.employee.employee_master.repository.SignupRepository;
import com.employee.employee_master.service.SignupService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class SignupServiceImpl implements SignupService {

    @Autowired
    SignupRepository signupRepository;
    private PasswordEncoder passwordEncoder;
    @Autowired
    EmailService emailService;
    @Autowired
    OtpValidationRepository otpValidationRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public ResponseEntity<Object> employeeSignup(SignupRequestDTO signupRequestDTO) {
        SignupRequest signupRequest = modelMapper.map(signupRequestDTO, SignupRequest.class);
        ResponseDTO response = new ResponseDTO();
        SignupRequest existingUser = signupRepository.findByEmail(signupRequest.getEmail());
        if (existingUser != null) {
            response.setMessage("Email already registered!");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } else {
            if (!signupRequestDTO.getPassword().equals(signupRequestDTO.getConfirmPassword())) {
                response.setMessage("Password mismatch!");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else {
                String pass = passwordEncoder(signupRequest.getPassword());
                signupRequest.setPassword(pass);
                //  signupRequest.setConfirmPassword(pass);
                return new ResponseEntity<>(signupRepository.save(signupRequest), HttpStatus.OK);
            }
        }
    }

    @Override
    public ResponseEntity<Object> changePassword(String email, ChangePasswordDTO changePasswordDTO) {
        ResponseDTO response = new ResponseDTO();
        SignupRequest existingUser = signupRepository.findByEmail(email);

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
                //  existingUser.setConfirmPassword(pass);
                signupRepository.save(existingUser);
                response.setMessage("Password changes successfully!");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }
    }

    @Override
    public ResponseEntity<Object> sentEmail(String email) {
        SignupRequest existingUser = signupRepository.findByEmail(email);
        ResponseDTO response = new ResponseDTO();
        if (existingUser != null) {
            OtpValidation otpValidation = new OtpValidation();
            String otp = getRandomNumberString();
            otpValidation.setOtp(otp);
            otpValidation.setEmpId(existingUser.getId());
            otpValidation.setEmail(existingUser.getEmail());
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
        SignupRequest existingUser = signupRepository.findByEmail(forgetPasswordDTO.getEmail());
        if (existingUser == null) {
            response.setMessage("Email not registered!");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } else {
            if (!forgetPasswordDTO.getNewPassword().equals(forgetPasswordDTO.getConfirmPassword())) {
                response.setMessage("Password mismatch!");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else {
                existingUser.setPassword(forgetPasswordDTO.getNewPassword());
                signupRepository.save(existingUser);
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
