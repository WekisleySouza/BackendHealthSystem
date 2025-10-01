package com.project.healthsystem.controller;

import com.project.healthsystem.controller.dto.ConsultationDTO;
import com.project.healthsystem.controller.dto.ErrorResponseDTO;
import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.Consultation;
import com.project.healthsystem.service.ConsultationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/consultations")
public class ConsultationController {

    private final ConsultationService consultationService;

    public ConsultationController(ConsultationService consultationService){
        this.consultationService = consultationService;
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody ConsultationDTO consultationDTO){
        Consultation consultation = consultationService.save(consultationDTO);

        if(consultation == null){
            return ResponseEntity.badRequest().build();
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(consultation.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> update(@PathVariable("id") long id, @RequestBody ConsultationDTO consultationDTO){
        try {
            consultationService.update(consultationDTO, id);
            return ResponseEntity.noContent().build();
        } catch (NoClassDefFoundError err){
            ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.notFound(err.getMessage());
            return ResponseEntity.status(errorResponseDTO.status()).body(errorResponseDTO);
        }
    }

    @GetMapping
    public ResponseEntity<List<ConsultationDTO>> readAll(){
        List<ConsultationDTO> consultationDTOS = consultationService.getAll();
        return ResponseEntity.ok(consultationDTOS);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") long id){
        try {
            consultationService.delete(id);
            return ResponseEntity.notFound().build();
        } catch (NotFoundException err){
            ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.notFound(err.getMessage());
            return ResponseEntity.status(errorResponseDTO.status()).body(errorResponseDTO);
        }
    }
}
