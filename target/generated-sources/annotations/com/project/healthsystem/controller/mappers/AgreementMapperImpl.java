package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.basic_requests.AgreementRequestDTO;
import com.project.healthsystem.controller.dto.basic_responses.AgreementResponseDTO;
import com.project.healthsystem.model.Agreement;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-27T16:39:56-0300",
    comments = "version: 1.6.0, compiler: javac, environment: Java 21.0.11 (Microsoft)"
)
@Component
public class AgreementMapperImpl extends AgreementMapper {

    @Override
    public Agreement toEntity(AgreementRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Agreement agreement = new Agreement();

        agreement.setName( dto.getName() );

        return agreement;
    }

    @Override
    public AgreementResponseDTO toDto(Agreement entity) {
        if ( entity == null ) {
            return null;
        }

        AgreementResponseDTO agreementResponseDTO = new AgreementResponseDTO();

        agreementResponseDTO.setId( entity.getId() );
        agreementResponseDTO.setName( entity.getName() );

        return agreementResponseDTO;
    }
}
