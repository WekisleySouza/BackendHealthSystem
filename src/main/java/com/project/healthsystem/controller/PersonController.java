package com.project.healthsystem.controller;

import com.project.healthsystem.controller.dto.ErrorResponseDTO;
import com.project.healthsystem.controller.dto.PersonDTO;
import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.Person;
import com.project.healthsystem.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/persons")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody PersonDTO person){
        Person personEntity = personService.save(person);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(personEntity.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> update(@PathVariable("id") long id, @RequestBody PersonDTO personDTO){
        try {
            personService.update(personDTO, id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException err){
            ErrorResponseDTO errorDTO = ErrorResponseDTO.notFound(err.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> read(@PathVariable("id") long id){
        try{
            PersonDTO personDTO = personService.findById(id);
            return ResponseEntity.ok(personDTO);
        } catch(NotFoundException err) {
            var errorDTO = ErrorResponseDTO.notFound(err.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        }
    }

    @GetMapping
    public ResponseEntity<List<PersonDTO>> readAll(
            // Caso não for usar, retirar estes parâmetros futuramente
            @RequestParam(value = "name", required = false) String name
    ){
        List<PersonDTO> personsDTO = personService.search();
        return ResponseEntity.ok(personsDTO);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") long id){
        try {
            personService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException err){
            ErrorResponseDTO errorDTO = ErrorResponseDTO.notFound(err.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        }
    }
}
