package com.project.healthsystem.controller;

import com.project.healthsystem.controller.dto.AppointmentDTO;
import com.project.healthsystem.model.Appointment;
import com.project.healthsystem.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentsController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody @Valid AppointmentDTO appointmentDTO){
        Appointment appointment = appointmentService.save(appointmentDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(appointment.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> update(@PathVariable("id") long id, @RequestBody @Valid AppointmentDTO appointmentDTO){
        appointmentService.update(appointmentDTO, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<AppointmentDTO>> readAll(){
        List<AppointmentDTO> appointmentDTOS = appointmentService.getAll();
        return ResponseEntity.ok(appointmentDTOS);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") long id){
        appointmentService.delete(id);
        return ResponseEntity.notFound().build();
    }
}
