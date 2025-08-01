package ru.hogwarts.school.service;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.AvatarRepository;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
@Transactional
public class AvatarService {

    private static final Logger logger = LoggerFactory.getLogger(AvatarService.class);

    private AvatarRepository avatarRepository;
    private StudentService studentService;

    @Value("${students.avatars.dir.pach}")
    private String avatarsDir;

    public AvatarService(AvatarRepository avatarRepository, StudentService studentService) {
        this.avatarRepository = avatarRepository;
        this.studentService = studentService;
    }

    public void unloadAvatar(Long id, MultipartFile file) throws IOException {
        Student student = studentService.get(id);

        Path filePath = Path.of(avatarsDir, id + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        try (
                InputStream is = file.getInputStream();
                OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
                BufferedInputStream bis = new BufferedInputStream(is, 1024);
                BufferedOutputStream bos = new BufferedOutputStream(os, 1024)
        ){
            bis.transferTo(bos);
        }

        Avatar avatar = new Avatar(id);
        avatar.setStudent(student);
        avatar.setData(file.getBytes());
        avatar.setFilePath(filePath.toString());
        avatar.setMediaType(file.getContentType());
        avatar.setFileSize(file.getSize());
        avatarRepository.save(avatar);
    }

    public Avatar getById(long id) {
        logger.info("start method getById");
        if (avatarRepository.findAll().stream().map(i -> i.getId()).toList().contains(id)) {
            return  avatarRepository.findById(id).get();

        }
        return null;
    }

    public List<Avatar> getAll(Integer size, Integer number) {
        logger.info("start method getAll(Integer size, Integer number)");
        PageRequest pageRequest = PageRequest.of(number-1, size);
        return avatarRepository.findAll(pageRequest).getContent();
    }

    public List<Avatar> getAll(){
        logger.info("start method getAll");
        return avatarRepository.findAll();
    }

    public void put(Avatar avatar) {
        logger.info("start method put");
        if (!avatarRepository.findAll().stream().map(i -> i.getId()).toList().contains(avatar.getId())) {
            avatarRepository.save(avatar);
            logger.info("method put save: "+avatar);
        }
        else {
            logger.debug("method put don't save: "+avatar);
        }
    }

    public void remove(Long id) {
        logger.info("start method remove");
        avatarRepository.deleteById(id);
    }


    public String getAvatarsDir() {
        logger.info("start method getAvatarDir");
        return avatarsDir;
    }


}
