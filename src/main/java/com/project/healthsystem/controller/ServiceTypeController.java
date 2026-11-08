package com.project.healthsystem.controller;

import com.project.healthsystem.controller.docs.ServiceTypeApiDocs;

import com.project.healthsystem.controller.common.ControllerAuxFunctions;
import com.project.healthsystem.controller.common.Permissions;
import com.project.healthsystem.controller.dto.ErrorResponseDTO;
import com.project.healthsystem.controller.dto.basic_requests.ServiceTypeRequestDTO;
import com.project.healthsystem.controller.dto.basic_responses.ServiceTypeResponseDTO;
import com.project.healthsystem.controller.dto.simplified_info.ServiceTypeSimplifiedResponseDTO;
import com.project.healthsystem.model.ServiceType;
import com.project.healthsystem.service.ServiceTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;

@RestController
@RequestMapping("/service-types")
@RequiredArgsConstructor

public class ServiceTypeController implements ServiceTypeApiDocs {
    private final ServiceTypeService serviceTypeService;

    @PostMapping
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER)

    public ResponseEntity<Void> save(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody @Valid ServiceTypeRequestDTO serviceTypeRequestDTO
    ){
        String accessToken = ControllerAuxFunctions.getTokenFrom(authHeader);
        ServiceType serviceType = serviceTypeService.save(serviceTypeRequestDTO, accessToken);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(serviceType.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("{id}")
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER)

    public ResponseEntity<Object> update(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("id") long id,
            @RequestBody @Valid ServiceTypeRequestDTO serviceTypeRequestDTO
    ){
        String accessToken = ControllerAuxFunctions.getTokenFrom(authHeader);
        serviceTypeService.update(serviceTypeRequestDTO, id, accessToken);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)

    public ResponseEntity<Object> read(@PathVariable("id") long id){
        return ResponseEntity.ok(serviceTypeService.findById(id));
    }

    @GetMapping("/get-all-simplified")
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)

    public ResponseEntity<Page<ServiceTypeSimplifiedResponseDTO>> readAllSimplified(
            @RequestParam(value = "page-number", defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "page-length", defaultValue = "10") Integer pageLength
    ){
        return ResponseEntity.ok(serviceTypeService.getAllSimplified(
                pageNumber,
                pageLength
        ));
    }

    @GetMapping

    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
    public ResponseEntity<Page<ServiceTypeResponseDTO>> readAll(
            @RequestParam(value = "page-number", defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "page-length", defaultValue = "10") Integer pageLength,
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "category-group-name", required = false) String categoryGroupName,
            @RequestParam(value = "value", required = false) BigDecimal value
    ){
        return ResponseEntity.ok(serviceTypeService.getAll(
            pageNumber,
            pageLength,
            code,
            name,
            type,
            categoryGroupName,
            value
        ));
    }

    @DeleteMapping("delete-replace-with-new/{id}")
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER)

    public ResponseEntity<Object> deleteWhenNewServiceType(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("id") long id,
            @RequestBody @Valid ServiceTypeRequestDTO serviceTypeRequestDTO
    ){
        String accessToken = ControllerAuxFunctions.getTokenFrom(authHeader);
        serviceTypeService.deleteWhenNewServiceType(id, serviceTypeRequestDTO, accessToken);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("delete-replace-with/{id}")
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER)
    public ResponseEntity<Integer> deleteWhenInnered(
        @PathVariable("id") long id,
        @RequestParam(required=true) Long replaceWithId
    ){
        int replacedNumber = serviceTypeService.deleteWhenInnered(id, replaceWithId);
        return ResponseEntity.ok(replacedNumber);
    }

    @DeleteMapping("{id}")
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER)

    public ResponseEntity<Object> delete(@PathVariable("id") long id){
        serviceTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER)

    public ResponseEntity<String> importExcel(
        
        @RequestParam("file") MultipartFile file
    ){
        serviceTypeService.importCsv(file);
        return ResponseEntity.ok("Importado com sucesso!");
    }
}
