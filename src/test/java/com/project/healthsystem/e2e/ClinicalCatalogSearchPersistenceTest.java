package com.project.healthsystem.e2e;

import com.project.healthsystem.model.datasus_data.cid.Cid10;
import com.project.healthsystem.model.datasus_data.ciap.Ciap2;
import com.project.healthsystem.repository.datasus.*;
import com.project.healthsystem.repository.specs.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
class ClinicalCatalogSearchPersistenceTest {
    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void datasource(DynamicPropertyRegistry properties) {
        properties.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        properties.add("spring.datasource.username", POSTGRES::getUsername);
        properties.add("spring.datasource.password", POSTGRES::getPassword);
        properties.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        properties.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired Cid10Repository cidRepository;
    @Autowired Ciap2Repository ciapRepository;

    void insert(boolean cid, String code, String description) {
        if (cid) {
            Cid10 entry = new Cid10();
            entry.setCode(code);
            entry.setDescription(description);
            entry.setActiveInPec(false);
            cidRepository.saveAndFlush(entry);
        } else {
            Ciap2 entry = new Ciap2();
            entry.setCode(code);
            entry.setDescription(description);
            entry.setDefaultFilterInPec(false);
            ciapRepository.saveAndFlush(entry);
        }
    }

    Page<String> search(boolean cid, String term, int page, int length) {
        var pageable = PageRequest.of(page, length, Sort.by("code").ascending());
        return cid
            ? cidRepository.findAll(Cid10Specs.search(term), pageable).map(Cid10::getCode)
            : ciapRepository.findAll(Ciap2Specs.search(term), pageable).map(Ciap2::getCode);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void matchesPartialCodeOrDescriptionIgnoringCase(boolean cid) {
        insert(cid, "J350", "AMIGDALITE CRÔNICA");
        insert(cid, "A000", "Outra descrição");
        assertEquals(List.of("J350"), search(cid, " j35 ", 0, 10).getContent());
        assertEquals(List.of("J350"), search(cid, "amigdal", 0, 10).getContent());
        assertTrue(search(cid, "inexistente", 0, 10).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void blankSearchListsAllSortedAndPaginatedIncludingFalsePecFlags(boolean cid) {
        insert(cid, "B02", "Segundo");
        insert(cid, "A01", "Primeiro");
        assertEquals(List.of("A01", "B02"), search(cid, null, 0, 10).getContent());
        var result = search(cid, "   ", 1, 1);
        assertEquals(List.of("B02"), result.getContent());
        assertEquals(2, result.getTotalElements());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void treatsLikeWildcardsAsLiteralText(boolean cid) {
        insert(cid, "A01", "Texto comum");
        insert(cid, "B02", "Texto 100%_!");
        assertEquals(List.of("B02"), search(cid, "%_!", 0, 10).getContent());
    }
}
