package com.project.healthsystem.api_integration;

import builders.PatientBuilder;
import builders.PersonBuilder;
import com.project.healthsystem.IntegrationTestBase;
import com.project.healthsystem.model.Patient;
import com.project.healthsystem.model.Person;
import com.project.healthsystem.repository.PatientRepository;
import com.project.healthsystem.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class PatientControllerTest extends IntegrationTestBase {
    @Autowired
    PatientRepository patientRepository;

    @BeforeEach
    void setup() throws Exception {
        Person person = PersonBuilder.builder()
            .name("Alex Silva")
            .build();
        personRepository.save(person);

        Patient patient = PatientBuilder.builder()
            .person(person)
            .build();
        patientRepository.save(patient);

        this.createLogin();
        this.login();
    }

    @Test
    void shouldUpdatePatient() throws Exception {

    }

    @Test
    void shouldSavePatient() throws Exception {

    }

    @Test
    void shouldFindPatientById() throws Exception {
        mockMvc.perform(
                get("/patients/1")
                .header("Authorization", "Bearer " + this.token)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name")
                .value("Alex Silva"));
    }

    @Test
    void shouldGetPatientInfoById() throws Exception {

    }

    @Test
    void shouldGetAllPatientsInfoSimplified() throws Exception {

    }

    @Test
    void shouldGetAllPatientsSuperSimplified() throws Exception {

    }

    @Test
    void shouldGetAllPatients() throws Exception {

    }

    @Test
    void shouldDeletePatient() throws Exception {

    }

    @Test
    void shouldUpdatePatientCPF() throws Exception {

    }
}
