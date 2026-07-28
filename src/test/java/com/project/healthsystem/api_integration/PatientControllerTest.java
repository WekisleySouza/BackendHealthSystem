package com.project.healthsystem.api_integration;

import com.project.healthsystem.DBTestConfig;
import com.project.healthsystem.model.Patient;
import com.project.healthsystem.model.Person;
import com.project.healthsystem.repository.PatientRepository;
import com.project.healthsystem.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class PatientControllerTest extends DBTestConfig {

    @Autowired
    PersonRepository personRepository;
    @Autowired
    PatientRepository patientRepository;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void setup(){
        Person person = new Person();
        person.setName("Wekisley de Souza Ananias");
        personRepository.save(person);

        Patient patient = new Patient();
        patient.setPerson(person);
        patientRepository.save(patient);
    }

    @Test
    void shouldFindPatientById() throws Exception {
        mockMvc.perform(
            get("/patients/1")
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name")
            .value("Wekisley de Souza Ananias"));
    }
}
