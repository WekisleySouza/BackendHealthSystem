package com.project.healthsystem.controller;

import com.project.healthsystem.controller.dto.ConditionRequestDTO;
import com.project.healthsystem.model.Condition;
import com.project.healthsystem.service.ConditionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/conditions")
@RequiredArgsConstructor
public class ConditionController {
    private final ConditionService conditionService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<Void> save(@RequestBody @Valid ConditionRequestDTO conditionRequestDTO){
        Condition conditionEntity = conditionService.save(conditionRequestDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(conditionEntity.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<Object> update(@PathVariable("id") long id, @RequestBody @Valid ConditionRequestDTO conditionRequestDTO){
            conditionService.update(conditionRequestDTO, id);
            return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<Page<ConditionRequestDTO>> readAll(
        @RequestParam(value = "page-number", defaultValue = "0") Integer pageNumber,
        @RequestParam(value = "page-length", defaultValue = "10") Integer pageLength
    ){
        return ResponseEntity.ok(conditionService.getAll(pageNumber, pageLength));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<Object> delete(@PathVariable("id") long id){
        conditionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
