package com.project.healthsystem.integration.service;

import com.project.healthsystem.service.datasus.*;
import com.project.healthsystem.model.datasus_data.cnes.*;
import com.project.healthsystem.repository.datasus.*;
import com.project.healthsystem.exceptions.InvalidDataException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
import java.nio.file.*;
import java.util.concurrent.*;
import jakarta.persistence.EntityManager;
import static org.junit.jupiter.api.Assertions.*;
import static builders.CnesFixtures.*;
import static com.project.healthsystem.service.datasus.CnesCsvReader.Catalog.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
@Import({CnesImportService.class,CnesCsvReader.class})
@Testcontainers(disabledWithoutDocker=true)
@Transactional(propagation=Propagation.NOT_SUPPORTED)
class CnesImportPersistenceTest {
    @Container static final PostgreSQLContainer<?> POSTGRES=new PostgreSQLContainer<>("postgres:16-alpine");
    @DynamicPropertySource static void datasource(DynamicPropertyRegistry p) {
        p.add("spring.datasource.url",POSTGRES::getJdbcUrl);
        p.add("spring.datasource.username",POSTGRES::getUsername);
        p.add("spring.datasource.password",POSTGRES::getPassword);
        p.add("spring.datasource.driver-class-name",()->"org.postgresql.Driver");
        p.add("spring.jpa.hibernate.ddl-auto",()->"create-drop");
    }
    @TempDir Path directory;
    @Autowired CnesImportService importer;
    @Autowired CnesEstabilishmentsRepository establishments;
    @Autowired CnesProfessionalsRepository professionals;
    @Autowired PlatformTransactionManager manager;
    @Autowired EntityManager em;
    @BeforeEach void setup() {
        new TransactionTemplate(manager).executeWithoutResult(s->{
            establishments.deleteAllInBatch(); professionals.deleteAllInBatch();
        });
    }
    @ParameterizedTest @ValueSource(booleans={true,false})
    void repeatedImportReplacesCatalogWithoutAccumulatingRows(boolean est) throws Exception {
        var catalog=est?ESTABLISHMENTS:PROFESSIONALS;
        String content=est?EST_HEADER+"\n"+establishment("0012345")
            :PROF_HEADER+"\n"+professional("000000000000001","0012345")
                +"\n"+professional("000000000000001","0012346");
        Path file=Files.writeString(directory.resolve("data.csv"),content);
        long count=est?1:2;
        importer.replace(file,catalog,count);
        importer.replace(file,catalog,count);
        assertEquals(count,est?establishments.count():professionals.count());
    }
    @ParameterizedTest @ValueSource(booleans={true,false})
    void failureAfterFirstBatchRestoresPreviousCatalog(boolean est) throws Exception {
        var catalog=est?ESTABLISHMENTS:PROFESSIONALS;
        String header=est?EST_HEADER:PROF_HEADER;
        String old=est?establishment("9999999"):professional("000000000000001","9999999");
        Path file=Files.writeString(directory.resolve("data.csv"),header+"\n"+old);
        importer.replace(file,catalog,1);
        long original=est?establishments.findAll().get(0).getId():professionals.findAll().get(0).getId();
        StringBuilder content=new StringBuilder(header).append('\n');
        for(int i=0;i<1000;i++) content.append(est?establishment(String.format("%07d",i))
            :professional("000000000000001",String.format("%07d",i))).append('\n');
        content.append(est?establishment("bad"):professional("1234","0012345"));
        Files.writeString(file,content);
        assertThrows(InvalidDataException.class,()->importer.replace(file,catalog,1001));
        assertEquals(1,est?establishments.count():professionals.count());
        assertTrue(est?establishments.existsById(original):professionals.existsById(original));
    }
    @Test void databaseLockBlocksAnotherTransaction() throws Exception {
        CountDownLatch locked=new CountDownLatch(1), release=new CountDownLatch(1);
        ExecutorService executor=Executors.newSingleThreadExecutor();
        Path file=Files.writeString(directory.resolve("data.csv"),EST_HEADER+"\n"+establishment("0012345"));
        try {
            Future<?> task=executor.submit(()->new TransactionTemplate(manager).executeWithoutResult(s->{
                em.createNativeQuery("select pg_advisory_xact_lock(:key)").setParameter("key",ESTABLISHMENTS.lockId()).getSingleResult();
                locked.countDown();
                try { if(!release.await(10,TimeUnit.SECONDS)) throw new IllegalStateException("timeout"); }
                catch(InterruptedException e) { Thread.currentThread().interrupt(); throw new RuntimeException(e); }
            }));
            assertTrue(locked.await(5,TimeUnit.SECONDS));
            assertThrows(InvalidDataException.class,()->importer.replace(file,ESTABLISHMENTS,1));
            release.countDown(); task.get(5,TimeUnit.SECONDS);
            assertEquals(1,importer.replace(file,ESTABLISHMENTS,1).importedRecords());
        } finally { release.countDown(); executor.shutdownNow(); }
    }
}
