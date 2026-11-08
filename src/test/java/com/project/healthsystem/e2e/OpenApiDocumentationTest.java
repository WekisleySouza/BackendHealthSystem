package com.project.healthsystem.e2e;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.healthsystem.controller.*;
import com.project.healthsystem.config.OpenApiConfiguration;
import com.project.healthsystem.controller.common.GlobalExceptionHandler;
import com.project.healthsystem.controller.dto.basic_requests.AppointmentRequestDTO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import java.nio.file.*;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = OpenApiDocumentationTest.TestApp.class, properties = {
    "springdoc.api-docs.enabled=true", "springdoc.api-docs.path=/v3/api-docs",
    "spring.main.banner-mode=off", "springdoc.writer-with-order-by-keys=true"
})
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OpenApiDocumentationTest {
    static final Class<?>[] CONTROLLERS = {AgentController.class, AgreementController.class,
        AppointmentsController.class, AuthController.class, CategoryGroupController.class,
        ConditionController.class, DataSusController.class, EmployeeController.class,
        InstituitionController.class, LoginController.class, PatientController.class,
        ProfessionalController.class, ServiceTypeController.class, SurgeryController.class, SurgeryTypeController.class};

    @org.springframework.context.annotation.Configuration
    @EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class,
        SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class,
        org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration.class})
    @Import({AgentController.class,AgreementController.class,AppointmentsController.class,AuthController.class,
        CategoryGroupController.class,ConditionController.class,DataSusController.class,EmployeeController.class,
        InstituitionController.class,LoginController.class,PatientController.class,ProfessionalController.class,
        ServiceTypeController.class,SurgeryController.class,SurgeryTypeController.class,
        OpenApiConfiguration.class,GlobalExceptionHandler.class})
    static class TestApp {
        @Bean static BeanFactoryPostProcessor mockedDependencies() {
            return factory -> {
                BeanDefinitionRegistry registry=(BeanDefinitionRegistry)factory;
                Set<Class<?>> types=new HashSet<>();
                for(Class<?> controller:CONTROLLERS)
                    types.addAll(Arrays.asList(controller.getConstructors()[0].getParameterTypes()));
                for(Class<?> type:types) {
                    factory.registerSingleton(type.getName(),mock(type));
                }
            };
        }
    }

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    JsonNode api;
    @BeforeAll void loadDocument() throws Exception {
        String json=mvc.perform(get("/v3/api-docs")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        api=mapper.readTree(json);
        Files.createDirectories(Path.of("target"));
        Files.writeString(Path.of("target/openapi-frontend.json"),json);
    }
    JsonNode schema(String name) {return api.path("components").path("schemas").path(name);}
    JsonNode operation(String path,String verb){return api.path("paths").path(path).path(verb);}
    @Test void everyOperationInheritsDescriptionAndTag() {
        int count=0;
        for(JsonNode path:api.path("paths")) {
            for(JsonNode op:path) {
                if(!op.has("responses"))continue;
                count++;
                assertFalse(op.path("summary").asText().isBlank(),op.toString());
                assertFalse(op.path("description").asText().isBlank(),op.toString());
                assertFalse(op.path("tags").isEmpty(),op.toString());
            }
        }
        assertEquals(100,count);
    }
    @Test void controllersHaveNoSwaggerOperationAnnotations() {
        for(Class<?> cls:CONTROLLERS) {
            assertEquals(1,cls.getInterfaces().length);
            for(var method:cls.getDeclaredMethods())
                assertNull(method.getDeclaredAnnotation(io.swagger.v3.oas.annotations.Operation.class));
        }
    }
    @Test void appointmentSchemaUsesActualLabelsAndRequiredFields() {
        var s=schema("AppointmentRequestDTO");
        assertTrue(s.path("required").toString().contains("requestingProfessionalId"));
        assertTrue(s.path("properties").path("careModality").path("enum").toString().contains("Ambulatorial"));
        assertFalse(s.path("properties").path("careModality").path("enum").toString().contains("AMBULATORY"));
        assertEquals("^[0-9]{6}$",s.path("properties").path("executingCbo").path("pattern").asText());
        assertTrue(s.path("properties").path("requestingInstituitionId").path("description").asText().contains("preserva"));
    }
    @Test void documentationMatchesJacksonBooleanProperty() throws Exception {
        var tree=mapper.valueToTree(new AppointmentRequestDTO());
        assertTrue(tree.has("return"));
        assertTrue(schema("AppointmentRequestDTO").path("properties").has("return"));
        assertFalse(schema("AppointmentRequestDTO").path("properties").has("isReturn"));
    }
    @Test void duplicateSimpleDtoNamesHaveDistinctSchemas() {
        assertFalse(schema("BasicResponsesInstituitionResponseDTO").isMissingNode());
        assertFalse(schema("AuthorizationFormInstituitionResponseDTO").isMissingNode());
        assertFalse(schema("PatientInfoResponseDTO").isMissingNode());
        assertFalse(schema("AppointmentGetByIdPatientInfoResponseDTO").isMissingNode());
    }
    @Test void paginationAndPdfKeepTheirResponseShapes() {
        var response=operation("/datasus/cnes/professionals","get").path("responses").path("200");
        assertTrue(response.toString().contains("PageCnesProfessionalResponseDTO"),response.toString());
        assertTrue(operation("/appointments/report-patients-pdf","get").path("responses").path("200").path("content").has("application/pdf"));
        assertFalse(operation("/appointments","post").path("responses").path("201").has("content"));
    }
    @Test void loginIsPublicAndErrorsAreObjects() {
        assertTrue(operation("/auth/login","post").path("security").isEmpty());
        assertTrue(operation("/appointments","post").path("responses").path("422").toString().contains("ErrorResponseDTO"));
        assertFalse(operation("/appointments","post").path("responses").path("422").toString().contains("\"type\":\"array\""));
    }
    @Test void cnesPropertiesHaveDescriptionsAndCodesStayStrings() {
        for(String name:List.of("CnesProfessionalResponseDTO","CnesEstabilishmentResponseDTO")) {
            var fields=schema(name).path("properties").fields();
            while(fields.hasNext()) {
                var field=fields.next();
                assertFalse(field.getValue().path("description").asText().isBlank(),name+"."+field.getKey());
            }
        }
        assertEquals("string",schema("CnesProfessionalResponseDTO").path("properties").path("cns").path("type").asText());
    }
    @Test void newCommonStatusDoesNotAdvertisePreScheduled() {
        assertFalse(schema("NewCommonStatusDTO").path("properties").path("status").path("enum").toString().contains("Pré agendado"));
    }

    @Test void pdfFiltersUseActualBindParamNames() {
        var names=new HashSet<String>();
        operation("/appointments/report-patients-pdf","get").path("parameters").forEach(p -> names.add(p.path("name").asText()));
        assertTrue(names.containsAll(Set.of("patient-name","mother-name","scheduled-at-start","created-at-end","fields","sort")));
        assertFalse(names.contains("patientName"));
    }

    @Test void allGeneratedPropertiesHaveDescriptions() {
        var all=api.path("components").path("schemas").fields();
        while(all.hasNext()) {
            var schema=all.next();
            var fields=schema.getValue().path("properties").fields();
            while(fields.hasNext()) {
                var field=fields.next();
                assertFalse(field.getValue().path("description").asText().isBlank(),schema.getKey()+"."+field.getKey());
            }
        }
    }

    @Test void roleOptionsAreDocumentedOnArrayItems() {
        var roles=schema("EmployeeRequestDTO").path("properties").path("roles");
        assertTrue(roles.path("items").path("enum").toString().contains("MANAGER"));
    }

    @Test void inheritedDocumentationDoesNotBreakBodyValidation() throws Exception {
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/auth/login")
            .contentType("application/json").content("{\"login\":\"\",\"password\":\"x\"}"))
            .andExpect(status().isUnprocessableEntity());
    }
}
