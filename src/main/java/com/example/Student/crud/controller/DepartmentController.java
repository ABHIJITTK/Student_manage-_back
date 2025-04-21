package com.example.Student.crud.controller;




import com.example.Student.crud.model.Department;
import com.example.Student.crud.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping
    public List<Department> getAllDepartments() {
        return departmentService.getAllDepartments();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Department> getDepartmentById(@PathVariable Long id) {
        Optional<Department> department = departmentService.getDepartmentById(id);
        return department.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<Department> createDepartment(@RequestBody Department department) {
        Department createdDepartment = departmentService.saveDepartment(department);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDepartment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Department> updateDepartment(@PathVariable Long id, @RequestBody Department department) {
        Optional<Department> existingDepartment = departmentService.getDepartmentById(id);
        if (existingDepartment.isPresent()) {
            Department updatedDepartment = departmentService.updateDepartment(id,department);
            return ResponseEntity.ok(updatedDepartment);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        Optional<Department> department = departmentService.getDepartmentById(id);
        if (department.isPresent()) {
            departmentService.deleteDepartment(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}

//
//import com.example.Student.crud.model.Department;
//import com.example.Student.crud.service.DepartmentService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/departments")
//public class DepartmentController {
//
//    @Autowired
//    private DepartmentService departmentService;
//
//    @GetMapping
//    public List<Department> getAllDepartments() {
//        return departmentService.getAllDepartments();
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Department> getDepartmentById(@PathVariable Long id) {
//        Optional<Department> department = departmentService.getDepartmentById(id);
//        return department.map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
//    }
//
//    @PostMapping
//    public ResponseEntity<Department> createDepartment(@RequestBody Department department) {
//        Department savedDepartment = departmentService.saveDepartment(department);
//        return ResponseEntity.status(HttpStatus.CREATED).body(savedDepartment);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<Department> updateDepartment(@PathVariable Long id, @RequestBody Department department) {
//        Optional<Department> existingDepartment = departmentService.getDepartmentById(id);
//        if (existingDepartment.isPresent()) {
//            department.setId(id);
//            Department updatedDepartment = departmentService.saveDepartment(department);
//            return ResponseEntity.ok(updatedDepartment);
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
//        departmentService.deleteDepartment(id);
//        return ResponseEntity.noContent().build();
//    }
//}
