package com.employee.employee_master.service;

import com.employee.employee_master.dto.ResponseDTO;
import com.employee.employee_master.entity.SignupRequest;
import com.employee.employee_master.repository.SignupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SignupServiceImpl implements SignupService {

    @Autowired
    SignupRepository signupRepository;

    @Override
    public ResponseEntity<Object> employeeSignup(SignupRequest signupRequest) {
        ResponseDTO response = new ResponseDTO();
        SignupRequest existingUser = signupRepository.findByEmail(signupRequest.getEmail());
        if (existingUser != null ) {
            response.setMessage("Email already registered!");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } else {
            if(!signupRequest.getPassword().equals(signupRequest.getConfirmPassword())){
                response.setMessage("Password mismatch!");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }else{
                return new ResponseEntity<>(signupRepository.save(signupRequest), HttpStatus.OK);
            }
        }
    }
}
