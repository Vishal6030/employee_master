package com.employee.employee_master.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForgetPasswordDTO {
    private String email;
    private String newPassword;
    private String confirmPassword;

}
