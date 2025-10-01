package com.project.healthsystem.controller;

import com.project.healthsystem.controller.dto.ErrorResponseDTO;
import com.project.healthsystem.controller.dto.LoginDTO;
import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.Login;
import com.project.healthsystem.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody LoginDTO loginDTO){
        Login login = loginService.save(loginDTO);

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(login.getId())
            .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> update(@PathVariable("id") long id, @RequestBody LoginDTO loginDTO){
        try {
            loginService.update(loginDTO, id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException err){
            ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.notFound(err.getMessage());
            return ResponseEntity.status(errorResponseDTO.status()).body(errorResponseDTO);
        }
    }

    @GetMapping
    public ResponseEntity<List<LoginDTO>> readAll(){
        List<LoginDTO> loginDTOS = loginService.getAll();
        return ResponseEntity.ok(loginDTOS);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") long id){
        try{
            loginService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException err){
            ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.notFound(err.getMessage());
            return ResponseEntity.status(errorResponseDTO.status()).body(errorResponseDTO);
        }
    }
}
