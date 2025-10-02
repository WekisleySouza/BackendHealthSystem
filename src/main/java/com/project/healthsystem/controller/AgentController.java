package com.project.healthsystem.controller;

import com.project.healthsystem.controller.dto.AgentDTO;
import com.project.healthsystem.controller.dto.ErrorResponseDTO;
import com.project.healthsystem.exceptions.DuplicatedRegisterException;
import com.project.healthsystem.model.Agent;
import com.project.healthsystem.service.AgentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/agents")
public class AgentController {
    private final AgentService agentService;

    public AgentController(AgentService agentService){
        this.agentService = agentService;
    }

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody @Valid AgentDTO agentDTO){
        try{
            Agent agentEntity = agentDTO.mappingToAgent();
            agentService.save(agentEntity);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(agentEntity.getId())
                    .toUri();

            return ResponseEntity.created(location).build();
        } catch (DuplicatedRegisterException err){
            var errorDTO = ErrorResponseDTO.conflict(err.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable("id") long id, @RequestBody AgentDTO agentDTO){
        Optional<Agent> agentOptional = agentService.findById(id);

        if(agentOptional.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        var agent = agentOptional.get();

        agent.mappingFromAgentDTO(agentDTO);

        agentService.update(agent);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgentDTO> read(@PathVariable("id") long id){
        Optional<Agent> agentOptional = agentService.findById(id);

        if(agentOptional.isPresent()){
            Agent agent = agentOptional.get();
            AgentDTO agentDto = new AgentDTO(agent);
            return ResponseEntity.ok(agentDto);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<AgentDTO>> readAll(){
        List<Agent> agents = agentService.getAll();

        List<AgentDTO> agentDTOS = agents.stream().map(
                agent -> new AgentDTO(agent)
        ).collect(Collectors.toList());

        return ResponseEntity.ok(agentDTOS);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") long id){
        Optional<Agent> agentOptional = agentService.findById(id);

        if(agentOptional.isPresent()){
            agentService.delete(agentOptional.get());
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}
