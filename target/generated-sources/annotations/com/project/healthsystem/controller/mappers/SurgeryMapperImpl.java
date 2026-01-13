package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.SurgeryRequestDTO;
import com.project.healthsystem.controller.dto.SurgeryResponseDTO;
import com.project.healthsystem.model.Surgery;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-13T18:09:26-0300",
    comments = "version: 1.6.0, compiler: javac, environment: Java 21.0.9 (Ubuntu)"
)
@Component
public class SurgeryMapperImpl extends SurgeryMapper {

    @Override
    public Surgery toEntity(SurgeryRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Surgery surgery = new Surgery();

        surgery.setDateTime( dto.getDateTime() );
        surgery.setPersonName( dto.getPersonName() );
        surgery.setSurgeryRisk( dto.getSurgeryRisk() );
        surgery.setLocation( dto.getLocation() );
        surgery.setConclusion( dto.getConclusion() );
        surgery.setSusEasy( dto.getSusEasy() );
        surgery.setSesap( dto.getSesap() );
        surgery.setProcedureDate( dto.getProcedureDate() );
        surgery.setAnesthesicRisk( dto.getAnesthesicRisk() );
        surgery.setObservation( dto.getObservation() );

        return surgery;
    }

    @Override
    public SurgeryResponseDTO toDto(Surgery entity) {
        if ( entity == null ) {
            return null;
        }

        SurgeryResponseDTO surgeryResponseDTO = new SurgeryResponseDTO();

        surgeryResponseDTO.setId( entity.getId() );
        surgeryResponseDTO.setDateTime( entity.getDateTime() );
        surgeryResponseDTO.setPersonName( entity.getPersonName() );
        surgeryResponseDTO.setSurgeryRisk( entity.getSurgeryRisk() );
        surgeryResponseDTO.setLocation( entity.getLocation() );
        surgeryResponseDTO.setConclusion( entity.getConclusion() );
        surgeryResponseDTO.setSusEasy( entity.getSusEasy() );
        surgeryResponseDTO.setSesap( entity.getSesap() );
        surgeryResponseDTO.setProcedureDate( entity.getProcedureDate() );
        surgeryResponseDTO.setAnesthesicRisk( entity.getAnesthesicRisk() );
        surgeryResponseDTO.setObservation( entity.getObservation() );

        surgeryResponseDTO.setSurgeryTypeId( entity.getSurgeryTypeId() );

        return surgeryResponseDTO;
    }
}
