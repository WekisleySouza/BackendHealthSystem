package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.ConditionResponseDTO;
import com.project.healthsystem.controller.dto.basic_requests.ConditionRequestDTO;
import com.project.healthsystem.model.Condition;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-08T16:24:37-0300",
    comments = "version: 1.6.0, compiler: javac, environment: Java 21.0.10 (Microsoft)"
)
@Component
public class ConditionMapperImpl extends ConditionMapper {

    @Override
    public Condition toEntity(ConditionRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Condition condition = new Condition();

        condition.setSpecification( dto.getSpecification() );

        return condition;
    }

    @Override
    public ConditionResponseDTO toDto(Condition entity) {
        if ( entity == null ) {
            return null;
        }

        ConditionResponseDTO conditionResponseDTO = new ConditionResponseDTO();

        conditionResponseDTO.setId( entity.getId() );
        conditionResponseDTO.setSpecification( entity.getSpecification() );

        return conditionResponseDTO;
    }
}
