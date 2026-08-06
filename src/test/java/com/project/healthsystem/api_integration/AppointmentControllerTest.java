package com.project.healthsystem.api_integration;

import builders.AppointmentBuilder;
import com.project.healthsystem.IntegrationTestBase;
import com.project.healthsystem.model.Appointment;
import com.project.healthsystem.repository.AppointmentRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AppointmentControllerTest extends IntegrationTestBase {
  @Autowired
  private AppointmentRepository appointmentRepository;
  private Appointment appointment;
  
  @BeforeAll
  void setup() throws Exception {
    appointment = AppointmentBuilder.builder()
      .build();
    
    loginConfig();
  }
  
  @Test
  void shouldSaveAppointment() throws Exception {
  
  }
  
  @Test
  void shouldUpdateAppointment() throws Exception {
  
  }
  
  @Test
  void shouldReturnAppointmentById() throws Exception {
  
  }
  
  @Test
  void shouldReturnAllAppointments() throws Exception {
  
  }
  
  @Test
  void shouldReturnReportAppointmentPatientsGraph() throws Exception {
  
  }
  
  @Test
  void shouldReturnReportAppointmentPatientsPage() throws Exception {
  
  }
  
  @Test
  void shouldDeleteAppointment() throws Exception {
  
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
  void shouldAppointmentsFromOnePatient() throws Exception {
  
  }
}
