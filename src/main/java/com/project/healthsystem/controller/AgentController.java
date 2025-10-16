package com.project.healthsystem.controller;

import com.project.healthsystem.controller.dto.AgentDTO;
import com.project.healthsystem.model.Agent;
import com.project.healthsystem.service.AgentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/agents")
@RequiredArgsConstructor
public class AgentController {
    private final AgentService agentService;

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody @Valid AgentDTO agentDTO){
        Agent agentEntity = agentService.save(agentDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(agentEntity.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable("id") long id, @RequestBody @Valid AgentDTO agentDTO){
        agentService.update(agentDTO, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> read(@PathVariable("id") long id){
        return ResponseEntity.ok(agentService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<AgentDTO>> readAll(){
        return ResponseEntity.ok(agentService.getAll());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") long id){
        agentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
