package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.SurgeryTypeResponseDTO;
import com.project.healthsystem.controller.dto.basic_requests.SurgeryTypeRequestDTO;
import com.project.healthsystem.model.SurgeryType;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-06T17:38:30-0300",
    comments = "version: 1.6.0, compiler: javac, environment: Java 21.0.10 (Ubuntu)"
)
@Component
public class SurgeryTypeMapperImpl extends SurgeryTypeMapper {

    @Override
    public SurgeryType toEntity(SurgeryTypeRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        SurgeryType surgeryType = new SurgeryType();

        surgeryType.setType( dto.getType() );

        return surgeryType;
    }

    @Override
    public SurgeryTypeResponseDTO toDto(SurgeryType entity) {
        if ( entity == null ) {
            return null;
        }

        SurgeryTypeResponseDTO surgeryTypeResponseDTO = new SurgeryTypeResponseDTO();

        surgeryTypeResponseDTO.setId( entity.getId() );
        surgeryTypeResponseDTO.setType( entity.getType() );

        return surgeryTypeResponseDTO;
    }
}
