package com.project.healthsystem.controller;

import com.project.healthsystem.controller.dto.CategoryGroupDTO;
import com.project.healthsystem.model.CategoryGroup;
import com.project.healthsystem.service.CategoryGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/category-groups")
@RequiredArgsConstructor
public class CategoryGroupController {

    private final CategoryGroupService categoryGroupService;

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody @Valid CategoryGroupDTO categoryGroupDTO){
        CategoryGroup categoryGroup = categoryGroupService.save(categoryGroupDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(categoryGroup.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> update(@PathVariable("id") long id, @RequestBody @Valid CategoryGroupDTO categoryGroupDTO){
        categoryGroupService.update(categoryGroupDTO, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<CategoryGroupDTO>> readAll(){
        List<CategoryGroupDTO> categoryGroupDTOS = categoryGroupService.getAll();
        return ResponseEntity.ok(categoryGroupDTOS);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") long id){
        categoryGroupService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
