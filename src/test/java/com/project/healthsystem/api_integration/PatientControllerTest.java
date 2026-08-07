package com.project.healthsystem.api_integration;

import builders.PatientBuilder;
import builders.PersonBuilder;
import com.project.healthsystem.IntegrationTestBase;
import com.project.healthsystem.model.Patient;
import com.project.healthsystem.model.Person;
import com.project.healthsystem.model.Roles;
import com.project.healthsystem.repository.PatientRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class PatientControllerTest extends IntegrationTestBase {
  @Autowired
  private PatientRepository patientRepository;
  
  private Patient patient;
  
  @BeforeAll
  void setup() throws Exception {
    Person person = PersonBuilder.builder()
      .name("Alex Silva")
      .cpf("14828169628")
      .gender("Masculino")
      .sex("Masculino")
      .build();
    person = personRepository.save(person);
    
    this.patient = PatientBuilder.builder()
      .person(person)
      .build();
    this.patient = patientRepository.save(patient);
    
    loginConfig();
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
          .header("Authorization", "Bearer " + this.adminToken)
      )
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.cpf").value("14828169628"))
      .andExpect(jsonPath("$.name").value("Alex Silva"));
  }
  
  @Test
  void shouldNotFindPatientById() throws Exception {
    mockMvc.perform(
        get("/patients/99999")
          .header("Authorization", "Bearer " + this.adminToken)
      )
      .andExpect(status().isNotFound());
  }
  
  @Test
  void shouldReturnPatientInfoWhenIdExists() throws Exception {
      mockMvc.perform(
        get("/patients/info-patient/{id}", this.patient.getId())
          .header("Authorization", "Bearer " + this.adminToken)
      )
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(this.patient.getId()))
        .andExpect(jsonPath("$.name").value(this.patient.getPerson().getName()));
  }
  
  @Test
  void shouldNotReturnPatientInfoWhenIdDontExists() throws Exception {
    mockMvc.perform(
        get("/patients/info-patient/{id}", 99999)
          .header("Authorization", "Bearer " + this.adminToken)
      )
      .andExpect(status().isNotFound());
  }
  
  @Test
  void shouldGetAllPatientsInfoSimplified() throws Exception {
    mockMvc.perform(
      get("/patients/simplified-list")
        .header("Authorization", "Bearer " + this.adminToken)
    )
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$.length()").value(1))
      .andExpect(jsonPath("$[0].id").value(this.patient.getId()))
      .andExpect(jsonPath("$[0].name").value(this.patient.getPerson().getName()));
  }
  
  @Test
  void shouldReturnAllPatientsSuperSimplified() throws Exception {
    mockMvc.perform(
        get("/patients/get-all-simplified")
          .header("Authorization", "Bearer " + this.adminToken)
      )
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.content").isArray())
      .andExpect(jsonPath("$.content.length()").value(1))
      .andExpect(jsonPath("$.content[0].id").value(this.patient.getId()))
      .andExpect(jsonPath("$.content[0].name").value(this.patient.getPerson().getName()))
      .andExpect(jsonPath("$.totalElements").value(1))
      .andExpect(jsonPath("$.totalPages").value(1))
      .andExpect(jsonPath("$.number").value(0));
    
  }
  
  @Test
  void shouldReturnAllPatients() throws Exception {
    mockMvc.perform(
        get("/patients")
          .header("Authorization", "Bearer " + this.adminToken)
      )
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.content").isArray())
      .andExpect(jsonPath("$.content.length()").value(1))
      .andExpect(jsonPath("$.content[0].id").value(this.patient.getId()))
      .andExpect(jsonPath("$.content[0].name").value(this.patient.getPerson().getName()))
      .andExpect(jsonPath("$.totalElements").value(1))
      .andExpect(jsonPath("$.totalPages").value(1))
      .andExpect(jsonPath("$.number").value(0));
    
  }
  
  @Test
  void shouldDeletePatient() throws Exception {
  
  }
  
  @Test
  void shouldUpdatePatientCPF() throws Exception {
  
  }
  
  @ParameterizedTest
  @CsvSource({
    "ADMIN, 200",
    "MANAGER, 200",
    "EMPLOYEE, 200",
    "PATIENT, 403"
  })
  void patientRoleShouldRespectAllRestrictions(String role, int expectedStatus) throws Exception {
    String token =
      role.equals("ADMIN") ? this.adminToken :
      role.equals("MANAGER") ? this.managerToken :
      role.equals("EMPLOYEE") ? this.employeeToken : this.patientToken;
        
        // Find by id tests
    mockMvc.perform(
        get("/patients/1")
          .header("Authorization", "Bearer " + token)
      )
      .andExpect(status().is(expectedStatus));
    
    // Get patient info
    mockMvc.perform(
        get("/patients/info-patient/{id}", this.patient.getId())
          .header("Authorization", "Bearer " + token)
      )
      .andExpect(status().is(expectedStatus));
    
    // Get all patients info
    mockMvc.perform(
        get("/patients/simplified-list")
          .header("Authorization", "Bearer " + token)
      )
      .andExpect(status().is(expectedStatus));
    
    // Get all patients super simplified info
    mockMvc.perform(
        get("/patients/get-all-simplified")
          .header("Authorization", "Bearer " + token)
      )
      .andExpect(status().is(expectedStatus));
    
    // Get all patients
    mockMvc.perform(
        get("/patients")
          .header("Authorization", "Bearer " + token)
      )
      .andExpect(status().is(expectedStatus));
  }
  
  
  @Test
  void shouldNotAcceptAnonymousUsers() throws Exception {
    // Find by id tests
    mockMvc.perform(get("/patients/1"))
      .andExpect(status().isUnauthorized());
    
    // Get patient info
    mockMvc.perform(
        get("/patients/info-patient/{id}", this.patient.getId())
      )
      .andExpect(status().isUnauthorized());
    
    // Get all patients info
    mockMvc.perform(
        get("/patients/simplified-list")
      )
      .andExpect(status().isUnauthorized());
    
    // Get all patients super simplified info
    mockMvc.perform(
        get("/patients/get-all-simplified")
      )
      .andExpect(status().isUnauthorized());
    
    // Get all patients
    mockMvc.perform(
        get("/patients")
      )
      .andExpect(status().isUnauthorized());
    
  }
}
