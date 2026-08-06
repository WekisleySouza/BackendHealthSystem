package com.project.healthsystem;

import builders.LoginBuilder;
import builders.PersonBuilder;
import builders.RoleBuilder;
import com.project.healthsystem.model.Login;
import com.project.healthsystem.model.Person;
import com.project.healthsystem.model.Role;
import com.project.healthsystem.model.Roles;
import com.project.healthsystem.repository.LoginRepository;
import com.project.healthsystem.repository.PersonRepository;
import com.project.healthsystem.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.JsonNode;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Locale;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class IntegrationTestBase extends DBTestConfig {

    @Autowired
    protected PersonRepository personRepository;
    @Autowired
    protected LoginRepository loginRepository;
    @Autowired
    protected RoleRepository roleRepository;
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    private PasswordEncoder passwordEncoder;

    protected String adminToken;
    protected String managerToken;
    protected String employeeToken;
    protected String patientToken;
    protected Long userId;

    protected void loginConfig() throws Exception {
        createLogins();

        loginAdmin();
        loginManager();
        loginEmployee();
        loginPatient();
    }

    private void loginAdmin() throws Exception {
        String response = mockMvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "login": "admin",
                        "password": "1234567"
                    }
            """)
        )
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

        ObjectMapper mapper = new ObjectMapper();

        JsonNode json = mapper.readTree(response);

        this.adminToken = json.get("access_token").asText();
    }

    private void loginManager() throws Exception {
        String response = mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                    {
                        "login": "manager",
                        "password": "1234567"
                    }
            """)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper mapper = new ObjectMapper();

        JsonNode json = mapper.readTree(response);

        this.managerToken = json.get("access_token").asText();
    }
    
    private void loginEmployee() throws Exception {
        String response = mockMvc.perform(
            post("/auth/login")
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                    {
                        "login": "employee",
                        "password": "1234567"
                    }
            """)
          )
          .andExpect(status().isOk())
          .andReturn()
          .getResponse()
          .getContentAsString();
        
        ObjectMapper mapper = new ObjectMapper();
        
        JsonNode json = mapper.readTree(response);
        
        this.employeeToken = json.get("access_token").asText();
    }

    private void loginPatient() throws Exception {
        String response = mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                    {
                        "login": "patient",
                        "password": "1234567"
                    }
            """)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper mapper = new ObjectMapper();

        JsonNode json = mapper.readTree(response);

        this.patientToken = json.get("access_token").asText();
    }

    private void createLogins(){
        this.initRoles();

        createLogin(Roles.ADMIN);
        createLogin(Roles.MANAGER);
        createLogin(Roles.EMPLOYEE);
        createLogin(Roles.PATIENT);
    }

    private void createLogin(Roles roleLabel){
        Role role = roleRepository
            .findByRole(roleLabel)
            .orElse(null);

        Person user = PersonBuilder.builder()
                .name(roleLabel.getLabel().toLowerCase())
                .roles(role)
                .build();

        Person userSaved = personRepository.save(user);
        this.userId = userSaved.getId();

        Login login = LoginBuilder.builder()
            .withLogin(roleLabel.getLabel().toLowerCase())
            .withPassword(passwordEncoder.encode("1234567"))
            .withPerson(userSaved)
            .build();
        loginRepository.save(login);
    }

    private void initRoles(){
        for(Roles role : Roles.values()){
            if(roleRepository.findByRole(role).isEmpty()){
                roleRepository.save(new Role(role));
            }
        }
    }
}
