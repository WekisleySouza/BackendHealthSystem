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

    protected String token;
    protected Long userId;

    protected void login() throws Exception {
        String response = mockMvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "login": "test",
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

        this.token = json.get("access_token").asText();
    }

    protected void createLogin(){
        this.initRoles();

        Role roleAdmin = roleRepository
            .findByRole(Roles.ADMIN)
            .orElse(null);

        Person user = PersonBuilder.builder()
                .name("Tester")
                .roles(roleAdmin)
                .build();

        Person userSaved = personRepository.save(user);
        this.userId = userSaved.getId();

        Login login = LoginBuilder.builder()
            .withLogin("test")
            .withPassword(passwordEncoder.encode("1234567"))
            .withPerson(userSaved)
            .build();
        loginRepository.save(login);
    }

    protected void initRoles(){
        for(Roles role : Roles.values()){
            if(roleRepository.findByRole(role).isEmpty()){
                roleRepository.save(new Role(role));
            }
        }
    }

    protected void changeRoleTo(Roles role){
        Role newRole = roleRepository
            .findByRole(role)
            .orElse(null);

        Person user = personRepository
            .findById(this.userId)
            .orElse(null);
        user.removeAllRoles();
        user.addRole(newRole);

        personRepository.save(user);
    }
}
