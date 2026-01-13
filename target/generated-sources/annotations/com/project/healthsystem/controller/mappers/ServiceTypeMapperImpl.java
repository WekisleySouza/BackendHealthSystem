package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.ServiceTypeRequestDTO;
import com.project.healthsystem.controller.dto.ServiceTypeResponseDTO;
import com.project.healthsystem.model.ServiceType;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-13T18:09:26-0300",
    comments = "version: 1.6.0, compiler: javac, environment: Java 21.0.9 (Ubuntu)"
)
@Component
public class ServiceTypeMapperImpl extends ServiceTypeMapper {

    @Override
    public ServiceType toEntity(ServiceTypeRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        ServiceType serviceType = new ServiceType();

        serviceType.setName( dto.getName() );
        serviceType.setValue( dto.getValue() );
        serviceType.setType( dto.getType() );

        return serviceType;
    }

    @Override
    public ServiceTypeResponseDTO toDto(ServiceType entity) {
        if ( entity == null ) {
            return null;
        }

        ServiceTypeResponseDTO serviceTypeResponseDTO = new ServiceTypeResponseDTO();

        serviceTypeResponseDTO.setId( entity.getId() );
        serviceTypeResponseDTO.setName( entity.getName() );
        serviceTypeResponseDTO.setValue( entity.getValue() );
        serviceTypeResponseDTO.setType( entity.getType() );

        serviceTypeResponseDTO.setCategoryGroupId( entity.getCategoryGroupId() );

        return serviceTypeResponseDTO;
    }
}
