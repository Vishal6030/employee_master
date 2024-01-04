package com.employee.employee_master;

import org.modelmapper.*;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.cache.annotation.*;
import org.springframework.context.annotation.*;

@SpringBootApplication
@EnableCaching
public class EmployeeMasterApplication {

	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}

	public static void main(String[] args) {
		SpringApplication.run(EmployeeMasterApplication.class, args);
	}

}
