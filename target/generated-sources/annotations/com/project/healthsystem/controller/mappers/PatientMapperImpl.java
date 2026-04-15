package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.PatientResponseDTO;
import com.project.healthsystem.controller.dto.basic_requests.PatientRequestDTO;
import com.project.healthsystem.model.Patient;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-15T16:21:22-0300",
    comments = "version: 1.6.0, compiler: javac, environment: Java 21.0.10 (Microsoft)"
)
@Component
public class PatientMapperImpl extends PatientMapper {

    @Override
    public Patient toEntity(PatientRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Patient patient = new Patient();

        patient.setTeamName( dto.getTeamName() );
        patient.setTeamINE( dto.getTeamINE() );
        patient.setMicroArea( dto.getMicroArea() );
        patient.setOrigin( dto.getOrigin() );
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
        patientResponseDTO.setTeamName( entity.getTeamName() );
        patientResponseDTO.setTeamINE( entity.getTeamINE() );
        patientResponseDTO.setMicroArea( entity.getMicroArea() );
        patientResponseDTO.setOrigin( entity.getOrigin() );
        patientResponseDTO.setResponsibleId( entity.getResponsibleId() );
        patientResponseDTO.setName( entity.getPerson().getName() );
        patientResponseDTO.setGender( entity.getPerson().getGender().getLabel() );
        patientResponseDTO.setBirthday( entity.getPerson().getBirthday() );
        patientResponseDTO.setCpf( entity.getPerson().getCpf() );
        patientResponseDTO.setSex( entity.getPerson().getSex().getLabel() );
        patientResponseDTO.setCellPhone( entity.getPerson().getCellPhone() );
        patientResponseDTO.setResidentialPhone( entity.getPerson().getResidentialPhone() );
        patientResponseDTO.setContactPhone( entity.getPerson().getContactPhone() );
        patientResponseDTO.setAddress( entity.getPerson().getAddress() );
        patientResponseDTO.setEmail( entity.getPerson().getEmail() );

        return patientResponseDTO;
    }
}
