package com.project.healthsystem.e2e;

import com.project.healthsystem.controller.DataSusController;
import com.project.healthsystem.controller.common.GlobalExceptionHandler;
import com.project.healthsystem.controller.mappers.*;
import com.project.healthsystem.model.datasus_data.cnes.*;
import com.project.healthsystem.repository.datasus.*;
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

class CnesCatalogApiTest {
    CnesProfessionalsRepository professionals = mock(CnesProfessionalsRepository.class);
    CnesEstabilishmentsRepository establishments = mock(CnesEstabilishmentsRepository.class);
    MockMvc mvc;

    @BeforeEach void setup() {
        var p = new CnesProfessionalService(professionals, Mappers.getMapper(CnesProfessionalMapper.class));
        var e = new CnesEstabilishmentService(establishments, Mappers.getMapper(CnesEstabilishmentMapper.class));
        mvc = MockMvcBuilders.standaloneSetup(new DataSusController(mock(CnesService.class),
            mock(Cid10Service.class), mock(Ciap2Service.class), mock(SigtapService.class),
            mock(SigtapProcedureService.class), p, e))
            .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @Test void defaultsAndStableOrdering() throws Exception {
        when(professionals.findAll(any(Specification.class), any(Pageable.class))).thenAnswer(i -> Page.empty(i.getArgument(1)));
        when(establishments.findAll(any(Specification.class), any(Pageable.class))).thenAnswer(i -> Page.empty(i.getArgument(1)));
        for (String route : List.of("professionals", "estabilishments")) {
            mvc.perform(get("/datasus/cnes/" + route)).andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty()).andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0));
        }
        verify(professionals).findAll(any(Specification.class), eq(PageRequest.of(0,10,Sort.by("name","id"))));
        verify(establishments).findAll(any(Specification.class), eq(PageRequest.of(0,10,Sort.by("tradeName","id"))));
    }

    @Test void preservesMultipleBondsAndStringCodes() throws Exception {
        var a = professional(1,"0000123");
        var b = professional(2,"0000456");
        when(professionals.findAll(any(Specification.class), any(Pageable.class)))
            .thenAnswer(i -> new PageImpl<>(List.of(a,b),i.getArgument(1),2));
        mvc.perform(get("/datasus/cnes/professionals").param("search","Ana"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(2))
            .andExpect(jsonPath("$.content[0].cns").value("000000000000001"))
            .andExpect(jsonPath("$.content[1].cns").value("000000000000001"))
            .andExpect(jsonPath("$.content[0].cbo").value("2231F9"))
            .andExpect(jsonPath("$.content[0].cnes").value("0000123"))
            .andExpect(jsonPath("$.content[1].cnes").value("0000456"));
    }

    @Test void detailReturnsDtosAndNullableFields() throws Exception {
        when(professionals.findById(1L)).thenReturn(Optional.of(professional(1,"0000123")));
        Estabilishment e = new Estabilishment();
        e.setId(2); e.setCnesCode("0000123"); e.setTradeName("Unidade");
        e.setCnpj("00123456000100"); e.setAddress("Rua teste");
        when(establishments.findById(2L)).thenReturn(Optional.of(e));
        mvc.perform(get("/datasus/cnes/professionals/1")).andExpect(status().isOk())
            .andExpect(jsonPath("$.sus").value(true)).andExpect(jsonPath("$.resident").value(false))
            .andExpect(jsonPath("$.cboDescription").value("Especialidade"));
        mvc.perform(get("/datasus/cnes/estabilishments/2")).andExpect(status().isOk())
            .andExpect(jsonPath("$.cnesCode").value("0000123"))
            .andExpect(jsonPath("$.cnpj").value("00123456000100"))
            .andExpect(jsonPath("$.address").value("Rua teste")).andExpect(jsonPath("$.email").doesNotExist());
    }

    @Test void missingDetailsReturn404() throws Exception {
        for (String route : List.of("professionals", "estabilishments"))
            mvc.perform(get("/datasus/cnes/" + route + "/999")).andExpect(status().isNotFound());
    }

    @Test void invalidPaginationNeverQueriesDatabase() throws Exception {
        for (String route : List.of("professionals", "estabilishments")) {
            for (String[] pair : List.of(new String[]{"-1","10"}, new String[]{"0","0"}, new String[]{"0","101"}, new String[]{"x","10"})) {
                mvc.perform(get("/datasus/cnes/" + route).param("page-number",pair[0]).param("page-length",pair[1]))
                    .andExpect(status().isBadRequest());
            }
        }
        verifyNoInteractions(professionals,establishments);
    }

    @Test void explicitPageAndMaximumSize() throws Exception {
        when(professionals.findAll(any(Specification.class), any(Pageable.class))).thenAnswer(i -> Page.empty(i.getArgument(1)));
        when(establishments.findAll(any(Specification.class), any(Pageable.class))).thenAnswer(i -> Page.empty(i.getArgument(1)));
        for (String route : List.of("professionals", "estabilishments"))
            mvc.perform(get("/datasus/cnes/" + route).param("page-number","2").param("page-length","100"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.size").value(100)).andExpect(jsonPath("$.number").value(2));
    }

    static CnesProfessional professional(long id, String cnes) {
        CnesProfessional p = new CnesProfessional();
        p.setId(id); p.setName("Ana"); p.setCns("000000000000001"); p.setCnes(cnes);
        p.setCbo("2231F9"); p.setCboDescription("Especialidade"); p.setSus(true); p.setResident(false);
        return p;
    }
}
