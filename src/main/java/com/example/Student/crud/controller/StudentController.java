package com.example.Student.crud.controller;

import com.example.Student.crud.dto.ResponseModel;
import com.example.Student.crud.model.Student;
import com.example.Student.crud.service.StudentService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private EntityManager entityManager;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseModel> getAllStudent() {
        try {
            List<Student> students = studentService.getAllStudent();
            ResponseModel response = new ResponseModel(
                    HttpStatus.OK.value(),
                    HttpStatus.OK,
                    students,
                    "Students fetched successfully"
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ResponseModel response = new ResponseModel(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "No data",
                    "Failed to fetch students"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseModel> createStudent(@RequestBody Student student) {
        try {
            Student created = studentService.createStudent(student);
            ResponseModel response = new ResponseModel(
                    HttpStatus.CREATED.value(),
                    HttpStatus.CREATED,
                    created,
                    "Student created successfully"
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            ResponseModel response = new ResponseModel(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "No data",
                    "Failed to create student"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel> getStudentById(@PathVariable Long id) {
        Optional<Student> student = studentService.getStudentById(id);
        if (student.isPresent()) {
            ResponseModel response = new ResponseModel(
                    HttpStatus.OK.value(),
                    HttpStatus.OK,
                    student.get(),
                    "Student found"
            );
            return ResponseEntity.ok(response);
        } else {
            ResponseModel response = new ResponseModel(
                    HttpStatus.NOT_FOUND.value(),
                    HttpStatus.NOT_FOUND,
                    "No data",
                    "Student not found"
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseModel> updateStudent(@PathVariable Long id, @RequestBody Student student) {
        try {
            Student updated = studentService.updateStudent(id, student);
            ResponseModel response = new ResponseModel(
                    HttpStatus.OK.value(),
                    HttpStatus.OK,
                    updated,
                    "Student updated successfully"
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            ResponseModel response = new ResponseModel(
                    HttpStatus.NOT_FOUND.value(),
                    HttpStatus.NOT_FOUND,
                    "No data",
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseModel> deleteStudent(@PathVariable Long id) {
        try {
            studentService.deleteStudent(id);
            ResponseModel response = new ResponseModel(
                    HttpStatus.NO_CONTENT.value(),
                    HttpStatus.NO_CONTENT,
                    null,
                    "Student deleted successfully"
            );
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        } catch (Exception e) {
            ResponseModel response = new ResponseModel(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "No data",
                    "Failed to delete student"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    //ithu 2nd: Task Controller for paging
    @GetMapping("/pagination")
    @PreAuthorize("hasAuthority('USER','ADMIN')")
    public ResponseEntity<Page<Student>> getStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Page<Student> students = studentService.getStdWithsort(page, size, sortBy, sortDir);
        return ResponseEntity.ok(students);
    }


    //Ithu criteria query ku with search,pagination,sort ku need practice vera function la implement panniparu
    @GetMapping("/search/pagination")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    public List<Student> pagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam String search){

        CriteriaBuilder cb=entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery=cb.createQuery(Long.class);
        Root<Long> countroot=countQuery.from(Long.class);
        Long countdb=entityManager.createQuery(countQuery).getSingleResult();

        if (page*size>=countdb.intValue()){
            return Collections.emptyList();
        }

        CriteriaQuery<Student> query=cb.createQuery(Student.class);
        Root<Student> root=query.from(Student.class);
        query.select(root);

        if (search!=null){
            String searchItem="%"+search+"%";
            Predicate searchName=cb.like(root.get("name"),searchItem);
            Predicate searchMailid=cb.like(root.get("mailid"),searchItem);
            Predicate searchAge;
            Predicate searchParseAge;

            if (search.matches("\\d+")){
                Double age=Double.parseDouble(search);
                searchAge=cb.equal(root.get("age"),age);
                searchParseAge=cb.or(searchAge,searchMailid,searchName);
            }
            else{
                searchParseAge=cb.or(searchMailid,searchName);
            }


            query.where(searchParseAge);
        }

        if (sortBy!=null){
            if (sortBy.equalsIgnoreCase("asc")){
                query.orderBy(cb.asc(root.get("sortBy")));
            }else{
                query.orderBy(cb.desc(root.get("sortBy")));
            }
        }


        TypedQuery<Student> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult(page * size);
        typedQuery.setMaxResults(size);
        return typedQuery.getResultList();
    }


    // ithu stream api ku controller
    @GetMapping("/filter-age/{age}")
    public ResponseEntity<List<Student>> getStudentsByAge(@PathVariable int age) {
        List<Student> students = studentService.getStudentsByAge(age);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/search-std")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    public ResponseEntity<List<Student>> getbyfilter(
            @RequestParam String name,
            @RequestParam String mailid,
            @RequestParam int age) {

        List<Student> students = studentService.searchStudent(name, mailid, age);

        return ResponseEntity.ok(students);
    }
    @GetMapping("/search-unique-name")
    public ResponseEntity<List<String>> getUniqueNames() {
        List<String> uniqueStudentNames = studentService.getUniqueStudentName();
        return ResponseEntity.ok(uniqueStudentNames);
    }

    }