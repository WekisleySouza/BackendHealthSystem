package com.project.healthsystem.controller;

import com.project.healthsystem.controller.dto.AppointmentDTO;
import com.project.healthsystem.controller.dto.ErrorResponseDTO;
import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.Appointment;
import com.project.healthsystem.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/consultations")
public class AppointmentsController {

    private final AppointmentService appointmentService;

    public AppointmentsController(AppointmentService appointmentService){
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody AppointmentDTO appointmentDTO){
        Appointment appointment = appointmentService.save(appointmentDTO);

        if(appointment == null){
            return ResponseEntity.badRequest().build();
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(appointment.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> update(@PathVariable("id") long id, @RequestBody AppointmentDTO appointmentDTO){
        try {
            appointmentService.update(appointmentDTO, id);
            return ResponseEntity.noContent().build();
        } catch (NoClassDefFoundError err){
            ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.notFound(err.getMessage());
            return ResponseEntity.status(errorResponseDTO.status()).body(errorResponseDTO);
        }
    }

    @GetMapping
    public ResponseEntity<List<AppointmentDTO>> readAll(){
        List<AppointmentDTO> appointmentDTOS = appointmentService.getAll();
        return ResponseEntity.ok(appointmentDTOS);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") long id){
        try {
            appointmentService.delete(id);
            return ResponseEntity.notFound().build();
        } catch (NotFoundException err){
            ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.notFound(err.getMessage());
            return ResponseEntity.status(errorResponseDTO.status()).body(errorResponseDTO);
        }
    }
}
