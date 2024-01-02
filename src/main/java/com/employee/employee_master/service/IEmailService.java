package com.employee.employee_master.service;

public interface IEmailService {
    String sendSimpleMail(String to,String subject,String content);
}
