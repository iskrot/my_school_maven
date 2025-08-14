package ru.hogwarts.school.controller;


import org.assertj.core.api.Assertions;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.FacultyRepository;
import ru.hogwarts.school.repositories.StudentRepository;
import ru.hogwarts.school.service.StudentService;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    private StudentController studentController;

    @Autowired
    private FacultyController facultyController;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Test
    void initTest() {
        Assertions.assertThat(true).isNotNull();
    }

    @Test
    void CRUDTest() {
        Student student = new Student(null, "1", 1);

        Assertions.assertThat(student = restTemplate.postForObject("http://localhost:" + port + "/student", student, Student.class)).isEqualTo(student);
        Assertions.assertThat(restTemplate.getForObject("http://localhost:" + port + "/student/" + student.getId(), Student.class)).isEqualTo(student);
        student.setName("2");
        restTemplate.put("http://localhost:" + port + "/student", student);
        Assertions.assertThat(restTemplate.getForObject("http://localhost:" + port + "/student/" + student.getId(), Student.class)).isEqualTo(student);
        restTemplate.delete("http://localhost:" + port + "/student/" + student.getId());
        Assertions.assertThat(restTemplate.getForObject("http://localhost:" + port + "/student/" + student.getId(), Student.class)).isEqualTo(new Student());
    }

    @Test
    void findByAgeTest() {
        List<Student> list = new ArrayList<>();
        for (byte i = 0; i < 5; i++) {
            list.add(studentController.addStudent(new Student(null, "1", i)));
            list.add(studentController.addStudent(new Student(null, "2", i)));
        }

        Assertions.assertThat(restTemplate.getForObject("http://localhost:" + port + "/student?age=2", Student[].class)).isEqualTo(list.stream().filter(i -> i.getAge() == 2).toList().toArray());

    }

    @Test
    void findTest() {
        List<Student> list = new ArrayList<>();
        for (byte i = 0; i < 5; i++) {
            list.add(studentController.addStudent(new Student(null, "1", i)));
            list.add(studentController.addStudent(new Student(null, "2", i)));
        }

        Assertions.assertThat(restTemplate.getForObject("http://localhost:" + port + "/student/getMinToMax?min=2&max=3", Student[].class)).isEqualTo(list.stream().filter(i -> 2 <= i.getAge() & i.getAge() <= 3).toList().toArray());

    }

    @Test
    void getStudentFacultyTest(){
        Faculty faculty = facultyController.addFaculty(new Faculty(null, "1", "1"));
        Student student = new Student(null, "1", 1);student.setFaculty(faculty);
        student = studentController.addStudent(student);

        Assertions.assertThat(restTemplate.getForObject("http://localhost:" + port + "/student/"+student.getId()+"/getFaculty", Faculty.class)).isEqualTo(faculty);


    }

    @Test
    void studentParallelsPrint(){
        Assertions.assertThat(restTemplate.getForEntity("http://localhost:" + port + "/students/print-parallel", Object.class).getStatusCode().equals(HttpStatusCode.valueOf(200)));
    }

    @Test
    void studentSynchronizedParallelsPrint(){
        Assertions.assertThat(restTemplate.getForEntity("http://localhost:" + port + "/students/print-synchronized", Object.class).getStatusCode().equals(HttpStatusCode.valueOf(200)));
    }

    @AfterEach
    void clear(){
        List<Student> list1 = new ArrayList<>(studentRepository.findByName("1"));
        List<Student> list2 = new ArrayList<>(studentRepository.findByName("2"));
        list1.addAll(list2);
        for (Student i : list1){
            if (i.getFaculty() != null){
                facultyRepository.deleteById(i.getFaculty().getId());
            }
            studentRepository.deleteById(i.getId());
        }
    }

}
