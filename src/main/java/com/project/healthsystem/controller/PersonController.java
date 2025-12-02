package com.project.healthsystem.controller;

import com.project.healthsystem.controller.common.ControllerAuxFunctions;
import com.project.healthsystem.controller.dto.PersonRequestDTO;
import com.project.healthsystem.model.Person;
import com.project.healthsystem.service.PersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PersonController {
    private final PersonService personService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<Void> save(
        @RequestHeader("Authorization") String authHeader,
        @RequestBody @Valid PersonRequestDTO person
    ){
        String accessToken = ControllerAuxFunctions.getTokenFrom(authHeader);
        Person personEntity = personService.save(person, accessToken);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(personEntity.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<Object> update(
        @RequestHeader("Authorization") String authHeader,
        @PathVariable("id") long id,
        @RequestBody @Valid PersonRequestDTO personRequestDTO
    ){
        String accessToken = ControllerAuxFunctions.getTokenFrom(authHeader);
        personService.update(personRequestDTO, id, accessToken);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<Object> read(@PathVariable("id") long id){
        PersonRequestDTO personRequestDTO = personService.findById(id);
        return ResponseEntity.ok(personRequestDTO);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<Page<PersonRequestDTO>> readAll(
        @RequestParam(value = "page-number", defaultValue = "0") Integer pageNumber,
        @RequestParam(value = "page-length", defaultValue = "10") Integer pageLength,
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "cpf", required = false) String cpf,
        @RequestParam(value = "phone", required = false) String phone,
        @RequestParam(value = "birthday", required = false) LocalDate birthday,
        @RequestParam(value = "email", required = false) String email,
        @RequestParam(value = "cns", required = false) String cns,
        @RequestParam(value = "mother-name", required = false) String motherName
    ){
        return ResponseEntity.ok(personService.getAll(
            pageNumber,
            pageLength,
            name,
            cpf,
            phone,
            birthday,
            email,
            cns,
            motherName
        ));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<Object> delete(@PathVariable("id") long id){
        personService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
