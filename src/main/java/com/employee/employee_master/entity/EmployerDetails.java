package com.employee.employee_master.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import javax.crypto.SecretKey;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployerDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long empId;
    private String salary;
    private SecretKey secretKey;
}

