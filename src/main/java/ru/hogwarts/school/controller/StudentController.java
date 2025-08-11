package ru.hogwarts.school.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;
import java.util.Collections;

@RestController
@RequestMapping("student")
public class StudentController {

    private StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }


    @PostMapping
    public Student addStudent(@RequestBody Student student) {
        return studentService.add(student);
    }


    @GetMapping("/{id}")
    public Student getStudent(@PathVariable(name = "id") long id) {
        return studentService.get(id);
    }

    @GetMapping
    public ResponseEntity<Collection<Student>> findStudents(@RequestParam(required = false) Integer age) {
        System.out.println(age);
        if (age == null) {
            return ResponseEntity.ok(studentService.getAllStudents());
        }
        if (age > 0) {
            return ResponseEntity.ok(studentService.findByAge(age));
        }

        return ResponseEntity.ok(Collections.emptyList());

    }

    @GetMapping("/getMinToMax")
    public ResponseEntity<Collection<Student>> getMinToMax(@RequestParam int min, @RequestParam int max) {
        return ResponseEntity.ok(studentService.findByAgeBetween(min, max));
    }

    @GetMapping("/{id}/getFaculty")
    public Faculty getFaculty(@PathVariable(name = "id") long id) {
        return studentService.get(id).getFaculty();
    }

    @GetMapping("/getCountAllStudent")
    public Integer getCountAllStudent(){
        return studentService.getCountAllStudent();
    }

    @GetMapping("/getAVGAgeStudent")
    public Integer getAVGAgeAllStudent(){
        return studentService.getAVGAgeAllStudent();
    }

    @GetMapping("/getLastFiveStudent")
    public Collection<Student> getLastFiveStudent(){
        return studentService.getLastFiveStudent();
    }

    @GetMapping("/getAllNameStudent")
    public Collection<String> getAllNameStudent(){
        return studentService.getAllNames();
    }

    @GetMapping("/getMedianAge")
    public int getMedianAge(){
        return studentService.getMedianAge();
    }


    @PutMapping
    public Student putStudent(@RequestBody Student student) {
        return studentService.put(student);
    }


    @DeleteMapping("/{id}")
    public void deleteStudent(@PathVariable(name = "id") long id) {
        studentService.remove(id);
    }


}
