package com.project.healthsystem.e2e;

import com.project.healthsystem.controller.DataSusController;
import com.project.healthsystem.controller.common.GlobalExceptionHandler;
import com.project.healthsystem.controller.mappers.SigtapProcedureMapper;
import com.project.healthsystem.model.datasus_data.sigtap.*;
import com.project.healthsystem.repository.datasus.SigtapProcedureRepository;
import com.project.healthsystem.service.datasus.*;
import org.junit.jupiter.api.*;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SigtapProcedureApiTest {
    SigtapProcedureRepository repository = mock(SigtapProcedureRepository.class);
    MockMvc mvc;

    @BeforeEach void setup() {
        var service = new SigtapProcedureService(repository, Mappers.getMapper(SigtapProcedureMapper.class));
        mvc = MockMvcBuilders.standaloneSetup(new DataSusController(mock(CnesService.class),
            mock(Cid10Service.class), mock(Ciap2Service.class), mock(SigtapService.class), service))
            .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @Test void emptyListUsesDefaults() throws Exception {
        when(repository.findAll(any(Specification.class), any(Pageable.class)))
            .thenAnswer(i -> Page.empty(i.getArgument(1)));
        mvc.perform(get("/datasus/sigtap/procedures")).andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isEmpty()).andExpect(jsonPath("$.size").value(10))
            .andExpect(jsonPath("$.number").value(0));
        verify(repository).findAll(any(Specification.class),
            eq(PageRequest.of(0, 10, Sort.by("code", "id").ascending())));
    }

    @Test void returnsHierarchyAndUnmodifiedValues() throws Exception {
        var procedure = fixture();
        when(repository.findById(7L)).thenReturn(Optional.of(procedure));
        mvc.perform(get("/datasus/sigtap/procedures/7")).andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(7)).andExpect(jsonPath("$.code").value("0101010001"))
            .andExpect(jsonPath("$.name").value("AÇÃO TESTE"))
            .andExpect(jsonPath("$.competence").value("202611"))
            .andExpect(jsonPath("$.complexity").value("1")).andExpect(jsonPath("$.sex").value("M"))
            .andExpect(jsonPath("$.minimumAge").value(0)).andExpect(jsonPath("$.maximumAge").value(99))
            .andExpect(jsonPath("$.groupCode").value("01")).andExpect(jsonPath("$.groupName").value("Grupo"))
            .andExpect(jsonPath("$.subGroupCode").value("01")).andExpect(jsonPath("$.subGroupName").value("Subgrupo"))
            .andExpect(jsonPath("$.organizationCode").value("01")).andExpect(jsonPath("$.organizationName").value("Forma"));
    }

    @Test void returnsPagedDtos() throws Exception {
        when(repository.findAll(any(Specification.class), any(Pageable.class)))
            .thenAnswer(i -> new PageImpl<>(List.of(fixture()), i.getArgument(1), 3));
        mvc.perform(get("/datasus/sigtap/procedures").param("search", "ação")
                .param("page-number", "1").param("page-length", "1"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(3))
            .andExpect(jsonPath("$.content[0].groupName").value("Grupo"))
            .andExpect(jsonPath("$.number").value(1)).andExpect(jsonPath("$.size").value(1));
    }

    @Test void missingIdReturns404() throws Exception {
        mvc.perform(get("/datasus/sigtap/procedures/999")).andExpect(status().isNotFound());
    }

    @Test void invalidPaginationReturns400WithoutQuery() throws Exception {
        for (String[] params : List.of(new String[]{"-1","10"}, new String[]{"0","0"},
                new String[]{"0","101"}, new String[]{"abc","10"})) {
            mvc.perform(get("/datasus/sigtap/procedures").param("page-number",params[0])
                .param("page-length",params[1])).andExpect(status().isBadRequest());
        }
        verifyNoInteractions(repository);
    }

    @Test void acceptsMaximumPageSize() throws Exception {
        when(repository.findAll(any(Specification.class), any(Pageable.class)))
            .thenAnswer(i -> Page.empty(i.getArgument(1)));
        mvc.perform(get("/datasus/sigtap/procedures").param("page-length","100"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.size").value(100));
    }

    @Test void missingHierarchyDoesNotCauseServerError() throws Exception {
        SigtapProcedure procedure = fixture();
        procedure.setOrganization(null);
        when(repository.findById(7L)).thenReturn(Optional.of(procedure));
        mvc.perform(get("/datasus/sigtap/procedures/7")).andExpect(status().isOk())
            .andExpect(jsonPath("$.groupCode").doesNotExist());
    }

    static SigtapProcedure fixture() {
        SigtapGroup group = new SigtapGroup(); group.setCode("01"); group.setName("Grupo");
        SigtapSubGroup sub = new SigtapSubGroup(); sub.setCode("01"); sub.setName("Subgrupo"); sub.setGroup(group);
        SigtapOrganization org = new SigtapOrganization(); org.setCode("01"); org.setName("Forma"); org.setSubGroup(sub);
        SigtapProcedure p = new SigtapProcedure();
        p.setId(7); p.setCode("0101010001"); p.setName("AÇÃO TESTE"); p.setOrganization(org);
        p.setCompetence("202611"); p.setComplexity("1"); p.setSex("M");
        p.setMinimumAge(0); p.setMaximumAge(99);
        return p;
    }
}
