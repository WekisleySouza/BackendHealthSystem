package com.project.healthsystem.e2e;

import com.project.healthsystem.controller.DataSusController;
import com.project.healthsystem.controller.common.GlobalExceptionHandler;
import com.project.healthsystem.controller.mappers.*;
import com.project.healthsystem.model.datasus_data.cid.Cid10;
import com.project.healthsystem.model.datasus_data.ciap.Ciap2;
import com.project.healthsystem.repository.datasus.*;
import com.project.healthsystem.service.datasus.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ClinicalCatalogApiTest {
    private Cid10Repository cidRepository;
    private Ciap2Repository ciapRepository;
    private MockMvc mvc;

    @BeforeEach
    void setup() {
        cidRepository = mock(Cid10Repository.class);
        ciapRepository = mock(Ciap2Repository.class);
        var cid = new Cid10Service(cidRepository, Mappers.getMapper(Cid10Mapper.class));
        var ciap = new Ciap2Service(ciapRepository, Mappers.getMapper(Ciap2Mapper.class));
        mvc = MockMvcBuilders.standaloneSetup(
            new DataSusController(mock(CnesService.class), cid, ciap, mock(SigtapService.class), mock(SigtapProcedureService.class), mock(CnesProfessionalService.class), mock(CnesEstabilishmentService.class)))
            .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @ParameterizedTest
    @ValueSource(strings = {"cid10", "ciap2"})
    void returnsEmptyPageWithDefaultPagination(String catalog) throws Exception {
        if (catalog.equals("cid10")) {
            when(cidRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenAnswer(inv -> Page.empty(inv.getArgument(1)));
        } else {
            when(ciapRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenAnswer(inv -> Page.empty(inv.getArgument(1)));
        }
        mvc.perform(get("/datasus/" + catalog))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isEmpty())
            .andExpect(jsonPath("$.number").value(0))
            .andExpect(jsonPath("$.size").value(10))
            .andExpect(jsonPath("$.totalElements").value(0));
        Pageable expected = PageRequest.of(0, 10, Sort.by("code").ascending());
        if (catalog.equals("cid10")) verify(cidRepository).findAll(any(Specification.class), eq(expected));
        else verify(ciapRepository).findAll(any(Specification.class), eq(expected));
    }

    @ParameterizedTest
    @ValueSource(strings = {"cid10", "ciap2"})
    void returnsDtoAndPreservesPecIndicators(String catalog) throws Exception {
        if (catalog.equals("cid10")) {
            Cid10 entry = new Cid10();
            entry.setId(1L);
            entry.setCode("A000");
            entry.setDescription("Descrição");
            entry.setPecId(100L);
            entry.setSex("AMBOS");
            entry.setActiveInPec(false);
            when(cidRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenAnswer(inv -> new PageImpl<>(List.of(entry), inv.getArgument(1), 3));
        } else {
            Ciap2 entry = new Ciap2();
            entry.setId(1L);
            entry.setCode("ABP001");
            entry.setDescription("Descrição");
            entry.setPecId(100L);
            entry.setSex("AMBOS");
            entry.setDefaultFilterInPec(false);
            when(ciapRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenAnswer(inv -> new PageImpl<>(List.of(entry), inv.getArgument(1), 3));
        }
        mvc.perform(get("/datasus/" + catalog).param("search", "a")
                .param("page-number", "1").param("page-length", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(1))
            .andExpect(jsonPath("$.content[0].code").value(catalog.equals("cid10") ? "A000" : "ABP001"))
            .andExpect(jsonPath("$.content[0].description").value("Descrição"))
            .andExpect(jsonPath("$.content[0].pecId").value(100))
            .andExpect(jsonPath("$.content[0].sex").value("AMBOS"))
            .andExpect(jsonPath("$.content[0]." +
                (catalog.equals("cid10") ? "activeInPec" : "defaultFilterInPec")).value(false))
            .andExpect(jsonPath("$.number").value(1))
            .andExpect(jsonPath("$.size").value(1))
            .andExpect(jsonPath("$.totalElements").value(3));
    }

    @ParameterizedTest
    @ValueSource(strings = {"cid10", "ciap2"})
    void rejectsInvalidPaginationWithoutQueryingDatabase(String catalog) throws Exception {
        for (String[] params : List.of(new String[]{"-1", "10"}, new String[]{"0", "0"},
                new String[]{"0", "101"}, new String[]{"abc", "10"})) {
            mvc.perform(get("/datasus/" + catalog).param("page-number", params[0])
                    .param("page-length", params[1]))
                .andExpect(status().isBadRequest());
        }
        verifyNoInteractions(cidRepository, ciapRepository);
    }

    @ParameterizedTest
    @ValueSource(strings = {"cid10", "ciap2"})
    void acceptsMaximumPageLength(String catalog) throws Exception {
        if (catalog.equals("cid10")) {
            when(cidRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenAnswer(inv -> Page.empty(inv.getArgument(1)));
        } else {
            when(ciapRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenAnswer(inv -> Page.empty(inv.getArgument(1)));
        }
        mvc.perform(get("/datasus/" + catalog).param("page-length", "100"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.size").value(100));
    }
}
