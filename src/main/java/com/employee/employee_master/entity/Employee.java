package com.employee.employee_master.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(
                name="add_employee",
                procedureName = "add_employee",parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN,name="p_address_id1",type = Long.class ),
                @StoredProcedureParameter(mode = ParameterMode.IN,name="p_address_id2",type = Long.class ),
                @StoredProcedureParameter(mode = ParameterMode.IN,name="p_birth_date",type = Date.class ),
                @StoredProcedureParameter(mode = ParameterMode.IN,name="p_company_id",type = Long.class ),
                @StoredProcedureParameter(mode = ParameterMode.IN,name="p_department_id",type = Long.class ),
                @StoredProcedureParameter(mode = ParameterMode.IN,name="p_designation_id",type = Long.class ),
                @StoredProcedureParameter(mode = ParameterMode.IN,name="p_first_name",type = String.class ),
                @StoredProcedureParameter(mode = ParameterMode.IN,name="p_last_name",type = String.class ),
                @StoredProcedureParameter(mode = ParameterMode.IN,name="p_gender",type = String.class ),
                @StoredProcedureParameter(mode = ParameterMode.IN,name="p_hire_date",type = Date.class ),
                @StoredProcedureParameter(mode = ParameterMode.IN,name="p_home_number",type = String.class ),
                @StoredProcedureParameter(mode = ParameterMode.IN,name="p_personal_email",type = String.class ),
                @StoredProcedureParameter(mode = ParameterMode.IN,name="p_phone",type = String.class ),
                @StoredProcedureParameter(mode = ParameterMode.IN,name="p_status",type = String.class ),
                @StoredProcedureParameter(mode = ParameterMode.IN,name="p_work_email",type = String.class ),
                @StoredProcedureParameter(mode = ParameterMode.IN,name="p_password",type = String.class )
        }),
        @NamedStoredProcedureQuery(
                name="update_employee",
                procedureName = "update_employee",parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN,name="p_emp_id",type = Long.class ),
                @StoredProcedureParameter(mode = ParameterMode.IN,name="p_address_id1",type = Long.class ),
                @StoredProcedureParameter(mode = ParameterMode.IN,name="p_address_id2",type = Long.class ),
                @StoredProcedureParameter(mode = ParameterMode.IN,name="p_birth_date",type = Date.class ),
                @StoredProcedureParameter(mode = ParameterMode.IN,name="p_company_id",type = Long.class ),
                @StoredProcedureParameter(mode = ParameterMode.IN,name="p_department_id",type = Long.class ),
                @StoredProcedureParameter(mode = ParameterMode.IN,name="p_designation_id",type = Long.class ),
                @StoredProcedureParameter(mode = ParameterMode.IN,name="p_first_name",type = String.class ),
                @StoredProcedureParameter(mode = ParameterMode.IN,name="p_last_name",type = String.class ),
                @StoredProcedureParameter(mode = ParameterMode.IN,name="p_gender",type = String.class ),
                @StoredProcedureParameter(mode = ParameterMode.IN,name="p_hire_date",type = Date.class ),
                @StoredProcedureParameter(mode = ParameterMode.IN,name="p_home_number",type = String.class ),
                @StoredProcedureParameter(mode = ParameterMode.IN,name="p_personal_email",type = String.class ),
                @StoredProcedureParameter(mode = ParameterMode.IN,name="p_phone",type = String.class ),
                @StoredProcedureParameter(mode = ParameterMode.IN,name="p_status",type = String.class ),
                @StoredProcedureParameter(mode = ParameterMode.IN,name="p_work_email",type = String.class ),
                @StoredProcedureParameter(mode = ParameterMode.IN,name="p_password",type = String.class )
        })
})
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
    private String password;
}
