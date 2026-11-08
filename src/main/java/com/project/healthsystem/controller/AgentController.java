package com.project.healthsystem.controller;

import com.project.healthsystem.controller.docs.AgentApiDocs;

import com.project.healthsystem.controller.common.ControllerAuxFunctions;
import com.project.healthsystem.controller.common.Permissions;
import com.project.healthsystem.controller.dto.basic_requests.AgentRequestDTO;
import com.project.healthsystem.controller.dto.basic_responses.AgentResponseDTO;
import com.project.healthsystem.controller.dto.ErrorResponseDTO;
import com.project.healthsystem.controller.dto.simplified_info.AgentSimplifiedResponseDTO;
import com.project.healthsystem.model.Agent;
import com.project.healthsystem.service.AgentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;

@RestController
@RequestMapping("/agents")
@RequiredArgsConstructor

public class AgentController implements AgentApiDocs {
    private final AgentService agentService;

    @PostMapping
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER)

    public ResponseEntity<Object> save(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody @Valid AgentRequestDTO agentRequestDTO
    ){
        String accessToken = ControllerAuxFunctions.getTokenFrom(authHeader);
        Agent agentEntity = agentService.save(agentRequestDTO, accessToken);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(agentEntity.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER)

    public ResponseEntity<Object> update(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("id") long id,
            @RequestBody @Valid AgentRequestDTO agentRequestDTO
    ){
        String accessToken = ControllerAuxFunctions.getTokenFrom(authHeader);
        agentService.update(agentRequestDTO, id, accessToken);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)

    public ResponseEntity<Object> read(@PathVariable("id") long id){
        return ResponseEntity.ok(agentService.findById(id));
    }

    @GetMapping
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)

    public ResponseEntity<Page<AgentResponseDTO>> readAll(
            @RequestParam(value = "page-number", defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "page-length", defaultValue = "10") Integer pageLength,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "cpf", required = false) String cpf,
            @RequestParam(value = "birthday", required = false) LocalDate birthday,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "sex", required = false) String sex,
            @RequestParam(value = "cell-phone", required = false) String cellPhone,
            @RequestParam(value = "residential-phone", required = false) String residentialPhone,
            @RequestParam(value = "contact-phone", required = false) String contactPhone
    ){
        return ResponseEntity.ok(agentService.getAll(
                pageNumber,
                pageLength,
                name,
                gender,
                cpf,
                birthday,
                email,
                sex,
                cellPhone,
                residentialPhone,
                contactPhone
        ));
    }

    @GetMapping("/get-all-simplified")
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)

    public ResponseEntity<Page<AgentSimplifiedResponseDTO>> readAll(
            @RequestParam(value = "page-number", defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "page-length", defaultValue = "20") Integer pageLength
    ){
        return ResponseEntity.ok(agentService.getAllSimplified(
                pageNumber,
                pageLength
        ));
    }

    @PreAuthorize(Permissions.ADMIN_OR_MANAGER)
    @DeleteMapping("/{id}")

    public ResponseEntity<Object> delete(@PathVariable("id") long id){
        agentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
