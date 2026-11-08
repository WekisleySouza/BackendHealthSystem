package com.project.healthsystem.e2e;

import com.project.healthsystem.controller.DataSusController;
import com.project.healthsystem.controller.common.GlobalExceptionHandler;
import com.project.healthsystem.controller.dto.basic_responses.CnesImportResponseDTO;
import com.project.healthsystem.service.datasus.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.nio.charset.StandardCharsets;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static builders.CnesFixtures.*;

@SpringJUnitConfig(CnesImportControllerTest.Config.class)
class CnesImportControllerTest {
    @Configuration @EnableMethodSecurity(proxyTargetClass = true)
    static class Config {
        @Bean CnesImportService importer() { return mock(CnesImportService.class); }
        @Bean CnesService service(CnesImportService importer) { return new CnesService(new CnesCsvReader(),importer); }
        @Bean DataSusController controller(CnesService service) {
            return new DataSusController(service,mock(Cid10Service.class),mock(Ciap2Service.class),
                mock(SigtapService.class),mock(SigtapProcedureService.class), mock(CnesProfessionalService.class), mock(CnesEstabilishmentService.class));
        }
    }
    @Autowired DataSusController controller;
    @Autowired CnesImportService importer;
    @BeforeEach void setup() { reset(importer); login("ADMIN"); }
    @AfterEach void cleanup() { SecurityContextHolder.clearContext(); }
    void login(String role) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
            "test","unused",List.of(new SimpleGrantedAuthority("ROLE_"+role))));
    }
    MockMultipartFile file(String text) {
        return new MockMultipartFile("file","data.csv","text/csv",text.getBytes(StandardCharsets.UTF_8));
    }
    @ParameterizedTest @ValueSource(booleans={true,false})
    void adminGetsSummaryAfterImport(boolean establishment) throws Exception {
        var catalog = establishment ? CnesCsvReader.Catalog.ESTABLISHMENTS : CnesCsvReader.Catalog.PROFESSIONALS;
        String route = establishment ? "estabilishments" : "professionals";
        String content = establishment ? EST_HEADER+"\n"+establishment("0012345")
            : PROF_HEADER+"\n"+professional("000000000000001","0012345");
        when(importer.replace(any(),eq(catalog),eq(1L))).thenReturn(new CnesImportResponseDTO(catalog.name(),1));
        MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new GlobalExceptionHandler()).build()
            .perform(multipart("/datasus/import-cnes-"+route).file(file(content)))
            .andExpect(status().isOk()).andExpect(jsonPath("$.catalog").value(catalog.name()))
            .andExpect(jsonPath("$.importedRecords").value(1));
    }
    @ParameterizedTest @ValueSource(booleans={true,false})
    void invalidFileReturns400WithoutWriter(boolean establishment) throws Exception {
        String route=establishment?"estabilishments":"professionals";
        String content=establishment?EST_HEADER:PROF_HEADER+"\n"+professional("1234","0012345");
        MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new GlobalExceptionHandler()).build()
            .perform(multipart("/datasus/import-cnes-"+route).file(file(content)))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
        verifyNoInteractions(importer);
    }
    @ParameterizedTest @ValueSource(strings={"EMPLOYEE","MANAGER"})
    void nonAdministratorCannotImport(String role) {
        login(role);
        assertThrows(AccessDeniedException.class,()->controller.importCnesEstabilishments(file("")));
        assertThrows(AccessDeniedException.class,()->controller.importCnesProfessionals(file("")));
        verifyNoInteractions(importer);
    }
}
