package ru.hogwarts.school.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;


@Entity()
public class Student {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private Integer age;


    @ManyToOne()
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;

    @OneToOne
    private Avatar avatar;

    public Student() {
    }

    public Student(Long id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return age == student.age && Objects.equals(id, student.id) && Objects.equals(name, student.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, age);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }


    @JsonIgnore
    public Faculty getFaculty() {
        return faculty;
    }


    public String getFacultyName() {
        if (faculty != null){
            return faculty.getName();
        }
        return null;
    }

    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }

    @JsonIgnore
    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }
}
