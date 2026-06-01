package com.example.basiccrud;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    public StudentRepository repo;

    public void save(Student student){
        repo.save(student);
    }
    public List<Student> findAll(){
        return repo.findAll();
    }
}
