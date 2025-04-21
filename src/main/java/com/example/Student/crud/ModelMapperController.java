package com.example.Student.crud;

import com.example.Student.crud.dto.StudentDTO;
import com.example.Student.crud.model.Contact;
import com.example.Student.crud.model.Project;
import com.example.Student.crud.model.Student;
import com.example.Student.crud.repository.DepartmentRepository;
import com.example.Student.crud.repository.ProjectREpository;
import com.example.Student.crud.service.StudentService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/studentsDTO")
public class ModelMapperController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private ProjectREpository projectRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EntityManager entityManager;

    @GetMapping
    public List<StudentDTO> getAllStudents() {
        List<Student> students = studentService.getAllStudent();
        List<StudentDTO> studentDTOList = new ArrayList<>();
        for (Student student : students) {
            studentDTOList.add(modelMapper.map(student, StudentDTO.class));
        }
        return studentDTOList;
    }


    @PostMapping
    public ResponseEntity<StudentDTO> create(@RequestBody StudentDTO studentDTO) {
        Student student = modelMapper.map(studentDTO, Student.class);

        if (studentDTO.getDepartment() != null && studentDTO.getDepartment().getId() != null) {
            departmentRepository.findById(studentDTO.getDepartment().getId())
                    .ifPresent(student::setDepartment);
        }

        if (studentDTO.getProjects() != null) {
            List<Project> projectList = studentDTO.getProjects().stream()
                    .map(p -> projectRepository.findById(p.getId()).orElse(null))
                    .filter(Objects::nonNull)
                    .filter(p -> p.getId() != null)
                    .collect(Collectors.toList());
            student.setProjects(projectList);
        }

        if (studentDTO.getContacts() != null) {
            List<Contact> contactList = studentDTO.getContacts().stream()
                    .filter(c -> c.getContactPhone() != null)
                    .map(c -> {
                        Contact contact = new Contact();
                        contact.setContactPhone(c.getContactPhone());
                        return contact;
                    })
                    .collect(Collectors.toList());
            student.setContacts(contactList);
        }

        Student savedStudent = studentService.createStudent(student);
        return ResponseEntity.ok(modelMapper.map(savedStudent, StudentDTO.class));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable Long id) {
        Optional<Student> student = studentService.getStudentById(id);
        return student.map(s -> ResponseEntity.ok(modelMapper.map(s, StudentDTO.class)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


    @PutMapping("/{id}")
    public ResponseEntity<StudentDTO> updateStudent(@PathVariable Long id, @RequestBody Student student) {
        try {
            Student updatedStudent = studentService.updateStudent(id, student);
            return ResponseEntity.ok(modelMapper.map(updatedStudent, StudentDTO.class));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pagination")
    public ResponseEntity<Page<StudentDTO>> getStudentsWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Page<Student> studentsPage = studentService.getStdWithsort(page, size, sortBy, sortDir);
        Page<StudentDTO> studentDTOPage = studentsPage.map(student -> modelMapper.map(student, StudentDTO.class));
        return ResponseEntity.ok(studentDTOPage);
    }

    @GetMapping("/search/pagination")
    public List<StudentDTO> searchStudentsWithPaginationAndSorting(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam String search) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Student> query = cb.createQuery(Student.class);
        Root<Student> root = query.from(Student.class);

        List<Predicate> predicates = new ArrayList<>();
        if (search != null && !search.isEmpty()) {
            String searchItem = "%" + search + "%";
            predicates.add(cb.like(root.get("name"), searchItem));
            predicates.add(cb.like(root.get("mailid"), searchItem));

            if (search.matches("\\d+")) {
                Double age = Double.parseDouble(search);
                predicates.add(cb.equal(root.get("age"), age));
            }
        }

        if (!predicates.isEmpty()) {
            query.where(cb.or(predicates.toArray(new Predicate[0])));
        }

        if ("asc".equalsIgnoreCase(sortDir)) {
            query.orderBy(cb.asc(root.get(sortBy)));
        } else {
            query.orderBy(cb.desc(root.get(sortBy)));
        }

        TypedQuery<Student> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult(page * size);
        typedQuery.setMaxResults(size);
        List<Student> result = typedQuery.getResultList();

        List<StudentDTO> studentDTOList = new ArrayList<>();
        for (Student student : result) {
            studentDTOList.add(modelMapper.map(student, StudentDTO.class));
        }

        return studentDTOList;
    }

    @GetMapping("/filter-age/{age}")
    public ResponseEntity<List<StudentDTO>> getStudentsByAge(@PathVariable int age) {
        List<Student> students = studentService.getStudentsByAge(age);
        List<StudentDTO> studentDTOList = new ArrayList<>();
        for (Student student : students) {
            studentDTOList.add(modelMapper.map(student, StudentDTO.class));
        }
        return ResponseEntity.ok(studentDTOList);
    }

    @GetMapping("/search-std")
    public ResponseEntity<List<StudentDTO>> searchStudentsByFilters(
            @RequestParam String name,
            @RequestParam String mailid,
            @RequestParam int age) {

        List<Student> students = studentService.searchStudent(name, mailid, age);
        List<StudentDTO> studentDTOList = new ArrayList<>();
        for (Student student : students) {
            studentDTOList.add(modelMapper.map(student, StudentDTO.class));
        }
        return ResponseEntity.ok(studentDTOList);
    }

    @GetMapping("/search-unique-name")
    public ResponseEntity<List<String>> getUniqueNames() {
        List<String> uniqueStudentNames = studentService.getUniqueStudentName();
        return ResponseEntity.ok(uniqueStudentNames);
    }
}
