package com.employee.employee_master.serviceImpl;

import com.employee.employee_master.entity.Employee;
import com.employee.employee_master.repository.EmployeeRepo;
import com.employee.employee_master.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    EmployeeRepo employeeRepo;
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
}
