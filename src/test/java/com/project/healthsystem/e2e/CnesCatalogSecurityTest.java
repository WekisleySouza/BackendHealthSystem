package com.project.healthsystem.e2e;

import com.project.healthsystem.controller.DataSusController;
import com.project.healthsystem.service.datasus.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(CnesCatalogSecurityTest.Config.class)
class CnesCatalogSecurityTest {
    @Configuration @EnableMethodSecurity(proxyTargetClass = true)
    static class Config {
        @Bean CnesProfessionalService professionals() { return mock(CnesProfessionalService.class); }
        @Bean CnesEstabilishmentService establishments() { return mock(CnesEstabilishmentService.class); }
        @Bean DataSusController controller(CnesProfessionalService p, CnesEstabilishmentService e) {
            return new DataSusController(mock(CnesService.class), mock(Cid10Service.class),
                mock(Ciap2Service.class), mock(SigtapService.class), mock(SigtapProcedureService.class),p,e);
        }
    }
    @Autowired DataSusController controller;
    @Autowired CnesProfessionalService professionals;
    @Autowired CnesEstabilishmentService establishments;
    @BeforeEach void resetMocks() { reset(professionals,establishments); SecurityContextHolder.clearContext(); }
    @AfterEach void cleanup() { SecurityContextHolder.clearContext(); }
    void login(String role) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
            "test","unused",List.of(new SimpleGrantedAuthority(role))));
    }
    @Test void permittedRolesCanUseAllFourRoutesAndForwardFilters() {
        for (String role : List.of("ROLE_ADMIN","ROLE_MANAGER","ROLE_EMPLOYEE")) {
            login(role);
            controller.findProfessionals(0,10,"Ana","0000123","2231F9");
            controller.findProfessional(1);
            controller.findEstabilishments(0,10,"Unidade");
            controller.findEstabilishment(2);
        }
        verify(professionals,times(3)).findAll(0,10,"Ana","0000123","2231F9");
        verify(professionals,times(3)).findById(1);
        verify(establishments,times(3)).findAll(0,10,"Unidade");
        verify(establishments,times(3)).findById(2);
    }
    @Test void otherRoleCannotAccessAnyRoute() {
        login("ROLE_UNAUTHORIZED");
        assertThrows(AccessDeniedException.class, () -> controller.findProfessionals(0,10,null,null,null));
        assertThrows(AccessDeniedException.class, () -> controller.findProfessional(1));
        assertThrows(AccessDeniedException.class, () -> controller.findEstabilishments(0,10,null));
        assertThrows(AccessDeniedException.class, () -> controller.findEstabilishment(1));
        verifyNoInteractions(professionals,establishments);
    }
    @Test void unauthenticatedAccessIsDenied() {
        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> controller.findProfessionals(0,10,null,null,null));
        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> controller.findProfessional(1));
        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> controller.findEstabilishments(0,10,null));
        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> controller.findEstabilishment(1));
        verifyNoInteractions(professionals,establishments);
    }
}
