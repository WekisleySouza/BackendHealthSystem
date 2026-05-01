package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.basic_requests.InstituitionRequestDTO;
import com.project.healthsystem.controller.dto.basic_responses.InstituitionResponseDTO;
import com.project.healthsystem.model.Instituition;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-01T16:18:05-0300",
    comments = "version: 1.6.0, compiler: javac, environment: Java 21.0.10 (Microsoft)"
)
@Component
public class InstituitionMapperImpl extends InstituitionMapper {

    @Override
    public Instituition toEntity(InstituitionRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Instituition instituition = new Instituition();

        instituition.setName( dto.getName() );
        instituition.setCep( dto.getCep() );
        instituition.setCityName( dto.getCityName() );
        instituition.setAddress( dto.getAddress() );
        instituition.setPhone( dto.getPhone() );
        instituition.setLinkLogo( dto.getLinkLogo() );

        return instituition;
    }

    @Override
    public InstituitionResponseDTO toDto(Instituition entity) {
        if ( entity == null ) {
            return null;
        }

        InstituitionResponseDTO instituitionResponseDTO = new InstituitionResponseDTO();

        instituitionResponseDTO.setId( entity.getId() );
        instituitionResponseDTO.setName( entity.getName() );
        instituitionResponseDTO.setCep( entity.getCep() );
        instituitionResponseDTO.setCityName( entity.getCityName() );
        instituitionResponseDTO.setAddress( entity.getAddress() );
        instituitionResponseDTO.setPhone( entity.getPhone() );
        instituitionResponseDTO.setLinkLogo( entity.getLinkLogo() );

        return instituitionResponseDTO;
    }
}
