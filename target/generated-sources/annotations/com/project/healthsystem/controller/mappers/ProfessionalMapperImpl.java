package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.ProfessionalRequestDTO;
import com.project.healthsystem.controller.dto.ProfessionalResponseDTO;
import com.project.healthsystem.model.Professional;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-13T18:09:26-0300",
    comments = "version: 1.6.0, compiler: javac, environment: Java 21.0.9 (Ubuntu)"
)
@Component
public class ProfessionalMapperImpl extends ProfessionalMapper {

    @Override
    public Professional toEntity(ProfessionalRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Professional professional = new Professional();

        professional.setPerson( map(dto) );

        return professional;
    }

    @Override
    public ProfessionalResponseDTO toDto(Professional entity) {
        if ( entity == null ) {
            return null;
        }

        ProfessionalResponseDTO professionalResponseDTO = new ProfessionalResponseDTO();

        professionalResponseDTO.setId( entity.getId() );

        professionalResponseDTO.setCpf( entity.getPerson().getCpf() );
        professionalResponseDTO.setName( entity.getPerson().getName() );
        professionalResponseDTO.setPhone( entity.getPerson().getPhone() );
        professionalResponseDTO.setBirthday( entity.getPerson().getBirthday() );
        professionalResponseDTO.setEmail( entity.getPerson().getEmail() );

        return professionalResponseDTO;
    }
}
