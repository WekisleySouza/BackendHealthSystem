package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.EmployeeRequestDTO;
import com.project.healthsystem.controller.dto.EmployeeResponseDTO;
import com.project.healthsystem.model.Employee;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-13T18:09:26-0300",
    comments = "version: 1.6.0, compiler: javac, environment: Java 21.0.9 (Ubuntu)"
)
@Component
public class EmployeeMapperImpl extends EmployeeMapper {

    @Override
    public Employee toEntity(EmployeeRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Employee employee = new Employee();

        employee.setPerson( map(dto) );

        return employee;
    }

    @Override
    public EmployeeResponseDTO toDto(Employee entity) {
        if ( entity == null ) {
            return null;
        }

        EmployeeResponseDTO employeeResponseDTO = new EmployeeResponseDTO();

        employeeResponseDTO.setId( entity.getId() );

        employeeResponseDTO.setCpf( entity.getPerson().getCpf() );
        employeeResponseDTO.setName( entity.getPerson().getName() );
        employeeResponseDTO.setPhone( entity.getPerson().getPhone() );
        employeeResponseDTO.setBirthday( entity.getPerson().getBirthday() );
        employeeResponseDTO.setEmail( entity.getPerson().getEmail() );

        return employeeResponseDTO;
    }
}
