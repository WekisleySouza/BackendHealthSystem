package com.project.healthsystem.controller;

import com.project.healthsystem.controller.dto.ProfessionalDTO;
import com.project.healthsystem.model.Professional;
import com.project.healthsystem.service.ProfessionalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/professionals")
@RequiredArgsConstructor
public class ProfessionalController {

    private final ProfessionalService professionalService;

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody @Valid ProfessionalDTO professionalDto){
        Professional professionalEntity = professionalService.save(professionalDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(professionalEntity.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> update(@PathVariable("id") long id, @RequestBody @Valid ProfessionalDTO professionalDto){
        professionalService.update(professionalDto, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ProfessionalDTO>> readAll(){
        List<ProfessionalDTO> professionalDTOS = professionalService.getAll();
        return ResponseEntity.ok(professionalDTOS);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") long id){
        professionalService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
