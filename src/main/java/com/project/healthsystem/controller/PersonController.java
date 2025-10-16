package com.project.healthsystem.controller;

import com.project.healthsystem.controller.dto.PersonDTO;
import com.project.healthsystem.model.Person;
import com.project.healthsystem.service.PersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody @Valid PersonDTO person){
        Person personEntity = personService.save(person);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(personEntity.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> update(@PathVariable("id") long id, @RequestBody @Valid PersonDTO personDTO){
        personService.update(personDTO, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> read(@PathVariable("id") long id){
        PersonDTO personDTO = personService.findById(id);
        return ResponseEntity.ok(personDTO);
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
        personService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
