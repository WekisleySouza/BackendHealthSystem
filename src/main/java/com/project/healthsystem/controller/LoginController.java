package com.project.healthsystem.controller;

import com.project.healthsystem.controller.dto.LoginRequestDTO;
import com.project.healthsystem.service.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

//    @PostMapping
//    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
//    public ResponseEntity<Void> save(@RequestBody @Valid LoginRequestDTO loginRequestDTO){
//        LoginRequestDTO login = loginService.save(loginRequestDTO);
//
//        URI location = ServletUriComponentsBuilder
//            .fromCurrentRequest()
//            .path("/{id}")
//            .buildAndExpand(login.getId())
//            .toUri();
//
//        return ResponseEntity.created(location).build();
//    }

    @PutMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE', 'USER')")
    public ResponseEntity<Object> update(@PathVariable("id") long id, @RequestBody @Valid LoginRequestDTO loginRequestDTO){
        loginService.update(loginRequestDTO, id);
        return ResponseEntity.noContent().build();
    }

//    @GetMapping
//    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
//    public ResponseEntity<List<LoginRequestDTO>> readAll(){
//        List<LoginRequestDTO> loginRequestDTOS = loginService.getAll();
//        return ResponseEntity.ok(loginRequestDTOS);
//    }
//
//    @DeleteMapping("{id}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
//    public ResponseEntity<Object> delete(@PathVariable("id") long id){
//        loginService.delete(id);
//        return ResponseEntity.noContent().build();
//    }
}
