package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.AuthRequestDTO;
import com.project.healthsystem.controller.dto.AuthResponseDTO;
import com.project.healthsystem.controller.dto.ProfileResponseDTO;
import com.project.healthsystem.model.Employee;
import com.project.healthsystem.model.Login;
import com.project.healthsystem.model.Person;
import com.project.healthsystem.model.Roles;
import com.project.healthsystem.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;

    private final RefreshTokenService refreshTokenService;
    private final LoginService loginService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthResponseDTO authenticateUser(AuthRequestDTO authRequestDTO){
        // Autentica com o AuthenticationManager do Spring
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                authRequestDTO.getLogin(),
                authRequestDTO.getPassword()
            )
        );

        Login login = loginService.findByLogin(authRequestDTO.getLogin());
        String accessToken = jwtTokenProvider.generateToken(login.getLogin(), login.getRole().name());
        String refreshToken = refreshTokenService.createRefreshToken(login);

        return new AuthResponseDTO(
            login.getLogin(),
            refreshToken,
            accessToken,
            login.getRole().getLabel()
        );
    }

    public String getNewAccessToken(String refreshToken){
        Login login = refreshTokenService.validate(refreshToken);
        String newAccessToken = jwtTokenProvider.generateToken(
                login.getLogin(),
                login.getRole().name()
        );

        return newAccessToken;
    }

    public void  revokeRefreshToken(String refreshToken){
        refreshTokenService.revoke(refreshToken);
    }

    public ProfileResponseDTO getMyProfile(String token){
        Roles role = Roles.fromLabel(jwtTokenProvider.getRole(token));
        ProfileResponseDTO profileResponseDTO = new ProfileResponseDTO();
        if(role == Roles.USER){
            Person person = jwtTokenProvider.getPerson(token);
            profileResponseDTO.setName(person.getName());
            profileResponseDTO.setCpf(person.getCpf());
            profileResponseDTO.setPhone(person.getPhone());
            profileResponseDTO.setBirthday(person.getBirthday());
            profileResponseDTO.setEmail(person.getEmail());
            profileResponseDTO.setCns(person.getCns());
            profileResponseDTO.setMotherName(person.getMotherName());
        } else {
            Employee employee = jwtTokenProvider.getEmployee(token);
            profileResponseDTO.setName(employee.getName());
            profileResponseDTO.setCpf(employee.getCpf());
            profileResponseDTO.setPhone(employee.getPhone());
            profileResponseDTO.setBirthday(employee.getBirthday());
            profileResponseDTO.setEmail(employee.getEmail());

        }
        return profileResponseDTO;
    }
}
