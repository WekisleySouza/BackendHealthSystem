package com.project.healthsystem.integration.service;

import builders.SigtapFixtures;
import com.project.healthsystem.service.datasus.SigtapImportService;
import com.project.healthsystem.model.datasus_data.sigtap.*;
import com.project.healthsystem.repository.datasus.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.*;
import org.springframework.transaction.annotation.*;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.*;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(SigtapImportService.class)
@Testcontainers(disabledWithoutDocker = true)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class SigtapServiceTest {
    @Container static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");
    @DynamicPropertySource static void datasource(DynamicPropertyRegistry p) {
        p.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        p.add("spring.datasource.username", POSTGRES::getUsername);
        p.add("spring.datasource.password", POSTGRES::getPassword);
        p.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        p.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }
    @TempDir Path directory;
    @Autowired SigtapImportService importer;
    @Autowired SigtapGroupRepository groups;
    @Autowired SigtapSubGroupRepository subs;
    @Autowired SigtapOrganizationRepository orgs;
    @Autowired SigtapProcedureRepository procedures;
    @Autowired SigtapVersionRepository versions;
    @Autowired PlatformTransactionManager manager;

    @BeforeEach void setup() throws Exception {
        new TransactionTemplate(manager).executeWithoutResult(s -> {
            procedures.deleteAllInBatch(); orgs.deleteAllInBatch();
            subs.deleteAllInBatch(); groups.deleteAllInBatch(); versions.deleteAllInBatch();
        });
        SigtapFixtures.write(directory);
    }

    SigtapVersion version() { return SigtapVersion.build(Path.of("TabelaUnificada_202611_v2611051200.zip")); }

    @Test void reimportKeepsOneVersionAndCorrectHierarchy() throws Exception {
        importer.importData(directory, version(), true);
        importer.importData(directory, version(), true);
        assertEquals(1, versions.count());
        assertEquals(2, procedures.count());
        new TransactionTemplate(manager).executeWithoutResult(s ->
            procedures.findAll().forEach(p -> assertTrue(p.getCode().startsWith(
                p.getOrganization().getSubGroup().getGroup().getCode()))));
    }

    @Test void lateDatabaseFailureRollsBackEntireReplacement() throws Exception {
        importer.importData(directory, version(), true);
        long originalId = procedures.findAll().get(0).getId();
        SigtapVersion invalid = version();
        invalid.setFileName("x".repeat(300));
        assertThrows(RuntimeException.class, () -> importer.importData(directory, invalid, true));
        assertTrue(procedures.existsById(originalId));
        assertEquals(2, procedures.count());
        assertEquals(version().getFileName(), versions.findFirstByOrderByCompetenceDesc().orElseThrow().getFileName());
    }
}
