package com.project.healthsystem.controller;

import com.project.healthsystem.controller.dto.ErrorResponseDTO;
import com.project.healthsystem.controller.dto.SpecialityDTO;
import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.Speciality;
import com.project.healthsystem.service.SpecialityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/specialities")
@RequiredArgsConstructor
public class SpecialityController {

    private final SpecialityService specialityService;

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody SpecialityDTO specialityDto){
        Speciality speciality = specialityService.save(specialityDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(speciality.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> update(@PathVariable("id") long id, @RequestBody SpecialityDTO specialityDto){
        try{
            specialityService.update(specialityDto, id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException err){
            ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.notFound(err.getMessage());
            return ResponseEntity.status(errorResponseDTO.status()).body(errorResponseDTO);
        }
    }

    @GetMapping
    public ResponseEntity<List<SpecialityDTO>> readAll(){
        List<SpecialityDTO> specialityDTOS = specialityService.getAll();
        return ResponseEntity.ok(specialityDTOS);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") long id){
        try{
            specialityService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException err){
            ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.notFound(err.getMessage());
            return ResponseEntity.status(errorResponseDTO.status()).body(errorResponseDTO);
        }

    }
}