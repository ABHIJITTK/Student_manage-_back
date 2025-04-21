package com.example.Student.crud.dto;

import com.example.Student.crud.model.Address;
import com.example.Student.crud.model.Contact;
import com.example.Student.crud.model.Department;
import com.example.Student.crud.model.Project;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentDTO {
    private String name;
    private String mailid;
    private int age;
    private List<ContactDTO> contacts = new ArrayList<>();
    private DepartmentDTO department;
    private List<ProjectDTO> projects = new ArrayList<>();
    private AddressDTO address;
}

