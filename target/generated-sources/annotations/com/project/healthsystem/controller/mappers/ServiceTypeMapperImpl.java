package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.ServiceTypeResponseDTO;
import com.project.healthsystem.controller.dto.basic_requests.ServiceTypeRequestDTO;
import com.project.healthsystem.model.ServiceType;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-15T16:21:22-0300",
    comments = "version: 1.6.0, compiler: javac, environment: Java 21.0.10 (Microsoft)"
)
@Component
public class ServiceTypeMapperImpl extends ServiceTypeMapper {

    @Override
    public ServiceType toEntity(ServiceTypeRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        ServiceType serviceType = new ServiceType();

        serviceType.setSigtapCode( dto.getSigtapCode() );
        serviceType.setName( dto.getName() );
        serviceType.setValue( dto.getValue() );

        serviceType.setType( com.project.healthsystem.model.ServiceTypes.fromLabel(dto.getType()) );

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
        serviceTypeResponseDTO.setSigtapCode( entity.getSigtapCode() );
        serviceTypeResponseDTO.setValue( entity.getValue() );

        serviceTypeResponseDTO.setCategoryGroupId( entity.getCategoryGroupId() );
        serviceTypeResponseDTO.setType( entity.getType().getLabel() );

        return serviceTypeResponseDTO;
    }
}
