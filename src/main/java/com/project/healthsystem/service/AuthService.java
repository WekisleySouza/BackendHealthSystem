package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.AuthRequestDTO;
import com.project.healthsystem.controller.dto.AuthResponseDTO;
import com.project.healthsystem.controller.dto.ProfileResponseDTO;
import com.project.healthsystem.model.*;
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
    private final PatientService patientService;
    private final EmployeeService employeeService;
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
        String accessToken = jwtTokenProvider.generateToken(login.getLogin(), login.getPerson().getRoles());
        String refreshToken = refreshTokenService.createRefreshToken(login);

        return new AuthResponseDTO(
            login.getLogin(),
            refreshToken,
            accessToken,
            login.getPerson().getRoles()
        );
    }

    public String getNewAccessToken(String refreshToken){
        Login login = refreshTokenService.validate(refreshToken);
        String newAccessToken = jwtTokenProvider.generateToken(
            login.getLogin(),
            login.getPerson().getRoles()
        );

        return newAccessToken;
    }

    public void  revokeRefreshToken(String refreshToken){
        refreshTokenService.revoke(refreshToken);
    }

    public ProfileResponseDTO getMyProfile(String token){
        Roles role = Roles.fromLabel(jwtTokenProvider.getRole(token));
        ProfileResponseDTO profileResponseDTO = new ProfileResponseDTO();
        Person person = jwtTokenProvider.getPerson(token);
        if(role == Roles.PATIENT){
            Patient patient = patientService.getByCpf(person.getCpf());
            profileResponseDTO.setName(patient.getPerson().getName());
            profileResponseDTO.setCpf(patient.getPerson().getCpf());
            profileResponseDTO.setPhone(patient.getPerson().getPhone());
            profileResponseDTO.setBirthday(patient.getPerson().getBirthday());
            profileResponseDTO.setEmail(patient.getPerson().getEmail());
            profileResponseDTO.setCns(patient.getCns());
            profileResponseDTO.setMotherName(patient.getMotherName());
        } else {
            Employee employee = employeeService.findByCpf(person.getCpf());
            profileResponseDTO.setName(employee.getPerson().getName());
            profileResponseDTO.setCpf(employee.getPerson().getCpf());
            profileResponseDTO.setPhone(employee.getPerson().getPhone());
            profileResponseDTO.setBirthday(employee.getPerson().getBirthday());
            profileResponseDTO.setEmail(employee.getPerson().getEmail());

        }
        return profileResponseDTO;
    }
}
