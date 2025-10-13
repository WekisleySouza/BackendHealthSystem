package com.project.healthsystem.controller;

import com.project.healthsystem.controller.dto.AgentDTO;
import com.project.healthsystem.controller.dto.ErrorResponseDTO;
import com.project.healthsystem.controller.mappers.AgentMapper;
import com.project.healthsystem.exceptions.DuplicatedRegisterException;
import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.Agent;
import com.project.healthsystem.service.AgentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/agents")
@RequiredArgsConstructor
public class AgentController {
    private final AgentService agentService;
    private final AgentMapper agentMapper;

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody @Valid AgentDTO agentDTO){
        try{
            Agent agentEntity = agentMapper.toEntity(agentDTO);
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
    public ResponseEntity<Object> update(@PathVariable("id") long id, @RequestBody AgentDTO agentDTO){
        try{
            agentService.update(agentDTO, id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException err){
            var errorDTO = ErrorResponseDTO.notFound(err.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> read(@PathVariable("id") long id){
        try{
            return ResponseEntity.ok(agentService.findById(id));
        } catch (NotFoundException err){
            var errorDTO = ErrorResponseDTO.notFound(err.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        }
    }

    @GetMapping
    public ResponseEntity<List<AgentDTO>> readAll(){
        return ResponseEntity.ok(agentService.getAll());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") long id){
        try{
            agentService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException err){
            var errorDTO = ErrorResponseDTO.notFound(err.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        }
    }
}
