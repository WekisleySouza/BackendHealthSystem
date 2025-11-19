package com.project.healthsystem.controller;

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

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PersonController {
    private final PersonService personService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<Void> save(@RequestBody @Valid PersonRequestDTO person){
        Person personEntity = personService.save(person);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(personEntity.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<Object> update(@PathVariable("id") long id, @RequestBody @Valid PersonRequestDTO personRequestDTO){
        personService.update(personRequestDTO, id);
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
            // Caso não for usar, retirar estes parâmetros futuramente
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "page-number", defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "page-length", defaultValue = "10") Integer pageLength
    ){
        Page<PersonRequestDTO> personsDTO = personService.search(pageNumber, pageLength);
        return ResponseEntity.ok(personsDTO);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<Object> delete(@PathVariable("id") long id){
        personService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
