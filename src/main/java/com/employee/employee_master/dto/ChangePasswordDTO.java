package com.employee.employee_master.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordDTO {
   // private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}
