package com.project.healthsystem.e2e;

import com.project.healthsystem.model.abstractions.IDAbstraction;
import com.project.healthsystem.model.datasus_data.cid.Cid10;
import com.project.healthsystem.model.datasus_data.ciap.Ciap2;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
class ClinicalCatalogPersistenceTest {

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

    @Autowired
    private TestEntityManager entityManager;

    private IDAbstraction entry(boolean cid, String code, String description) {
        if (cid) {
            Cid10 entry = new Cid10();
            entry.setCode(code);
            entry.setDescription(description);
            return entry;
        }
        Ciap2 entry = new Ciap2();
        entry.setCode(code);
        entry.setDescription(description);
        return entry;
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void preservesCodesAndAllowsMissingPecMetadata(boolean cid) {
        String code = cid ? "A000" : "ABP001";
        String description = "Descrição ".repeat(50);
        IDAbstraction original = entry(cid, code, description);
        entityManager.persistAndFlush(original);
        entityManager.clear();

        assertTrue(original.getId() > 0);
        if (cid) {
            Cid10 saved = entityManager.find(Cid10.class, original.getId());
            assertEquals(code, saved.getCode());
            assertEquals(description, saved.getDescription());
            assertNull(saved.getPecId());
            assertNull(saved.getSex());
            assertNull(saved.getActiveInPec());
        } else {
            Ciap2 saved = entityManager.find(Ciap2.class, original.getId());
            assertEquals(code, saved.getCode());
            assertEquals(description, saved.getDescription());
            assertNull(saved.getPecId());
            assertNull(saved.getSex());
            assertNull(saved.getDefaultFilterInPec());
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void rejectsDuplicateCode(boolean cid) {
        entityManager.persistAndFlush(entry(cid, "A01", "Primeiro"));
        assertThrows(PersistenceException.class,
            () -> entityManager.persistAndFlush(entry(cid, "A01", "Duplicado")));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void requiresCode(boolean cid) {
        assertThrows(PersistenceException.class,
            () -> entityManager.persistAndFlush(entry(cid, null, "Descrição")));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void requiresDescription(boolean cid) {
        assertThrows(PersistenceException.class,
            () -> entityManager.persistAndFlush(entry(cid, "A01", null)));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void preservesPecMetadata(boolean cid) {
        IDAbstraction original = entry(cid, cid ? "A000" : "30", "Descrição");
        if (original instanceof Cid10 entry) {
            entry.setPecId(123L);
            entry.setSex("AMBOS");
            entry.setActiveInPec(false);
        } else if (original instanceof Ciap2 entry) {
            entry.setPecId(456L);
            entry.setSex("AMBOS");
            entry.setDefaultFilterInPec(false);
        }
        entityManager.persistAndFlush(original);
        entityManager.clear();

        if (cid) {
            Cid10 saved = entityManager.find(Cid10.class, original.getId());
            assertEquals(123L, saved.getPecId());
            assertEquals("AMBOS", saved.getSex());
            assertEquals(false, saved.getActiveInPec());
        } else {
            Ciap2 saved = entityManager.find(Ciap2.class, original.getId());
            assertEquals("30", saved.getCode());
            assertEquals(456L, saved.getPecId());
            assertEquals("AMBOS", saved.getSex());
            assertEquals(false, saved.getDefaultFilterInPec());
        }
    }
}
