package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students") // Base URL for all methods in this controller
// Allows React (running on a different port) to talk to Spring Boot
@CrossOrigin(origins = "http://localhost:3000") 
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    /**
     * GET endpoint to fetch all students
     * URL: GET http://localhost:8080/api/students
     */
    @GetMapping
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    /**
     * POST endpoint to create a new student
     * Body should be JSON: {"name": "Alice", "age": 20}
     * URL: POST http://localhost:8080/api/students
     */
    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        // The save method handles both insertion and update if ID exists
        return studentRepository.save(student);
    }
}


@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
}
