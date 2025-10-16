package com.project.healthsystem.controller;

import com.project.healthsystem.controller.dto.StatusDTO;
import com.project.healthsystem.model.Status;
import com.project.healthsystem.service.StatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/status")
@RequiredArgsConstructor
public class StatusController {

    private final StatusService statusService;

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody @Valid StatusDTO statusDTO){
        Status statusEntity = statusService.save(statusDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(statusEntity.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> update(@PathVariable("id") long id, @RequestBody @Valid StatusDTO statusDTO){
        statusService.update(statusDTO, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<StatusDTO>> readAll(){
        List<StatusDTO> statusDTOS = statusService.getAll();
        return ResponseEntity.ok(statusDTOS);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") long id){
        statusService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
