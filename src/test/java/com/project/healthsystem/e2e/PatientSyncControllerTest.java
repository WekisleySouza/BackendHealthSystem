package com.project.healthsystem.e2e;

import com.project.healthsystem.controller.PatientController;
import com.project.healthsystem.controller.dto.basic_requests.PersonBackupInfoDTO;
import com.project.healthsystem.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitConfig(PatientSyncControllerTest.Config.class)
class PatientSyncControllerTest {
  @Configuration
  @EnableMethodSecurity
  static class Config {

    @Bean
    PatientController patientController(PatientService service) { return new PatientController(service); }
  }

  @Autowired
  private PatientController controller;
  @org.springframework.test.context.bean.override.mockito.MockitoBean
  private PatientService service;
  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    reset(service);
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
  }

  @Test
  @WithMockUser(roles = "API")
  void shouldSynchronizeJsonArrayAndReturn200() throws Exception {
    mockMvc.perform(post("/patients/sync-patients")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
          [{"citizenSeqId":"123", "patientName":"Paciente de teste"}]
          """))
      .andExpect(status().isOk())
      .andExpect(content().string(""));

    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<PersonBackupInfoDTO>> captor = ArgumentCaptor.forClass(List.class);
    verify(service).syncExternalDataBase(captor.capture());
    assertEquals(1, captor.getValue().size());
    assertEquals("123", captor.getValue().get(0).getCitizenSeqId());
    assertEquals("Paciente de teste", captor.getValue().get(0).getPatientName());
  }

  @Test
  void shouldRejectUnauthenticatedInvocation() {
    assertThrows(AuthenticationCredentialsNotFoundException.class, () -> controller.syncPatients(List.of()));
    verifyNoInteractions(service);
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void shouldRejectUserWithoutApiRole() {
    assertThrows(AccessDeniedException.class, () -> controller.syncPatients(List.of()));
    verifyNoInteractions(service);
  }

  @Test
  @WithMockUser(roles = "API")
  void shouldRejectObjectInsteadOfArray() throws Exception {
    mockMvc.perform(post("/patients/sync-patients")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{}"))
      .andExpect(status().isBadRequest());
    verifyNoInteractions(service);
  }
}