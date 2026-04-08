package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.basic_requests.LoginRequestDTO;
import com.project.healthsystem.model.Login;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-08T11:49:23-0300",
    comments = "version: 1.6.0, compiler: javac, environment: Java 21.0.10 (Microsoft)"
)
@Component
public class LoginMapperImpl extends LoginMapper {

    @Override
    public Login toEntity(LoginRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Login login = new Login();

        login.setPassword( dto.getPassword() );

        return login;
    }

    @Override
    public LoginRequestDTO toDto(Login entity) {
        if ( entity == null ) {
            return null;
        }

        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();

        loginRequestDTO.setPassword( entity.getPassword() );

        return loginRequestDTO;
    }
}
