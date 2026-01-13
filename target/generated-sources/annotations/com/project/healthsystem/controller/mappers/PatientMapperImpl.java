package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.PatientRequestDTO;
import com.project.healthsystem.controller.dto.PatientResponseDTO;
import com.project.healthsystem.model.Patient;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-13T18:09:26-0300",
    comments = "version: 1.6.0, compiler: javac, environment: Java 21.0.9 (Ubuntu)"
)
@Component
public class PatientMapperImpl extends PatientMapper {

    @Override
    public Patient toEntity(PatientRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Patient patient = new Patient();

        patient.setCns( dto.getCns() );
        patient.setMotherName( dto.getMotherName() );
        if ( patient.getConditionsId() != null ) {
            List<Long> list = dto.getConditionsId();
            if ( list != null ) {
                patient.getConditionsId().addAll( list );
            }
        }

        patient.setPerson( map(dto) );

        return patient;
    }

    @Override
    public PatientResponseDTO toDto(Patient entity) {
        if ( entity == null ) {
            return null;
        }

        PatientResponseDTO patientResponseDTO = new PatientResponseDTO();

        patientResponseDTO.setId( entity.getId() );
        patientResponseDTO.setMotherName( entity.getMotherName() );
        patientResponseDTO.setCns( entity.getCns() );

        patientResponseDTO.setAgentId( entity.getAgentId() );
        patientResponseDTO.setConditionsId( entity.getConditionsId() );
        patientResponseDTO.setResponsibleId( entity.getResponsibleId() );

        return patientResponseDTO;
    }
}
