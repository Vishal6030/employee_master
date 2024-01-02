package com.employee.employee_master.serviceImpl;

import com.employee.employee_master.entity.SignupRequest;
import com.employee.employee_master.repository.SignupRepository;
import com.employee.employee_master.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserService implements IUserService {

    @Autowired
    SignupRepository signupRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        SignupRequest signupRequest = signupRepository.findByEmail(userName);
        if (signupRequest == null) {
            throw new UsernameNotFoundException("User not found:" + userName);
        }
        return new User(signupRequest.getEmail(), signupRequest.getPassword(), new ArrayList<>());
    }
}
