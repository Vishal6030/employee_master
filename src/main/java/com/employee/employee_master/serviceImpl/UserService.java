package com.employee.employee_master.serviceImpl;

import com.employee.employee_master.entity.Employee;
import com.employee.employee_master.repository.EmployeeRepo;
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
    EmployeeRepo employeeRepo;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Employee employee = employeeRepo.findByWorkEmail(userName);
        if (employee == null) {
            throw new UsernameNotFoundException("User not found:" + userName);
        }
        return new User(employee.getWorkEmail(), employee.getPassword(), new ArrayList<>());
    }
}
