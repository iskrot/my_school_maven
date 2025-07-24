package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.StudentRepository;

import java.util.Collection;

@Service

public class StudentService {


    private final StudentRepository studentRepositories;

    @Autowired
    public StudentService(StudentRepository studentRepositories) {
        this.studentRepositories = studentRepositories;
    }

    public Collection<Student> getAllStudents() {
        return studentRepositories.findAll();
    }

    public Student add(Student student){
        return studentRepositories.save(student);
    }

    public Student get(long id){
        return studentRepositories.findById(id).get();
    }

    public Student put(Student student){
        return studentRepositories.save(student);
    }

    public void remove(long id){
        studentRepositories.deleteById(id);
    }


    public Collection<Student> findByAge(int age) {
        return studentRepositories.findByAge(age);
    }

    public Collection<Student> findByAgeBetween(int min, int max){
        return studentRepositories.findByAgeBetween(min, max);
    }

    public Integer getCountAllStudent(){
        return studentRepositories.getQuantityAllStudent();
    }

    public Integer getAVGAgeAllStudent(){
        return studentRepositories.getAVGAgeAllStudent();
    }

    public Collection<Student> getLastFiveStudent(){
        return studentRepositories.getLastFiveStudent();
    }
}
