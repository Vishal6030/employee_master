package com.employee.employee_master.serviceImpl;

import com.employee.employee_master.dto.*;
import com.employee.employee_master.encryption.*;
import com.employee.employee_master.entity.*;
import com.employee.employee_master.repository.*;
import com.employee.employee_master.service.*;
import org.modelmapper.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;

import javax.crypto.*;

@Service
public class EmployerDetailsServiceImpl implements EmployerDetailsService {

    @Autowired
    EmployerDetailsRepo employerDetailsRepo;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    AESEncryption aesEncryption;

    @Override
    public ResponseEntity<Object> addEmployeeDetails(EmployerDetailsDTO employerDetailsDTO) {
        //This method is used to add the employee details (i.e. id,name and salary details).
        try {
            EmployerDetails emp = modelMapper.map(employerDetailsDTO, EmployerDetails.class);

            if (emp != null) {
                SecretKey key = aesEncryption.generateSecretKey();
                String encryptedSalary = aesEncryption.encrypt(emp.getSalary(), key);
                emp.setSalary(encryptedSalary);
                emp.setSecretKey(key);
                employerDetailsRepo.save(emp);

                return new ResponseEntity<>(new EmployerDetailsDTO(emp.getId(), emp.getEmpId(), emp.getEmpName(), emp.getSalary()), HttpStatus.OK);
            } else {
                ResponseDTO response = new ResponseDTO();
                response.setMessage("Error!");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            // Log the exception
            ex.printStackTrace();

            ResponseDTO response = new ResponseDTO();
            response.setMessage("An error occurred while processing the request.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public EmployerDetails getEmployeeDetailsById(Long id) {
        //This method is used to know salary details of an employee.
        try {
            EmployerDetails optionalDetails = employerDetailsRepo.findByEmpId(id);
            if (optionalDetails != null) {
                return optionalDetails;
            } else {
                // Handling the case where no employee details were found for the given id.
                return null;
            }
        } catch (Exception ex) {
            // Log the exception
            ex.printStackTrace();
            throw new RuntimeException("Error retrieving employee details by id: " + id);
        }
    }

    @Override
    public ResponseEntity<Object> updateEmployeeDetails(EmployerDetailsDTO employerDetailsDTO) {
        EmployerDetails existingDetails = employerDetailsRepo.findByEmpId(employerDetailsDTO.getEmpId());
        ResponseDTO response = new ResponseDTO();
        if (existingDetails != null) {
            EmployerDetails employerDetails = modelMapper.map(employerDetailsDTO, EmployerDetails.class);
            employerDetails.setId(existingDetails.getId());
            employerDetailsRepo.save(existingDetails);
            response.setMessage("Details updated successfully!");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.setMessage("Employee Details not found!");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
