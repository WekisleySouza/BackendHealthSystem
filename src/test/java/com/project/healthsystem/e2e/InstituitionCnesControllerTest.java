package com.project.healthsystem.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.healthsystem.controller.InstituitionController;
import com.project.healthsystem.controller.common.GlobalExceptionHandler;
import com.project.healthsystem.controller.mappers.InstituitionMapper;
import com.project.healthsystem.model.Instituition;
import com.project.healthsystem.repository.InstituitionRepository;
import com.project.healthsystem.service.InstituitionService;
import com.project.healthsystem.validator.InstituitionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class InstituitionCnesControllerTest {
    @Mock
    private InstituitionRepository repository;
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        var service = new InstituitionService(repository,
            new InstituitionValidator(repository), Mappers.getMapper(InstituitionMapper.class));
        mockMvc = MockMvcBuilders.standaloneSetup(new InstituitionController(service))
            .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t\n", "0012345"})
    void shouldCreateWithOptionalCnes(String cnes) throws Exception {
        when(repository.save(any(Instituition.class))).thenAnswer(invocation -> {
            Instituition institution = invocation.getArgument(0);
            institution.setId(1L);
            return institution;
        });

        mockMvc.perform(post("/instituition").contentType(MediaType.APPLICATION_JSON)
                .content(request(cnes, true)))
            .andExpect(status().isCreated());

        ArgumentCaptor<Instituition> captor = ArgumentCaptor.forClass(Instituition.class);
        verify(repository).save(captor.capture());
        assertEquals(cnes == null || cnes.isBlank() ? null : cnes, captor.getValue().getCnes());
        assertEquals("Unidade de teste", captor.getValue().getName());
    }

    @Test
    void shouldCreateWhenCnesIsOmitted() throws Exception {
        when(repository.save(any(Instituition.class))).thenAnswer(invocation -> {
            Instituition institution = invocation.getArgument(0);
            institution.setId(1L);
            return institution;
        });
        mockMvc.perform(post("/instituition").contentType(MediaType.APPLICATION_JSON)
                .content(request(null, false)))
            .andExpect(status().isCreated());
        ArgumentCaptor<Instituition> captor = ArgumentCaptor.forClass(Instituition.class);
        verify(repository).save(captor.capture());
        assertNull(captor.getValue().getCnes());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t\n", "0012345"})
    void shouldUpdateOrClearOptionalCnes(String cnes) throws Exception {
        Instituition existing = new Instituition();
        existing.setId(1L);
        existing.setCnes("7654321");
        when(repository.findById(1L)).thenReturn(Optional.of(existing));

        mockMvc.perform(put("/instituition/1").contentType(MediaType.APPLICATION_JSON)
                .content(request(cnes, true)))
            .andExpect(status().isNoContent());

        verify(repository).save(existing);
        assertEquals(cnes == null || cnes.isBlank() ? null : cnes, existing.getCnes());
    }

    @Test
    void shouldAllowUpdateWhenCnesIsOmitted() throws Exception {
        Instituition existing = new Instituition();
        existing.setId(1L);
        existing.setCnes("7654321");
        when(repository.findById(1L)).thenReturn(Optional.of(existing));

        mockMvc.perform(put("/instituition/1").contentType(MediaType.APPLICATION_JSON)
                .content(request(null, false)))
            .andExpect(status().isNoContent());

        verify(repository).save(existing);
        assertNull(existing.getCnes());
    }

    @ParameterizedTest
    @ValueSource(strings = {"123456", "12345678", "123456A", "123 456", " 0012345 ", "１２３４５６７"})
    void shouldRejectInvalidCnesOnCreateAndUpdate(String cnes) throws Exception {
        String json = request(cnes, true);
        mockMvc.perform(post("/instituition").contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("O CNES deve conter exatamente 7 dígitos."));
        mockMvc.perform(put("/instituition/1").contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("O CNES deve conter exatamente 7 dígitos."));
        verify(repository, never()).save(any(Instituition.class));
    }

    @Test
    void shouldReturnCnesInDetailAndListing() throws Exception {
        Instituition institution = new Instituition();
        institution.setId(1L);
        institution.setCnes("0012345");
        when(repository.findById(1L)).thenReturn(Optional.of(institution));
        when(repository.findAll(nullable(Specification.class), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(institution), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/instituition/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.cnes").value("0012345"));
        mockMvc.perform(get("/instituition"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].cnes").value("0012345"));
    }

    private String request(String cnes, boolean includeCnes) throws Exception {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("name", "Unidade de teste");
        body.put("cep", "12345678");
        body.put("address", "Rua de teste");
        if (includeCnes) {
            body.put("cnes", cnes);
        }
        return objectMapper.writeValueAsString(body);
    }
}
