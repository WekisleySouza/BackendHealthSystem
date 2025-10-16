package com.project.healthsystem.controller;

import com.project.healthsystem.controller.dto.EmployeeDTO;
import com.project.healthsystem.model.Employee;
import com.project.healthsystem.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService service;

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody @Valid EmployeeDTO employeeDTO){
        Employee employeeEntity = service.save(employeeDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(employeeEntity.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> update(@PathVariable("id") long id, @RequestBody @Valid EmployeeDTO employeeDTO){
        service.update(employeeDTO, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> readAll(){
        List<EmployeeDTO> employeeDTOS = service.getAll();
        return ResponseEntity.ok(employeeDTOS);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
