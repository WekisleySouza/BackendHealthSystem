package com.project.healthsystem.controller;

import com.project.healthsystem.controller.dto.ConditionDTO;
import com.project.healthsystem.model.Condition;
import com.project.healthsystem.service.ConditionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/conditions")
@RequiredArgsConstructor
public class ConditionController {

    private final ConditionService conditionService;

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody @Valid ConditionDTO conditionDTO){
        Condition conditionEntity = conditionService.save(conditionDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(conditionEntity.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> update(@PathVariable("id") long id, @RequestBody @Valid ConditionDTO conditionDTO){
            conditionService.update(conditionDTO, id);
            return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ConditionDTO>> readAll(){
        List<ConditionDTO> conditionDTOS = conditionService.getAll();
        return ResponseEntity.ok(conditionDTOS);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") long id){
        conditionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
