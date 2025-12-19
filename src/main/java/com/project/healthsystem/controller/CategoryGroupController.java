package com.project.healthsystem.controller;

import com.project.healthsystem.controller.common.ControllerAuxFunctions;
import com.project.healthsystem.controller.dto.CategoryGroupRequestDTO;
import com.project.healthsystem.controller.dto.CategoryGroupResponseDTO;
import com.project.healthsystem.model.CategoryGroup;
import com.project.healthsystem.service.CategoryGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/category-groups")
@RequiredArgsConstructor
public class CategoryGroupController {

    private final CategoryGroupService categoryGroupService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> save(
        @RequestHeader("Authorization") String authHeader,
        @RequestBody @Valid CategoryGroupRequestDTO categoryGroupRequestDTO
    ){
        String accessToken = ControllerAuxFunctions.getTokenFrom(authHeader);
        CategoryGroup categoryGroup = categoryGroupService.save(categoryGroupRequestDTO, accessToken);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(categoryGroup.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Object> update(
        @RequestHeader("Authorization") String authHeader,
        @PathVariable("id") long id,
        @RequestBody @Valid CategoryGroupRequestDTO categoryGroupRequestDTO
    ){
        String accessToken = ControllerAuxFunctions.getTokenFrom(authHeader);
        categoryGroupService.update(categoryGroupRequestDTO, id, accessToken);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<Object> read(@PathVariable("id") long id){
        return ResponseEntity.ok(categoryGroupService.findById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<Page<CategoryGroupResponseDTO>> readAll(
        @RequestParam(value = "page-number", defaultValue = "0") Integer pageNumber,
        @RequestParam(value = "page-length", defaultValue = "10") Integer pageLength,
        @RequestParam(value = "name", required = false) String name
    ){
        return ResponseEntity.ok(categoryGroupService.getAll(pageNumber, pageLength, name));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Object> delete(@PathVariable("id") long id){
        categoryGroupService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
