package com.employee.employee_master.repository;

import com.employee.employee_master.entity.EmployerDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployerDetailsRepo extends JpaRepository<EmployerDetails, Long> {

    EmployerDetails findByEmpId(Long id);
}
