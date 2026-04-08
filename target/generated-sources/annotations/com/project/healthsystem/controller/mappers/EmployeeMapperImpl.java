package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.EmployeeResponseDTO;
import com.project.healthsystem.controller.dto.basic_requests.EmployeeRequestDTO;
import com.project.healthsystem.model.Employee;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-08T11:49:23-0300",
    comments = "version: 1.6.0, compiler: javac, environment: Java 21.0.10 (Microsoft)"
)
@Component
public class EmployeeMapperImpl extends EmployeeMapper {

    @Override
    public Employee toEntity(EmployeeRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Employee employee = new Employee();

        employee.setActive( dto.isActive() );

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
        employeeResponseDTO.setActive( entity.isActive() );

        employeeResponseDTO.setCpf( entity.getPerson().getCpf() );
        employeeResponseDTO.setName( entity.getPerson().getName() );
        employeeResponseDTO.setGender( entity.getPerson().getGender().getLabel() );
        employeeResponseDTO.setSex( entity.getPerson().getSex().getLabel() );
        employeeResponseDTO.setCellPhone( entity.getPerson().getCellPhone() );
        employeeResponseDTO.setResidentialPhone( entity.getPerson().getResidentialPhone() );
        employeeResponseDTO.setContactPhone( entity.getPerson().getContactPhone() );
        employeeResponseDTO.setAddress( entity.getPerson().getAddress() );
        employeeResponseDTO.setBirthday( entity.getPerson().getBirthday() );
        employeeResponseDTO.setEmail( entity.getPerson().getEmail() );
        employeeResponseDTO.setRoles( entity.getPerson().getStringRoles() );

        return employeeResponseDTO;
    }
}
