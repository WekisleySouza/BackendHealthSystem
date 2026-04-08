package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.ProfessionalResponseDTO;
import com.project.healthsystem.controller.dto.basic_requests.ProfessionalRequestDTO;
import com.project.healthsystem.model.Professional;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-08T16:24:37-0300",
    comments = "version: 1.6.0, compiler: javac, environment: Java 21.0.10 (Microsoft)"
)
@Component
public class ProfessionalMapperImpl extends ProfessionalMapper {

    @Override
    public Professional toEntity(ProfessionalRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Professional professional = new Professional();

        professional.setCns( dto.getCns() );
        professional.setCbo( dto.getCbo() );
        professional.setVinculation( dto.getVinculation() );
        professional.setDescription( dto.getDescription() );

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
        professionalResponseDTO.setCns( entity.getCns() );
        professionalResponseDTO.setCbo( entity.getCbo() );
        professionalResponseDTO.setVinculation( entity.getVinculation() );
        professionalResponseDTO.setDescription( entity.getDescription() );

        professionalResponseDTO.setCpf( entity.getPerson().getCpf() );
        professionalResponseDTO.setName( entity.getPerson().getName() );
        professionalResponseDTO.setGender( entity.getPerson().getGender().getLabel() );
        professionalResponseDTO.setSex( entity.getPerson().getSex().getLabel() );
        professionalResponseDTO.setCellPhone( entity.getPerson().getCellPhone() );
        professionalResponseDTO.setResidentialPhone( entity.getPerson().getResidentialPhone() );
        professionalResponseDTO.setContactPhone( entity.getPerson().getContactPhone() );
        professionalResponseDTO.setBirthday( entity.getPerson().getBirthday() );
        professionalResponseDTO.setAddress( entity.getPerson().getAddress() );
        professionalResponseDTO.setEmail( entity.getPerson().getEmail() );

        return professionalResponseDTO;
    }
}
