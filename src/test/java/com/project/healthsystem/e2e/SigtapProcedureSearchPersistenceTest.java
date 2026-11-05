package com.project.healthsystem.e2e;

import com.project.healthsystem.model.datasus_data.sigtap.*;
import com.project.healthsystem.repository.datasus.SigtapProcedureRepository;
import com.project.healthsystem.repository.specs.SigtapProcedureSpecs;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.*;
import org.springframework.data.domain.*;
import org.springframework.test.context.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
class SigtapProcedureSearchPersistenceTest {
    @Container static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");
    @DynamicPropertySource static void datasource(DynamicPropertyRegistry p) {
        p.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        p.add("spring.datasource.username", POSTGRES::getUsername);
        p.add("spring.datasource.password", POSTGRES::getPassword);
        p.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        p.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }
    @Autowired TestEntityManager em;
    @Autowired SigtapProcedureRepository repository;
    long id;

    @BeforeEach void setup() {
        var p = SigtapProcedureApiTest.fixture();
        p.setId(0);
        var org = p.getOrganization();
        em.persist(org.getSubGroup().getGroup());
        em.persist(org.getSubGroup());
        em.persist(org);
        id = em.persistAndFlush(p).getId();
        SigtapProcedure other = new SigtapProcedure();
        other.setCode("0201010001"); other.setName("Texto 100%_!"); other.setOrganization(org);
        em.persistAndFlush(other);
        em.clear();
    }

    Page<SigtapProcedure> search(String term, int page, int size) {
        return repository.findAll(SigtapProcedureSpecs.search(term),
            PageRequest.of(page, size, Sort.by("code","id")));
    }

    @Test void partialSearchMatchesCodeOrNameIgnoringCase() {
        assertEquals(id, search(" ação ",0,10).getContent().get(0).getId());
        assertEquals(id, search("010101",0,10).getContent().get(0).getId());
        assertEquals(1, search("%_!",0,10).getTotalElements());
        assertTrue(search("inexistente",0,10).isEmpty());
    }

    @Test void listsSortedPagesAndFetchesHierarchy() {
        var page = search(" ",0,1);
        assertEquals(2, page.getTotalElements());
        assertEquals(id,page.getContent().get(0).getId());
        em.clear();
        assertEquals("Grupo",page.getContent().get(0).getOrganization().getSubGroup().getGroup().getName());
        assertEquals("0201010001",search(null,1,1).getContent().get(0).getCode());
    }

    @Test void detailFetchesHierarchy() {
        var result = repository.findById(id).orElseThrow();
        em.clear();
        assertEquals("Grupo",result.getOrganization().getSubGroup().getGroup().getName());
    }
}
