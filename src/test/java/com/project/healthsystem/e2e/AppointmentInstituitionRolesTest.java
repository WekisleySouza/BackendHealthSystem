package com.project.healthsystem.e2e;

import builders.AppointmentBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.healthsystem.controller.dto.basic_requests.AppointmentRequestDTO;
import com.project.healthsystem.controller.mappers.AppointmentsMapper;
import com.project.healthsystem.exceptions.InvalidDataException;
import com.project.healthsystem.model.*;
import com.project.healthsystem.repository.*;
import com.project.healthsystem.repository.datasus.Cid10Repository;
import com.project.healthsystem.repository.datasus.Ciap2Repository;
import com.project.healthsystem.repository.projections.AppointmentGetByIdProjection;
import com.project.healthsystem.service.AppointmentService;
import com.project.healthsystem.validator.AppointmentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mapstruct.factory.Mappers;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppointmentInstituitionRolesTest {
    private static final String[] ROLES = {"Requesting", "Regulating", "Author", "Executing"};
    private final AppointmentRepository appointments = mock(AppointmentRepository.class);
    private final InstituitionRepository instituitions = mock(InstituitionRepository.class);
    private final AppointmentsMapper mapper = Mappers.getMapper(AppointmentsMapper.class);
    private AppointmentValidator validator;
    private AppointmentRequestDTO request;
    private Appointment existing;
    private final ObjectMapper json = new ObjectMapper();

    @BeforeEach
    void setup() {
        var patients = mock(PatientRepository.class);
        var services = mock(ServiceTypeRepository.class);
        var employees = mock(EmployeeRepository.class);
        var professionals = mock(ProfessionalRepository.class);
        validator = new AppointmentValidator(appointments, patients, services, employees,
            professionals, instituitions, mock(AgreementRepository.class), mapper, mock(Cid10Repository.class), mock(Ciap2Repository.class));
        request = AppointmentBuilder.createValidRequestDTO();
        var person = new Person();
        person.setName("Pessoa");
        var patient = new Patient();
        patient.setPerson(person);
        var employee = new Employee();
        employee.setPerson(person);
        var service = new ServiceType();
        service.setType(ServiceTypes.EXAM);
        service.setName("Exame");
        when(patients.findById(1L)).thenReturn(Optional.of(patient));
        when(employees.findById(1L)).thenReturn(Optional.of(employee));
        when(services.findById(1L)).thenReturn(Optional.of(service));
        when(professionals.findById(1L)).thenReturn(Optional.of(new Professional()));
        existing = AppointmentBuilder.builder().withPatient(patient).withEmployee(employee)
            .withServiceType(service).withStatus(Status.COMPLETED).withPriorit(Priority.ELECTIVE)
            .withInstituition(instituition(99L)).build();
        when(appointments.findById(1L)).thenReturn(Optional.of(existing));
    }

    private Instituition instituition(long id) {
        var value = new Instituition();
        value.setId(id);
        value.setName("Instituicao " + id);
        return value;
    }

    private void requestRole(String role, Long id) throws Exception {
        AppointmentRequestDTO.class.getMethod("set" + role + "InstituitionId", Long.class).invoke(request, id);
    }

    private void existingRole(String role, Instituition value) throws Exception {
        Appointment.class.getMethod("set" + role + "Instituition", Instituition.class).invoke(existing, value);
    }

    private Instituition role(Appointment appointment, String role) throws Exception {
        return (Instituition) Appointment.class.getMethod("get" + role + "Instituition").invoke(appointment);
    }

    @Test
    void createsFourIndependentRolesAndKeepsLegacyInstitution() throws Exception {
        request.setInstituitionId(99L);
        when(instituitions.findById(99L)).thenReturn(Optional.of(existing.getInstituition()));
        for (int i = 0; i < ROLES.length; i++) {
            long id = i + 10;
            requestRole(ROLES[i], id);
            when(instituitions.findById(id)).thenReturn(Optional.of(instituition(id)));
        }
        var result = validator.validateSave(request);
        assertEquals(99L, result.getInstituition().getId());
        for (int i = 0; i < ROLES.length; i++) {
            assertEquals(i + 10L, role(result, ROLES[i]).getId());
        }
    }

    @Test
    void oldRequestCreatesWithoutNewRolesOrAutomaticCopy() throws Exception {
        request.setInstituitionId(99L);
        when(instituitions.findById(99L)).thenReturn(Optional.of(existing.getInstituition()));
        var result = validator.validateSave(request);
        assertEquals(99L, result.getInstituition().getId());
        for (String name : ROLES) assertNull(role(result, name));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Requesting", "Regulating", "Author", "Executing"})
    void partialUpdateOnlyChangesSuppliedRole(String selected) throws Exception {
        for (String name : ROLES) existingRole(name, instituition(10L));
        requestRole(selected, 20L);
        when(instituitions.findById(20L)).thenReturn(Optional.of(instituition(20L)));
        var result = validator.validateUpdate(request, 1L);
        assertEquals(99L, result.getInstituition().getId());
        for (String name : ROLES) {
            assertEquals(name.equals(selected) ? 20L : 10L, role(result, name).getId());
        }
    }

    @Test
    void omittedAndExplicitNullRolesPreserveExistingValues() throws Exception {
        for (String name : ROLES) existingRole(name, instituition(10L));
        String oldJson = json.writeValueAsString(request);
        var tree = json.readTree(oldJson);
        for (String name : ROLES) {
            ((com.fasterxml.jackson.databind.node.ObjectNode) tree)
                .remove(Character.toLowerCase(name.charAt(0)) + name.substring(1) + "InstituitionId");
        }
        request = json.treeToValue(tree, AppointmentRequestDTO.class);
        validator.validateUpdate(request, 1L);
        request = json.readValue(oldJson, AppointmentRequestDTO.class);
        validator.validateUpdate(request, 1L);
        for (String name : ROLES) assertEquals(10L, role(existing, name).getId());
        verifyNoInteractions(instituitions);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Requesting", "Regulating", "Author", "Executing"})
    void rejectsUnknownAndNonpositiveIdsOnCreateAndUpdate(String name) throws Exception {
        for (long id : new long[]{0L, -1L, 404L}) {
            requestRole(name, id);
            assertThrows(InvalidDataException.class, () -> validator.validateSave(request));
            assertThrows(InvalidDataException.class, () -> validator.validateUpdate(request, 1L));
        }
        verify(appointments, never()).save(any());
    }

    @Test
    void sameInstitutionCanHaveAllRoles() throws Exception {
        when(instituitions.findById(10L)).thenReturn(Optional.of(instituition(10L)));
        for (String name : ROLES) requestRole(name, 10L);
        var result = validator.validateSave(request);
        for (String name : ROLES) assertEquals(10L, role(result, name).getId());
    }

    @Test
    void listResponseIncludesRoleIdsAndNamesAndLegacyField() throws Exception {
        for (int i = 0; i < ROLES.length; i++) existingRole(ROLES[i], instituition(i + 10));
        var response = json.valueToTree(mapper.toDto(existing));
        assertEquals("Instituicao 99", response.get("instituitionName").asText());
        for (int i = 0; i < ROLES.length; i++) {
            String prefix = Character.toLowerCase(ROLES[i].charAt(0)) + ROLES[i].substring(1);
            assertEquals(i + 10L, response.get(prefix + "InstituitionId").asLong());
            assertEquals("Instituicao " + (i + 10), response.get(prefix + "InstituitionName").asText());
        }
    }

    @Test
    void listResponseSupportsOldAppointmentsWithoutRoles() {
        var response = mapper.toDto(existing);
        assertNull(response.getRequestingInstituitionId());
        assertNull(response.getRegulatingInstituitionId());
        assertNull(response.getAuthorInstituitionId());
        assertNull(response.getExecutingInstituitionId());
        assertEquals("Instituicao 99", response.getInstituitionName());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void detailResponseIncludesOptionalNestedRoles(boolean includeRoles) throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("id", 1L);
        data.put("status", Status.COMPLETED);
        data.put("priorit", Priority.ELECTIVE);
        data.put("isReturn", false);
        data.put("employee", existing.getEmployee());
        data.put("patient", existing.getPatient());
        data.put("serviceType", existing.getServiceType());
        data.put("instituition", existing.getInstituition());
        for (int i = 0; i < ROLES.length; i++) {
            String prefix = Character.toLowerCase(ROLES[i].charAt(0)) + ROLES[i].substring(1);
            data.put(prefix + "Instituition", includeRoles ? instituition(i + 10L) : null);
        }
        var projection = new SpelAwareProxyProjectionFactory()
            .createProjection(AppointmentGetByIdProjection.class, data);
        when(appointments.findAppointmentById(1L)).thenReturn(Optional.of(projection));
        var service = new AppointmentService(appointments, null, validator, mapper, null, null, null);
        var response = json.valueToTree(service.findById(1L));
        assertEquals(99L, response.get("instituitionInfoResponseDTO").get("id").asLong());
        for (int i = 0; i < ROLES.length; i++) {
            String prefix = Character.toLowerCase(ROLES[i].charAt(0)) + ROLES[i].substring(1);
            var value = response.get(prefix + "Instituition");
            if (includeRoles) {
                assertEquals(i + 10L, value.get("id").asLong());
                assertEquals("Instituicao " + (i + 10), value.get("name").asText());
            } else assertTrue(value.isNull());
        }
    }
}
