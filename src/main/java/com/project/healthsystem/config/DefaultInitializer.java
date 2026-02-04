package com.project.healthsystem.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.healthsystem.controller.dto.*;
import com.project.healthsystem.model.*;
import com.project.healthsystem.repository.LoginRepository;
import com.project.healthsystem.repository.PatientRepository;
import com.project.healthsystem.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DefaultInitializer implements ApplicationRunner {
    private final LoginRepository loginRepository;
    private final LoginService loginService;

    private final Environment environment;
    private final ObjectMapper objectMapper;

    private final AuthService authService;
    private final AgentService agentService;
    private final ConditionService conditionService;
    private final CategoryGroupService categoryGroupService;
    private final ServiceTypeService serviceTypeService;
    private final ProfessionalService professionalService;
    private final EmployeeService employeeService;
    private final PatientService patientService;
    private final AppointmentService appointmentService;
    private final RoleService roleService;

    private final PatientRepository patientRepository;

    @Value("${app.default-admin.username}")
    private String DEFAULT_ADMIN_USERNAME;

    private final String name = "ADMIN";
    private final String cpf = "12345678909";
    private final LocalDate birthday = LocalDate.now();
    private final String email = "admin@admin.com";

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Inicialização dos roles
        if(!roleService.hasAnyRole()){
            for(Roles enumRole: Roles.values()){
                Role role = new Role(enumRole);
                this.roleService.save(role);
            }
        }

        if(!loginRepository.existsByLogin(this.DEFAULT_ADMIN_USERNAME)){
            Person person = new Person();
            person.setName(this.name);
            person.setGender(Gender.MALE);
            person.setCpf(this.cpf);
            person.setBirthday(this.birthday);
            person.setEmail(this.email);

            loginService.createDefaultAdmin(person);

            System.out.println("Usuário padrão criado com sucesso!");
        }

        // Adicionando dados para testes:
        if(patientRepository.count() > 0) return;

        AuthRequestDTO authRequestDTO = new AuthRequestDTO();
        authRequestDTO.setLogin(
            this.environment.getProperty("app.default-admin.username")
        );
        authRequestDTO.setPassword(
            this.environment.getProperty("app.default-admin.password")
        );

        String accesToken = authService.authenticateUser(authRequestDTO).access_token();

        this.fillAgents(accesToken);
        this.fillConditions(accesToken);
        this.fillCategoryGroups(accesToken);
        this.fillServiceTypes(accesToken);
        this.fillProfessionals(accesToken);
        this.fillEmployees(accesToken);
        this.fillPatients(accesToken);
        this.fillAppointments(accesToken);
    }

    private void fillAgents(String accessToken) {
        try {
            InputStream is = new ClassPathResource("data/agents.json").getInputStream();

            List<AgentRequestDTO> agents =
                objectMapper.readValue(is, new TypeReference<List<AgentRequestDTO>>() {
            });

            for (AgentRequestDTO agentDTO : agents) {
                agentService.save(agentDTO, accessToken);
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar agents iniciais", e);
        }
    }

    private void fillConditions(String accessToken) {
        try {
            InputStream is = new ClassPathResource("data/conditions.json").getInputStream();

            List<ConditionRequestDTO> conditions =
                objectMapper.readValue(is, new TypeReference<List<ConditionRequestDTO>>() {
            });

            for (ConditionRequestDTO conditionDTO : conditions) {
                conditionService.save(conditionDTO, accessToken);
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar conditions iniciais", e);
        }
    }

    private void fillCategoryGroups(String accessToken) {
        try {
            InputStream is = new ClassPathResource("data/category_groups.json").getInputStream();

            List<CategoryGroupRequestDTO> categoryGroups =
                objectMapper.readValue(is, new TypeReference<List<CategoryGroupRequestDTO>>() {
            });

            for (CategoryGroupRequestDTO categoryGroupsDTO : categoryGroups) {
                categoryGroupService.save(categoryGroupsDTO, accessToken);
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar categoryGroups iniciais", e);
        }
    }

    private void fillServiceTypes(String accessToken) {
        try {
            InputStream is = new ClassPathResource("data/service_types.json").getInputStream();

            List<ServiceTypeRequestDTO> serviceTypes =
                objectMapper.readValue(is, new TypeReference<List<ServiceTypeRequestDTO>>() {
            });

            for (ServiceTypeRequestDTO serviceTypeDTO : serviceTypes) {
                serviceTypeService.save(serviceTypeDTO, accessToken);
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar serviceTypes iniciais", e);
        }
    }

    private void fillProfessionals(String accessToken) {
        try {
            InputStream is = new ClassPathResource("data/professionals.json").getInputStream();

            List<ProfessionalRequestDTO> professionals =
                objectMapper.readValue(is, new TypeReference<List<ProfessionalRequestDTO>>() {
            });

            for (ProfessionalRequestDTO professionalDTO : professionals) {
                professionalService.save(professionalDTO, accessToken);
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar professionals iniciais", e);
        }
    }

    private void fillEmployees(String accessToken) {
        try {
            InputStream is = new ClassPathResource("data/employees.json").getInputStream();

            List<EmployeeRequestDTO> employees =
                objectMapper.readValue(is, new TypeReference<List<EmployeeRequestDTO>>() {
            });

            for (EmployeeRequestDTO employeeDTO : employees) {
                employeeService.save(employeeDTO, accessToken);
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar employees iniciais", e);
        }
    }

    private void fillPatients(String accessToken){
        try {
            InputStream is = new ClassPathResource("data/patients.json").getInputStream();

            List<PatientRequestDTO> patients =
                objectMapper.readValue(is, new TypeReference<List<PatientRequestDTO>>() {});

            for(PatientRequestDTO patientDTO : patients){
                patientService.save(patientDTO, accessToken);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar patients iniciais", e);
        }
    }

    private void fillAppointments(String accessToken){
        try {
            InputStream is = new ClassPathResource("data/appointments.json").getInputStream();

            List<AppointmentRequestDTO> appointments =
                objectMapper.readValue(is, new TypeReference<List<AppointmentRequestDTO>>() {});

            for(AppointmentRequestDTO appointmentDTO : appointments){
                appointmentService.save(appointmentDTO, accessToken);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar appointments iniciais", e);
        }
    }
}
