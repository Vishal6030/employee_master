package com.employee.employee_master.serviceImpl;

import com.employee.employee_master.dto.EmployerDetailsDTO;
import com.employee.employee_master.dto.ResponseDTO;
import com.employee.employee_master.encryption.AESEncryption;
import com.employee.employee_master.entity.EmployerDetails;
import com.employee.employee_master.exception.EmployeeNotFoundException;
import com.employee.employee_master.repository.EmployerDetailsRepo;
import com.employee.employee_master.service.EmployerDetailsService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Optional;

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
            Optional<EmployerDetails> optionalDetails = employerDetailsRepo.findById(id);

            if (optionalDetails.isPresent()) {
                return optionalDetails.get();
            } else {
                // Handling the case where no employee details were found for the given id.
                throw new EmployeeNotFoundException("Employee details not found for id: " + id);
            }
        } catch (Exception ex) {
            // Log the exception
            ex.printStackTrace();
            throw new RuntimeException("Error retrieving employee details by id: " + id);
        }
    }
//    @Override
//    public EmployerDetails getEmployeeDetailsById(Long id) {
//        EmployerDetails details = employerDetailsRepo.findByEmpId(id);
//        return details;
//    }
}
