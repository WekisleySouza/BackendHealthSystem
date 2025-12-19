package com.project.healthsystem.controller;

import com.project.healthsystem.controller.common.ControllerAuxFunctions;
import com.project.healthsystem.controller.dto.EmployeeRequestDTO;
import com.project.healthsystem.controller.dto.EmployeeResponseDTO;
import com.project.healthsystem.model.Employee;
import com.project.healthsystem.service.EmployeeService;
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
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> save(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody @Valid EmployeeRequestDTO employeeRequestDTO
    ){
        String accessToken = ControllerAuxFunctions.getTokenFrom(authHeader);
        Employee employeeEntity = service.save(employeeRequestDTO, accessToken);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(employeeEntity.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Object> update(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("id") long id,
            @RequestBody @Valid EmployeeRequestDTO employeeRequestDTO
    ){
        String accessToken = ControllerAuxFunctions.getTokenFrom(authHeader);
        service.update(employeeRequestDTO, id, accessToken);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<Object> read(@PathVariable("id") long id){
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Page<EmployeeResponseDTO>> readAll(
        @RequestParam(value = "page-number", defaultValue = "0") Integer pageNumber,
        @RequestParam(value = "page-length", defaultValue = "10") Integer pageLength,
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "cpf", required = false) String cpf,
        @RequestParam(value = "phone", required = false) String phone,
        @RequestParam(value = "birthday", required = false) LocalDate birthday,
        @RequestParam(value = "email", required = false) String email
    ){
        Page<EmployeeResponseDTO> employeeRequestDTOS = service.getAll(
            pageNumber,
            pageLength,
            name,
            cpf,
            phone,
            birthday,
            email
        );
        return ResponseEntity.ok(employeeRequestDTOS);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Object> delete(@PathVariable("id") long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
