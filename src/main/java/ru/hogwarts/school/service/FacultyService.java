package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.FacultyRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class FacultyService {

    Logger logger = LoggerFactory.getLogger(FacultyService.class);

    private final FacultyRepository facultyRepository;

    @Autowired
    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Collection<Faculty> getAllFaculty() {
        logger.info("start method getAllFaculty");
        return facultyRepository.findAll();

    }

    public Faculty add(Faculty faculty){
        logger.info("start method add");
        return facultyRepository.save(faculty);

    }

    public Faculty get(long id){
        logger.info("start method get");
        return facultyRepository.findById(id).get();
    }

    public Faculty put(Faculty faculty){
        logger.info("start method put");
        return facultyRepository.save(faculty);
    }

    public void remove(long id){
        logger.info("start method remove");
        facultyRepository.deleteById(id);
    }

    public Collection<Faculty> findByColor(String color){
        logger.info("start method findByColor");
        return facultyRepository.findByColorContains(color);

    }

    public Collection<Faculty> findByColorOrName(String color, String str){
        logger.info("start method findByColorOrName");
        if (color == null){
            if (str == null){
                return Collections.emptyList();
            }
            return facultyRepository.findAllByNameContainsIgnoreCase(str);
        }
        else if (str == null){
            return facultyRepository.findByColorContains(color);
        }
        else{
            Collection<Faculty> collection = facultyRepository.findAllByNameContainsIgnoreCase(str);
            collection.addAll(facultyRepository.findByColorContains(color));
            return collection.stream().collect(Collectors.toSet());
        }
    }




}
