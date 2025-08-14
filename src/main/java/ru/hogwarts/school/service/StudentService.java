package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.StudentRepository;

import java.util.Collection;
import java.util.List;

@Service

public class StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);
    private final StudentRepository studentRepositories;

    @Autowired
    public StudentService(StudentRepository studentRepositories) {
        this.studentRepositories = studentRepositories;
    }

    public Collection<Student> getAllStudents() {
        logger.info("start method getAllStudents");
        return studentRepositories.findAll();
    }

    public Student add(Student student){
        logger.info("start method add");
        return studentRepositories.save(student);
    }

    public Student get(long id){
        logger.info("start method get");
        return studentRepositories.findById(id).get();
    }

    public Student put(Student student){
        logger.info("start method put");
        return studentRepositories.save(student);
    }

    public void remove(long id){
        logger.info("start method remove");
        studentRepositories.deleteById(id);
    }


    public Collection<Student> findByAge(int age) {
        logger.info("start method findByAge");
        return studentRepositories.findByAge(age);
    }

    public Collection<Student> findByAgeBetween(int min, int max){
        logger.info("start method findByAgeBetween");
        return studentRepositories.findByAgeBetween(min, max);
    }

    public Integer getCountAllStudent(){
        logger.info("start method getCountAllStudent");
        return studentRepositories.getQuantityAllStudent();
    }

    public Integer getAVGAgeAllStudent(){
        logger.info("start method getAVGAgeAllStudent");
        return studentRepositories.getAVGAgeAllStudent();
    }

    public Collection<Student> getLastFiveStudent(){
        logger.info("start method getLastFiveStudent");
        return studentRepositories.getLastFiveStudent();
    }

    public Collection<String> getAllNames() {
        logger.info("start method getAllName");
        return studentRepositories.findAll().stream().parallel().map(i -> i.getName()).sorted().toList();
    }

    public int getMedianAge() {
        logger.info("start method getMedianAge");
        return (int) studentRepositories.findAll().stream()
                .map(i -> i.getAge())
                .mapToInt(Integer::intValue)
                .average()
                .getAsDouble();
    }

    public void studentParallelsPrint() {
        PageRequest pageRequest = PageRequest.of(0, 6);
        List<Student> list = studentRepositories.findAll(pageRequest).getContent();

        System.out.println(list.get(0));
        System.out.println(list.get(1));

        new Thread(()-> {
            System.out.println(list.get(2));
            System.out.println(list.get(3));
        }).start();
        new Thread(()-> {
            System.out.println(list.get(4));
            System.out.println(list.get(5));
        }).start();
    }
    private synchronized void print(Student x){
        System.out.println(x);
    }

    public void studentSynchronizedParallelsPrint() {

        PageRequest pageRequest = PageRequest.of(0, 6);
        List<Student> list = studentRepositories.findAll(pageRequest).getContent();

        print(list.get(0));
        print(list.get(1));

        new Thread(()-> {
            print(list.get(2));
            print(list.get(3));
        }).start();
        new Thread(()-> {
            print(list.get(4));
            print(list.get(5));
        }).start();
    }
}
