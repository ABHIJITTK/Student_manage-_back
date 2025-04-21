package com.example.Student.crud.service;

import com.example.Student.crud.model.Student;
import com.example.Student.crud.repository.StudentRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import java.awt.print.Book;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EntityManager entityManager;



    public Student createStudent(Student student){
        return studentRepository.save(student);
    }

    public List<Student> getAllStudent(){
        return studentRepository.findAll();
    }

    public Optional<Student> getStudentById(Long id){
        return studentRepository.findById(id);
    }
    public Student updateStudent(Long id,Student student){
        if (studentRepository.existsById(id)){
            student.setId(id);
            return studentRepository.save(student);
        }

        else {
            throw new RuntimeException("Student not found");
        }
    }

    public void deleteStudent(Long id){
        studentRepository.deleteById(id);
    }

    // Used criteia query to search,pagination and sorting
    public List<Student> searchStudent(String name,String mailid,int age){
        CriteriaBuilder cb=entityManager.getCriteriaBuilder();
        CriteriaQuery<Student> query=cb.createQuery(Student.class);
        Root<Student> root=query.from(Student.class);

        Predicate predicate=cb.conjunction();
        if (name!=null && !name.isEmpty()){
            predicate=cb.and(predicate,cb.like(root.get("name"),"%"+name+"%"));
        }
        else if (mailid!=null && !mailid.isEmpty()){
            predicate=cb.and(predicate,cb.like(root.get("mailid"),"%"+mailid+"%"));
        }
        else if (age>0){
            predicate=cb.and(predicate,cb.like(root.get("age"),"%"+age+"%"));
        }

        query.select(root).where(predicate);
        return entityManager.createQuery(query).getResultList();

    }


    // Ithu pagination and sorting criteria query illama
    public Page<Student> getStdWithsort(int page, int size, String sortBy, String sortDir) {
        Sort.Direction sort = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort,sortBy));
        return studentRepository.findAll(pageable);
    }

    // ithu stream api ku
    public List<Student> searchStudentWith(String name, String mailid, int age) {
        return studentRepository.findAll().stream()
                .filter(student -> (name != null && !name.isEmpty() ? student.getName().contains(name) : true))
                .filter(student -> (mailid != null && !mailid.isEmpty() ? student.getMailid().contains(mailid) : true))
                .filter(student -> (age > 0 ? student.getAge() == age : true))
                .collect(Collectors.toList());
    }

    public List<Student> getStudentsByAge(int age) {
        return studentRepository.findAll().stream()
                .filter(student -> student.getAge()==age)
                .collect(Collectors.toList());
    }
    public List<String> getUniqueStudentName() {
        return studentRepository.findAll().stream()
                .map(Student::getName)
                .distinct()
                .collect(Collectors.toList());
    }
    public List<Student> getByMailid(String mailid){
        return studentRepository.findAll().stream()
                .filter(s->s.getMailid().equalsIgnoreCase(mailid))
                .collect(Collectors.toList());
    }

    // check for error
//    public List<Student> getNameInUpperCase(){
//        return studentRepository.findAll().stream()
//                .map(s->s.setName(s.getName().toUpperCase()))
//                .collect(Collectors.toList());
//    }

}
