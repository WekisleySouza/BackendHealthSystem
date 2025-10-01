package com.project.healthsystem.controller;

import com.project.healthsystem.controller.dto.CategoryGroupDTO;
import com.project.healthsystem.controller.dto.ErrorResponseDTO;
import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.CategoryGroup;
import com.project.healthsystem.service.CategoryGroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/category-groups")
public class CategoryGroupController {

    private final CategoryGroupService categoryGroupService;

    public CategoryGroupController(CategoryGroupService categoryGroupService){
        this.categoryGroupService = categoryGroupService;
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody CategoryGroupDTO categoryGroupDTO){
        CategoryGroup categoryGroupEntity = categoryGroupDTO.mappingToCategoryGroup();
        categoryGroupService.save(categoryGroupEntity);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(categoryGroupEntity.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> update(@PathVariable("id") long id, @RequestBody CategoryGroupDTO categoryGroupDTO){
        try{
            categoryGroupService.update(categoryGroupDTO, id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException err){
            ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.notFound(err.getMessage());
            return ResponseEntity.status(errorResponseDTO.status()).body(errorResponseDTO);
        }
    }

    @GetMapping
    public ResponseEntity<List<CategoryGroupDTO>> readAll(){
        List<CategoryGroupDTO> categoryGroupDTOS = categoryGroupService.getAll();
        return ResponseEntity.ok(categoryGroupDTOS);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") long id){
        try {
            return ResponseEntity.notFound().build();
        } catch (NotFoundException err){
            ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.notFound(err.getMessage());
            return ResponseEntity.status(errorResponseDTO.status()).body(errorResponseDTO);
        }

    }
}
