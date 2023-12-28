package com.employee.employee_master.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long empId;
    private String firstName;
    private String lastName;
    private String workEmail;
    private String personalEmail;
    private String phone;
    private String homeNumber;
    private String gender;
    private String status;
    private Date hireDate;
    private Date birthDate;
    private Long companyId;
    private Long addressId1;
    private Long addressId2;
    private Long departmentId;
    private Long designationId;
}
