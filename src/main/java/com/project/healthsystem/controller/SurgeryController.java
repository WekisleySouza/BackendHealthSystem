package com.project.healthsystem.controller;

import com.project.healthsystem.controller.dto.SurgeryDTO;
import com.project.healthsystem.model.Surgery;
import com.project.healthsystem.service.SurgeryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/surgeries")
@RequiredArgsConstructor
public class SurgeryController {

    private final SurgeryService surgeryService;

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody SurgeryDTO surgeryDTO){
        Surgery surgery = surgeryService.save(surgeryDTO);

        if(surgery == null){
            return ResponseEntity.badRequest().build();
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(surgery.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> update(@PathVariable("id") long id, @RequestBody SurgeryDTO surgeryDTO){
        surgeryService.update(surgeryDTO, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<SurgeryDTO>> readAll(){
        List<SurgeryDTO> surgeryDTOS = surgeryService.getAll();
        return ResponseEntity.ok(surgeryDTOS);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") long id){
        surgeryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
