package com.employee.employee_master.repository;

import com.employee.employee_master.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee, Long> {

    Employee findByEmpId(Long empId);
    List<Employee> findByCompanyId(Long companyId);
    Employee findByWorkEmail(String email);
}
