package ru.hogwarts.school.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;
import ru.hogwarts.school.service.FacultyService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@RestController
public class AvatarController {
    private AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @PostMapping(value = "/student/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity addAvatar(@PathVariable(name = "id") Long id, @RequestParam MultipartFile avatar) throws IOException {
        if (avatar.getSize() > 1024*50){
            return ResponseEntity.badRequest().body("слишком большой файл");
        }
        avatarService.unloadAvatar(id, avatar);
        return ResponseEntity.ok("аватарка добавлена");
    }


    @GetMapping("/student/{id}/avatar")
    public ResponseEntity<byte[]> getById(@PathVariable(name = "id") long id){
        Avatar avatar = avatarService.getById(id);
        if  (avatar == null){
            ResponseEntity.status(404);
            return new ResponseEntity("аватарка не найдена", HttpStatusCode.valueOf(404));
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
        headers.setContentLength(avatar.getData().length);

        return ResponseEntity.status(200).headers(headers).body(avatar.getData());
    }

    @GetMapping("/student/{id}/avatar/dr")
    public ResponseEntity<byte[]> getDirById(@PathVariable(name = "id") long id) throws IOException {
        Avatar avatar = avatarService.getById(id);
        Path path = Path.of(avatar.getFilePath());
        byte[] file = Files.readAllBytes(path);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
        headers.setContentLength(avatar.getData().length);

        return ResponseEntity.status(200).headers(headers).body(file);
    }

    @GetMapping("/student/avatars")
    public List<Avatar> getAll(@RequestParam Integer size, @RequestParam Integer number) {
        return avatarService.getAll(size, number);
    }
}
