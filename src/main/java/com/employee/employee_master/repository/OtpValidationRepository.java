package com.employee.employee_master.repository;

import com.employee.employee_master.entity.OtpValidation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface OtpValidationRepository extends JpaRepository<OtpValidation, Long> {

    OtpValidation findByEmailAndOtpAndValidatedFalseAndExpirationTimeAfter(String email, String otp, LocalDateTime time);
}
