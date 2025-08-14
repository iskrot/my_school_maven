package ru.hogwarts.school.controller;

import org.assertj.core.api.Assertions;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
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
import ru.hogwarts.school.service.FacultyService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FacultyControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    FacultyController facultyController;

    @Autowired
    StudentController studentController;

    @Test
    void initTest() {
        Assertions.assertThat(facultyController).isNotNull();
    }

    @Test
    void CRUDTest() {
        Faculty faculty = new Faculty(null, "1", "1");

        Assertions.assertThat(faculty = restTemplate.postForObject("http://localhost:" + port + "/faculty", faculty, Faculty.class)).isEqualTo(faculty);
        Assertions.assertThat(restTemplate.getForObject("http://localhost:" + port + "/faculty/" + faculty.getId(), Faculty.class)).isEqualTo(faculty);
        faculty.setName("2");
        restTemplate.put("http://localhost:" + port + "/faculty", faculty);
        Assertions.assertThat(restTemplate.getForObject("http://localhost:" + port + "/faculty/" + faculty.getId(), Faculty.class)).isEqualTo(faculty);
        restTemplate.delete("http://localhost:" + port + "/faculty/" + faculty.getId());
        Assertions.assertThat(restTemplate.getForObject("http://localhost:" + port + "/faculty/" + faculty.getId(), Faculty.class)).isEqualTo(new Faculty());
    }

    @Test
    void findByColorTest() {
        List<Faculty> list = new ArrayList<>();
        for (byte i = 0; i < 5; i++) {
            list.add(facultyController.addFaculty(new Faculty(null, "1", String.valueOf(i))));
            list.add(facultyController.addFaculty(new Faculty(null, "2", String.valueOf(i))));
        }
        Assertions.assertThat(restTemplate.getForObject("http://localhost:" + port + "/faculty?color=2", Faculty[].class)).isEqualTo(list.stream().filter(i -> i.getColor().contains("2")).toList().toArray());
        for (Faculty i : list) {
            facultyController.deleteFaculty(i.getId());
        }
    }

    @Test
    void findTest() {
        List<Faculty> list = new ArrayList<>();
        for (byte i = 0; i < 5; i++) {
            list.add(facultyController.addFaculty(new Faculty(null, "1", String.valueOf(i))));
            list.add(facultyController.addFaculty(new Faculty(null, "2", String.valueOf(i))));
        }

        Assertions.assertThat(restTemplate.getForObject("http://localhost:" + port + "/faculty/searchToNameOrColor", Faculty[].class)).isEqualTo(new Faculty[]{});
        Assertions.assertThat(restTemplate.getForObject("http://localhost:" + port + "/faculty/searchToNameOrColor?color=2", Faculty[].class)).isEqualTo(list.stream().filter(i -> i.getColor().contains("2")).toList().toArray());
        Assertions.assertThat(restTemplate.getForObject("http://localhost:" + port + "/faculty/searchToNameOrColor?name=1", Faculty[].class)).isEqualTo(list.stream().filter(i -> i.getName().contains("1")).toList().toArray());
        Assertions.assertThat(restTemplate.getForObject("http://localhost:" + port + "/faculty/searchToNameOrColor?color=2&name=1", Faculty[].class)).isEqualTo(Stream.concat(list.stream().filter(i -> i.getColor().contains("2")),
                list.stream().filter(i -> i.getName().contains("1"))).collect(Collectors.toSet()).toArray());

        for (Faculty i : list) {
            facultyController.deleteFaculty(i.getId());
        }
    }

    @Test
    void getStudentsTest() throws Exception {
        Faculty faculty = facultyController.addFaculty(new Faculty(null, "1", "1"));
        Student student1;
        Student student2;
        List<Student> students = new ArrayList<>();

        Student student = new Student(null, "1", 1);
        student.setFaculty(faculty);
        students.add(student1 = studentController.addStudent(student));

        student = new Student(null, "2", 2);
        student.setFaculty(faculty);
        students.add(student2 = studentController.addStudent(student));

        System.out.println(restTemplate.getForObject("http://localhost:" + port + "/faculty/" + faculty.getId() + "/getStudents", List.class).stream().map(i -> Integer.parseInt(String.valueOf(i.toString().charAt(i.toString().lastIndexOf("age")+4)))).toList());

        Assertions.assertThat(restTemplate.getForObject("http://localhost:" + port + "/faculty/" + faculty.getId() + "/getStudents", Student[].class)).isEqualTo(students.toArray());

//        Assertions.assertThat(restTemplate.getForObject("http://localhost:" + port + "/faculty/" + faculty.getId() + "/getStudents", List.class)
//                .stream()
//                .map(i -> i.toString()
//                        .substring(i.toString().lastIndexOf("id")+3,
//                                i.toString().lastIndexOf("id")+3+
//                                        students.get(Integer.parseInt(
//                                                        String.valueOf(i.toString().charAt(i.toString().lastIndexOf("age")+4)))-1)
//                                                .getId().toString().length()))
//                .toList()).isEqualTo(students.stream()
//                .map(i -> i.getId().intValue()).map(i -> i.toString()).toList());

        studentController.deleteStudent(student1.getId());
        studentController.deleteStudent(student2.getId());
        facultyController.deleteFaculty(faculty.getId());

    }

}
