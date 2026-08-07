package com.project.healthsystem.api_integration;

import builders.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.healthsystem.IntegrationTestBase;
import com.project.healthsystem.controller.dto.basic_requests.AppointmentRequestDTO;
import com.project.healthsystem.model.*;
import com.project.healthsystem.repository.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.matchesPattern;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class AppointmentControllerTest extends IntegrationTestBase {
  @Autowired
  private AppointmentRepository appointmentRepository;
  @Autowired
  private PatientRepository patientRepository;
  @Autowired
  private ProfessionalRepository professionalRepository;
  @Autowired
  private EmployeeRepository employeeRepository;
  @Autowired
  private ServiceTypeRepository serviceTypeRepository;
  private Appointment appointment;
  
  @Autowired
  private ObjectMapper objectMapper;
  
  @BeforeAll
  void setup() throws Exception {
    Person p1 = PersonBuilder.builder()
      .name("Wekisley")
      .build();
    personRepository.save(p1);
    Person p2 = PersonBuilder.builder()
      .name("Gustavo")
      .build();
    personRepository.save(p2);
    Person p3 = PersonBuilder.builder()
      .name("Emílio")
      .build();
    personRepository.save(p3);
    
    Patient patient = PatientBuilder.builder()
      .person(p1)
      .build();
    patientRepository.save(patient);
    
    Professional professional = ProfessionalBuilder.builder()
      .person(p2)
      .build();
    professionalRepository.save(professional);
    
    Employee employee = EmployeeBuilder.builder()
      .withPerson(p3)
      .build();
    employeeRepository.save(employee);
    
    ServiceType serviceType = ServiceTypeBuilder.builder()
      .build();
    serviceTypeRepository.save(serviceType);
    
    appointment = AppointmentBuilder.builder()
      .withPatient(patient)
      .withRequestingProfessional(professional)
      .withEmployee(employee)
      .withServiceType(serviceType)
      .withStatus(Status.COMPLETED)
      .withPriorit(Priority.URGENT)
      .build();
    appointment = appointmentRepository.save(appointment);
    
    loginConfig();
  }
  
  @Test
  void shouldSaveAppointment() throws Exception {
    AppointmentRequestDTO requestDTO = AppointmentBuilder.createValidRequestDTO();
    
    MvcResult result = mockMvc.perform(
      post("/appointments")
        .header("Authorization", "Bearer " + this.adminToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDTO))
    ).andExpect(status().isCreated())
      .andExpect(header().string(
        "Location",
        matchesPattern(".*/appointments/\\d+")
      )).andReturn();
    
    String location = result.getResponse().getHeader("Location");
    
    mockMvc.perform(
      get(location)
        .header("Authorization", "Bearer " + this.adminToken)
    ).andExpect(status().isOk());
  }
  
  @Test
  void shouldUpdateAppointment() throws Exception {
    AppointmentRequestDTO requestDTO = AppointmentBuilder.createValidRequestDTO();
    requestDTO.setStatus(Status.CANCELED.getLabel());
    
    // Test valid data
    mockMvc.perform(
      put("/appointments/{id}", this.appointment.getId())
        .header("Authorization", "Bearer " + this.adminToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDTO))
    )
      .andExpect(status().isNoContent())
      .andExpect(content().string(""));
    
    mockMvc.perform(
        get("/appointments/{id}", this.appointment.getId())
          .header("Authorization", "Bearer " + this.adminToken)
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(this.appointment.getId()))
      .andExpect(jsonPath("$.status").value(Status.CANCELED.getLabel()));
  }
  
  @Test
  void shouldReturnNotFoundWhenAppointmentDoesNotExist() throws Exception {
    AppointmentRequestDTO requestDTO = AppointmentBuilder.createValidRequestDTO();
    
    mockMvc.perform(
        put("/appointments/{id}", 99999)
          .header("Authorization", "Bearer " + this.adminToken)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(requestDTO))
      )
      .andExpect(status().isNotFound());
  }
  
  @Test
  void shouldReturnUnprocessableEntityWhenAppointmentDataIsInvalid() throws Exception {
    AppointmentRequestDTO requestDTO = AppointmentBuilder.createValidRequestDTO();
    
    requestDTO.setEmployeeId(null);
    mockMvc.perform(
        put("/appointments/{id}", this.appointment.getId())
          .header("Authorization", "Bearer " + this.adminToken)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(requestDTO))
      )
      .andExpect(status().isUnprocessableEntity());
  }
  
  @Test
  void shouldReturnAppointmentById() throws Exception {
    mockMvc.perform(
      get("/appointments/{id}", this.appointment.getId())
        .header("Authorization", "Bearer " + this.adminToken)
    )
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(this.appointment.getId()))
      .andExpect(jsonPath("$.serviceType.name").value(this.appointment.getServiceType().getName()));
  }
  
  @Test
  void shouldReturnNotFoundOnAppointmentById() throws Exception {
    mockMvc.perform(
        get("/appointments/{id}", 9999)
          .header("Authorization", "Bearer " + this.adminToken)
      )
      .andExpect(status().isNotFound());
  }
  
  @Test
  void shouldReturnAllAppointments() throws Exception {
    mockMvc.perform(
        get("/appointments")
          .header("Authorization", "Bearer " + this.adminToken)
      )
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.content").isArray())
      .andExpect(jsonPath("$.content.length()").value(1))
      .andExpect(jsonPath("$.content[0].id").value(this.appointment.getId()))
      .andExpect(jsonPath("$.content[0].serviceTypeName").value(this.appointment.getServiceType().getName()))
      .andExpect(jsonPath("$.totalElements").value(1))
      .andExpect(jsonPath("$.totalPages").value(1))
      .andExpect(jsonPath("$.number").value(0));
  }
  
  @Test
  void shouldReturnReportAppointmentPatientsGraph() throws Exception {
  
  }
  
  @Test
  void shouldReturnReportAppointmentPatientsPage() throws Exception {
  
  }
  
  @Test
  void shouldDeleteAppointment() throws Exception {
    mockMvc.perform(
      delete("/appointments/{id}", this.appointment.getId())
        .header("Authorization", "Bearer " + this.adminToken)
    )
      .andExpect(status().isNoContent());
    
    mockMvc.perform(
        delete("/appointments/{id}", this.appointment.getId())
          .header("Authorization", "Bearer " + this.adminToken)
      )
      .andExpect(status().isNotFound());
  }
  
  @Test
  void shouldSetAppointmentToPreScheduled() throws Exception {
  
  }
  
  @Test
  void shouldSetAppointmentToOtherStatus() throws Exception {
  
  }
  
  @Test
  void shouldCountAppointmentsByServiceType() throws Exception {
  
  }
  
  @Test
  void shouldReturnAppointmentsFromAPatient() throws Exception {
  
  }
}
