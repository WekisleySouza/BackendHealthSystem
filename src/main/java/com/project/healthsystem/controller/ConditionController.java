package com.project.healthsystem.controller;

import com.project.healthsystem.controller.dto.ConditionDTO;
import com.project.healthsystem.controller.dto.ErrorResponseDTO;
import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.Condition;
import com.project.healthsystem.service.ConditionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/conditions")
@RequiredArgsConstructor
public class ConditionController {

    private final ConditionService conditionService;

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody ConditionDTO conditionDTO){
        Condition conditionEntity = conditionDTO.mappingToCondition();

        conditionService.save(conditionEntity);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(conditionEntity.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> update(@PathVariable("id") long id, @RequestBody ConditionDTO conditionDTO){
        try{
            conditionService.update(conditionDTO, id);

            return ResponseEntity.noContent().build();
        } catch (NotFoundException err){
            ErrorResponseDTO errorDTO = ErrorResponseDTO.notFound(err.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        }
    }

    @GetMapping
    public ResponseEntity<List<ConditionDTO>> readAll(){
        List<ConditionDTO> conditionDTOS = conditionService.getAll();
        return ResponseEntity.ok(conditionDTOS);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") long id){
        try{
            conditionService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException err){
            ErrorResponseDTO errorDTO = ErrorResponseDTO.notFound(err.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        }
    }
}
