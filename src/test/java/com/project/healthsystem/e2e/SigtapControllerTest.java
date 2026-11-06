package com.project.healthsystem.e2e;

import com.project.healthsystem.controller.DataSusController;
import com.project.healthsystem.service.datasus.*;
import com.project.healthsystem.controller.dto.basic_responses.SigtapImportResponseDTO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitConfig(SigtapControllerTest.Config.class)
class SigtapControllerTest {
    @Configuration @EnableMethodSecurity
    static class Config {
        @Bean SigtapService sigtapService() { return mock(SigtapService.class); }
        @Bean DataSusController controller(SigtapService service) {
            return new DataSusController(mock(CnesService.class), mock(Cid10Service.class),
                mock(Ciap2Service.class), service, mock(SigtapProcedureService.class), mock(CnesProfessionalService.class), mock(CnesEstabilishmentService.class));
        }
    }
    @Autowired DataSusController controller;
    @Autowired SigtapService service;
    @BeforeEach void setup() { reset(service); }
    @AfterEach void cleanup() { SecurityContextHolder.clearContext(); }

    void login(String role) {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("test", "unused", List.of(new SimpleGrantedAuthority(role))));
    }

    @Test void administratorCanForceImportAndSeeCounts() throws Exception {
        login("ROLE_ADMIN");
        when(service.importSigtap()).thenReturn(new SigtapImportResponseDTO(true, "202611",
            "TabelaUnificada_202611_v2611051200.zip", 2, 2, 2, 2));
        MockMvcBuilders.standaloneSetup(controller).build().perform(post("/datasus/import-sigtap"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.imported").value(true))
            .andExpect(jsonPath("$.competence").value("202611"))
            .andExpect(jsonPath("$.procedures").value(2));
        verify(service).importSigtap();
    }

    @Test void employeeCannotTriggerImport() {
        login("ROLE_EMPLOYEE");
        assertThrows(AccessDeniedException.class, () -> controller.importSigtap());
        verifyNoInteractions(service);
    }
}
