package com.project.healthsystem.controller;

import com.project.healthsystem.controller.dto.LoginDTO;
import com.project.healthsystem.service.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody @Valid LoginDTO loginDTO){
        LoginDTO login = loginService.save(loginDTO);

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(login.getId())
            .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> update(@PathVariable("id") long id, @RequestBody @Valid LoginDTO loginDTO){
        loginService.update(loginDTO, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<LoginDTO>> readAll(){
        List<LoginDTO> loginDTOS = loginService.getAll();
        return ResponseEntity.ok(loginDTOS);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") long id){
        loginService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
