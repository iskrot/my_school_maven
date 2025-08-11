package ru.hogwarts.school.controller;


import org.assertj.core.api.Assertions;
import org.json.JSONArray;
import org.json.JSONObject;
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
        for (Student i : list) {
            studentController.deleteStudent(i.getId());
        }
    }

    @Test
    void findTest() {
        List<Student> list = new ArrayList<>();
        for (byte i = 0; i < 5; i++) {
            list.add(studentController.addStudent(new Student(null, "1", i)));
            list.add(studentController.addStudent(new Student(null, "2", i)));
        }

        Assertions.assertThat(restTemplate.getForObject("http://localhost:" + port + "/student/getMinToMax?min=2&max=3", Student[].class)).isEqualTo(list.stream().filter(i -> 2 <= i.getAge() & i.getAge() <= 3).toList().toArray());

        for (Student i : list) {
            studentController.deleteStudent(i.getId());
        }
    }

    @Test
    void getStudentFacultyTest(){
        Faculty faculty = facultyController.addFaculty(new Faculty(null, "1", "1"));
        Student student = new Student(null, "1", 1);student.setFaculty(faculty);
        student = studentController.addStudent(student);

        Assertions.assertThat(restTemplate.getForObject("http://localhost:" + port + "/student/"+student.getId()+"/getFaculty", Faculty.class)).isEqualTo(faculty);

        studentController.deleteStudent(student.getId());
        facultyController.deleteFaculty(faculty.getId());

    }

    @Test
    void studentParallelsPrint(){
        Assertions.assertThat(restTemplate.getForEntity("http://localhost:" + port + "/students/print-parallel", Object.class).getStatusCode().equals(HttpStatusCode.valueOf(200)));
    }

    @Test
    void studentSynchronizedParallelsPrint(){
        Assertions.assertThat(restTemplate.getForEntity("http://localhost:" + port + "/students/print-synchronized", Object.class).getStatusCode().equals(HttpStatusCode.valueOf(200)));
    }

//    @Test
//    void CRUDdTest() throws Exception {
//        Long id = Null;
//        String name = "1";
//        int age = 1;
//
//        Student student = new Student(id, name, age);
//
//        when(studentRepository.save(any(Student.class))).thenReturn(student);
//        when(studentRepository.findById(any(Long.class))).thenReturn(Optional.of(student));
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .post("/student")
//                        .content(messenger.toString())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(name))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(age));
//        mockMvc.perform(MockMvcRequestBuilders
//                        .get("/student/1")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(name))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(age));
//    }

//    @Test
//    void findByAgeTest() throws Exception {
//        List<Student> list = new ArrayList<>();
//        list.add(new Student(1L, "1", 1));
//        list.add(new Student(2L, "2", 2));
//
//        when(studentRepository.findAll()).thenReturn(list);
//        when(studentRepository.findByAge(any(Integer.class))).thenReturn(list.stream().filter(i -> i.getAge() == 2).toList());
//
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .get("/student")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().json("[{\"id\":1,\"name\":\"1\",\"age\":1,\"facultyName\":null},{\"id\":2,\"name\":\"2\",\"age\":2,\"facultyName\":null}]"));
//        mockMvc.perform(MockMvcRequestBuilders
//                        .get("/student?age=2")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().json("[{\"id\":2,\"name\":\"2\",\"age\":2,\"facultyName\":null}]"));
//    }
//
//    @Test
//    void findByAgeBetweenTest() throws Exception {
//        List<Student> list = new ArrayList<>();
//        for (byte i = 0; i < 10; i++) {
//            list.add(new Student(i + 1L, "1", i + 1));
//            list.add(new Student(i + 2L, "2", i + 1));
//        }
//
//        when(studentRepository.findByAgeBetween(4, 7)).thenReturn(list.subList(6, 12));
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .get("/student/getMinToMax?min=4&max=7")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().json("[{\"id\":4,\"name\":\"1\",\"age\":4,\"facultyName\":null},{\"id\":5,\"name\":\"2\",\"age\":4,\"facultyName\":null},{\"id\":5,\"name\":\"1\",\"age\":5,\"facultyName\":null},{\"id\":6,\"name\":\"2\",\"age\":5,\"facultyName\":null},{\"id\":6,\"name\":\"1\",\"age\":6,\"facultyName\":null},{\"id\":7,\"name\":\"2\",\"age\":6,\"facultyName\":null}]"));
//    }
//
//    @Test
//    void getStudentFacultyTest() throws Exception {
//        Student student = new Student(1L, "1", 1);
//        student.setFaculty(new Faculty(1L, "1", "1"));
//
//        when(studentRepository.findById(any(Long.class))).thenReturn(Optional.of(student));
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .get("/student/1/getFaculty")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().json("{\"id\":1,\"name\":\"1\",\"color\":\"1\",\"students\":null}"));
//
//    }
//
//    @Test
//    void putStudentTest() throws Exception {
//        Student student = new Student(1L, "1", 2);
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("id", 1L);
//        jsonObject.put("name", "1");
//        jsonObject.put("age", 2);
//
//
//        when(studentRepository.save(any())).thenReturn(student);
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .put("/student")
//                        .content(jsonObject.toString())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().json(jsonObject.toString()));
//    }
//
//    @Test
//    void deleteTest() throws Exception {
//        Mockito.doNothing().when(studentRepository).deleteById(any());
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .delete("/student/1"))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//    }
}
