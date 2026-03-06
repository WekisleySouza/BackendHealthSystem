package com.project.healthsystem.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


//@WebMvcTest(PatientController.class)
public class PatientControllerTest {
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockitoBean
//    private PatientService patientService;
//
//    @Test
//    void saveTest() throws Exception {
//        PatientRequestDTO patientRequestDTO = new PatientRequestDTO();
//
//        // Required
//        patientRequestDTO.setAgentId(1L);
//        patientRequestDTO.setName("Josh Robson");
//        patientRequestDTO.setBirthday(LocalDate.of(1995, 5, 20));
//        patientRequestDTO.setCns("123456789012345"); // CNS fictício
//        patientRequestDTO.setCpf("123.456.789-09"); // CPF válido de exemplo
//
//        // Optionals
//        patientRequestDTO.setResponsibleId(2L);
//        patientRequestDTO.setConditionsId(List.of(10L, 20L, 30L));
//        patientRequestDTO.setMotherName("Anna Robson");
//        patientRequestDTO.setPhone("(11) 98888-7777");
//        patientRequestDTO.setEmail("wekisleysouza.a@gmail.com");
//
//        Patient patient = new Patient();
//        patient.getPerson().setName("Josh Robson");
//        patient.getPerson().setEmail("wekisleysouza.a@gmail.com");
//
//        Mockito.when(patientService.save(
//            any(PatientRequestDTO.class), any(String.class)))
//        .thenReturn(patient);
//
//        mockMvc.perform(post("/patients")
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(asJson(patientRequestDTO))
//        )
//                .andExpect(status().isCreated());
//    }
}
