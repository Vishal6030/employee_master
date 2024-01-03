package com.employee.employee_master.serviceImpl;

import com.employee.employee_master.dto.EmployerDetailsDTO;
import com.employee.employee_master.dto.ResponseDTO;
import com.employee.employee_master.encryption.AESEncryption;
import com.employee.employee_master.entity.EmployerDetails;
import com.employee.employee_master.repository.EmployerDetailsRepo;
import com.employee.employee_master.service.EmployerDetailsService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

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
        EmployerDetails emp = modelMapper.map(employerDetailsDTO, EmployerDetails.class);
        if(emp!=null) {
            try {
                SecretKey key = aesEncryption.generateSecretKey();
                String encryptedSalary = aesEncryption.encrypt(emp.getSalary(), key);
                emp.setSalary(encryptedSalary);
                emp.setSecretKey(key);
                employerDetailsRepo.save(emp);
            } catch (Exception ex) {

            }
            return new ResponseEntity<>(new EmployerDetailsDTO(emp.getId(), emp.getEmpId(), emp.getEmpName(), emp.getSalary()), HttpStatus.OK);
        }else{
            ResponseDTO response = new ResponseDTO();
            response.setMessage("Error!");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public EmployerDetails getEmployeeDetailsById(Long id) {
        EmployerDetails details = employerDetailsRepo.findByEmpId(id);
        if(details!=null){
            return details;
        }else {
            return null;
        }
    }
}
