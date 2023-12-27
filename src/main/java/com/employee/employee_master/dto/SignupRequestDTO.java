package com.employee.employee_master.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SignupRequestDTO {

    private Long id;
    private String fullName;
    private String email;
    private String password;
    private String confirmPassword;
    private String phoneNo;

}
