package com.employee.employee_master.dto;

import org.springframework.security.core.GrantedAuthority;

public enum AppUserRole implements GrantedAuthority {
  SUPER_ADMIN, USER_ROLE;

  public String getAuthority() {
    return name();
  }
  }
