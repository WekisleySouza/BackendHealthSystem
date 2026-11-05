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

class AppointmentRegulationFieldsTest {
    private final AppointmentRepository appointments = mock(AppointmentRepository.class);
    private final InstituitionRepository instituitions = mock(InstituitionRepository.class);
    private final AppointmentsMapper mapper = Mappers.getMapper(AppointmentsMapper.class);
    private final Cid10Repository cid10 = mock(Cid10Repository.class);
    private final Ciap2Repository ciap2 = mock(Ciap2Repository.class);
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
            professionals, instituitions, mock(AgreementRepository.class), mapper, cid10, ciap2);
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
        var professional = new Professional();
        professional.setPerson(person);
        when(professionals.findById(1L)).thenReturn(Optional.of(professional));
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


    @Test
    void createsAndReturnsRegulationFieldsWithoutChangingCodes() {
        fillRequest();
        var entity = validator.validateSave(request);
        assertEquals(CareModality.AMBULATORY, entity.getCareModality());
        assertEquals(RequestCharacter.ELECTIVE, entity.getRequestCharacter());
        assertEquals("001234", entity.getExecutingCbo());
        assertEquals("J35.0", entity.getClinicalReasonCode());
        assertEquals(request.getRequestedAt(), entity.getRequestedAt());
        var dto = mapper.toDto(entity);
        assertEquals("Ambulatorial", dto.getCareModality());
        assertEquals("Eletivo", dto.getRequestCharacter());
        assertEquals("CID-10", dto.getClinicalReasonSystem());
        assertEquals("J35.0", dto.getClinicalReasonCode());
        assertEquals("001234", dto.getExecutingCbo());
        assertEquals(request.getRequestedAt(), dto.getRequestedAt());
    }

    private void fillRequest() {
        request.setCareModality("Ambulatorial");
        request.setRequestCharacter("Eletivo");
        request.setClinicalReasonSystem("CID-10");
        request.setClinicalReasonCode("J35.0");
        when(cid10.existsByCode("J35.0")).thenReturn(true);
        request.setExecutingCbo("001234");
        request.setRequestedAt(java.time.LocalDateTime.of(2026, 1, 1, 9, 30));
    }

    @Test
    void legacyRequestKeepsNewFieldsNull() {
        var entity = validator.validateSave(request);
        assertNull(entity.getCareModality());
        assertNull(entity.getRequestCharacter());
        assertNull(entity.getClinicalReasonSystem());
        assertNull(entity.getClinicalReasonCode());
        assertNull(entity.getExecutingCbo());
        assertNull(entity.getRequestedAt());
        assertNull(mapper.toDto(entity).getCareModality());
    }

    @Test
    void updatePreservesOmittedFieldsAndChangesOnlySuppliedFields() {
        fillRequest();
        validator.validateUpdate(request, 1L);
        request = AppointmentBuilder.createValidRequestDTO();
        request.setExecutingCbo("225275");
        var entity = validator.validateUpdate(request, 1L);
        assertEquals(CareModality.AMBULATORY, entity.getCareModality());
        assertEquals(RequestCharacter.ELECTIVE, entity.getRequestCharacter());
        assertEquals("CID-10", entity.getClinicalReasonSystem());
        assertEquals("J35.0", entity.getClinicalReasonCode());
        assertEquals(java.time.LocalDateTime.of(2026, 1, 1, 9, 30), entity.getRequestedAt());
        assertEquals("225275", entity.getExecutingCbo());
        assertEquals(99L, entity.getInstituition().getId());
    }

    @Test
    void updatesAllFieldsIncludingCiapReason() {
        fillRequest();
        validator.validateUpdate(request, 1L);
        request.setCareModality("Hospitalar");
        request.setRequestCharacter("Urgente");
        request.setClinicalReasonSystem("CIAP-2");
        request.setClinicalReasonCode("R21");
        when(ciap2.existsByCode("R21")).thenReturn(true);
        request.setExecutingCbo("225275");
        request.setRequestedAt(java.time.LocalDateTime.of(2026, 2, 1, 10, 0));
        var entity = validator.validateUpdate(request, 1L);
        assertEquals(CareModality.HOSPITAL, entity.getCareModality());
        assertEquals(RequestCharacter.URGENT, entity.getRequestCharacter());
        assertEquals("CIAP-2", entity.getClinicalReasonSystem());
        assertEquals("R21", entity.getClinicalReasonCode());
        assertEquals("225275", entity.getExecutingCbo());
        assertEquals(request.getRequestedAt(), entity.getRequestedAt());
    }

    @ParameterizedTest
    @org.junit.jupiter.params.provider.CsvSource({
        "CareModality,INVALID", "CareModality,''",
        "RequestCharacter,INVALID", "RequestCharacter,''",
        "ExecutingCbo,12345", "ExecutingCbo,1234567",
        "ExecutingCbo,ABCDEF", "ExecutingCbo,2252-75", "ExecutingCbo,''",
        "ClinicalReasonSystem,OTHER", "ClinicalReasonCode,''",
        "ClinicalReasonCode,123456789012345678901"
    })
    void invalidFieldsRejectedOnCreateAndUpdate(String field, String value) throws Exception {
        fillRequest();
        AppointmentRequestDTO.class.getMethod("set" + field, String.class).invoke(request, value);
        assertThrows(InvalidDataException.class, () -> validator.validateSave(request));
        assertThrows(InvalidDataException.class, () -> validator.validateUpdate(request, 1L));
        verifyNoInteractions(appointments);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void incompleteReasonPairRejected(boolean systemOnly) {
        if (systemOnly) request.setClinicalReasonSystem("CID-10");
        else request.setClinicalReasonCode("J35.0");
        assertThrows(InvalidDataException.class, () -> validator.validateSave(request));
        assertThrows(InvalidDataException.class, () -> validator.validateUpdate(request, 1L));
    }

    @Test
    void futureRequestDateRejectedOnCreateAndUpdate() {
        request.setRequestedAt(java.time.LocalDateTime.now().plusDays(1));
        assertThrows(InvalidDataException.class, () -> validator.validateSave(request));
        assertThrows(InvalidDataException.class, () -> validator.validateUpdate(request, 1L));
    }

    @Test
    void jsonKeepsCodesAsStrings() throws Exception {
        var decoded = json.readValue("{\"clinicalReasonSystem\":\"CID-10\",\"clinicalReasonCode\":\"J35.0\",\"executingCbo\":\"001234\"}", AppointmentRequestDTO.class);
        assertEquals("001234", decoded.getExecutingCbo());
        var encoded = json.valueToTree(decoded);
        assertTrue(encoded.get("executingCbo").isTextual());
        assertEquals("J35.0", encoded.get("clinicalReasonCode").asText());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void detailReturnsNewFieldsAndSupportsLegacyRecords(boolean populated) {
        if (populated) {
            fillRequest();
            validator.validateUpdate(request, 1L);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("id", 1L);
        data.put("status", Status.COMPLETED);
        data.put("priorit", Priority.ELECTIVE);
        data.put("isReturn", false);
        data.put("employee", existing.getEmployee());
        data.put("patient", existing.getPatient());
        data.put("serviceType", existing.getServiceType());
        data.put("careModality", existing.getCareModality());
        data.put("requestCharacter", existing.getRequestCharacter());
        data.put("clinicalReasonSystem", existing.getClinicalReasonSystem());
        data.put("clinicalReasonCode", existing.getClinicalReasonCode());
        data.put("executingCbo", existing.getExecutingCbo());
        data.put("requestedAt", existing.getRequestedAt());
        var projection = new SpelAwareProxyProjectionFactory().createProjection(AppointmentGetByIdProjection.class, data);
        when(appointments.findAppointmentById(1L)).thenReturn(Optional.of(projection));
        var service = new AppointmentService(appointments, null, validator, mapper, null, null, null);
        var dto = service.findById(1L);
        assertEquals(populated ? "Ambulatorial" : null, dto.careModality());
        assertEquals(populated ? "Eletivo" : null, dto.requestCharacter());
        assertEquals(existing.getClinicalReasonSystem(), dto.clinicalReasonSystem());
        assertEquals(existing.getClinicalReasonCode(), dto.clinicalReasonCode());
        assertEquals(existing.getExecutingCbo(), dto.executingCbo());
        assertEquals(existing.getRequestedAt(), dto.requestedAt());
    }

    private void reason(String system, String code) {
        request.setClinicalReasonSystem(system);
        request.setClinicalReasonCode(code);
    }

    @ParameterizedTest
    @ValueSource(strings = {"CID-10", "CIAP-2"})
    void acceptsExistingCodeWithoutChangingItsText(String system) {
        String code = system.equals("CID-10") ? "A000" : "ABP001";
        reason(system, code);
        if (system.equals("CID-10")) when(cid10.existsByCode(code)).thenReturn(true);
        else when(ciap2.existsByCode(code)).thenReturn(true);
        var created = validator.validateSave(request);
        assertEquals(system, created.getClinicalReasonSystem());
        assertEquals(code, created.getClinicalReasonCode());
        if (system.equals("CID-10")) {
            verify(cid10).existsByCode(code);
            verifyNoInteractions(ciap2);
        } else {
            verify(ciap2).existsByCode(code);
            verifyNoInteractions(cid10);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"CID-10", "CIAP-2"})
    void rejectsMissingCodeOnCreateAndBothUpdateEntrypoints(String system) {
        reason(system, "UNKNOWN");
        existing.setClinicalReasonSystem("CID-10");
        existing.setClinicalReasonCode("OLD");
        var error = assertThrows(InvalidDataException.class, () -> validator.validateSave(request));
        assertTrue(error.getMessage().contains(system));
        assertTrue(error.getMessage().contains("UNKNOWN"));
        assertThrows(InvalidDataException.class, () -> validator.validateUpdate(request, 1L));
        assertThrows(InvalidDataException.class, () -> validator.validateUpdate(request, existing));
        assertEquals("OLD", existing.getClinicalReasonCode());
        verify(appointments, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"CID-10", "CIAP-2"})
    void preservesSameLegacyPairWithoutCatalogLookup(String system) {
        existing.setClinicalReasonSystem(system);
        existing.setClinicalReasonCode("LEGACY");
        reason(system, "LEGACY");
        assertEquals("LEGACY", validator.validateUpdate(request, 1L).getClinicalReasonCode());
        assertEquals("LEGACY", validator.validateUpdate(request, existing).getClinicalReasonCode());
        verifyNoInteractions(cid10, ciap2);
    }

    @Test
    void preservesOmittedLegacyReasonWithoutCatalogLookup() {
        existing.setClinicalReasonSystem("CID-10");
        existing.setClinicalReasonCode("LEGACY");
        assertEquals("LEGACY", validator.validateUpdate(request, existing).getClinicalReasonCode());
        assertEquals("CID-10", existing.getClinicalReasonSystem());
        verifyNoInteractions(cid10, ciap2);
    }

    @Test
    void optionalReasonDoesNotQueryCatalog() {
        validator.validateSave(request);
        verifyNoInteractions(cid10, ciap2);
    }

    @Test
    void changingOnlySystemStillRequiresCodeInNewCatalog() {
        existing.setClinicalReasonSystem("CID-10");
        existing.setClinicalReasonCode("A01");
        reason("CIAP-2", "A01");
        assertThrows(InvalidDataException.class, () -> validator.validateUpdate(request, existing));
        assertEquals("CID-10", existing.getClinicalReasonSystem());
        when(ciap2.existsByCode("A01")).thenReturn(true);
        assertEquals("CIAP-2", validator.validateUpdate(request, existing).getClinicalReasonSystem());
        verifyNoInteractions(cid10);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void changedCodeIsCheckedOncePerUpdate(boolean byId) {
        existing.setClinicalReasonSystem("CID-10");
        existing.setClinicalReasonCode("OLD");
        reason("CID-10", "J350");
        when(cid10.existsByCode("J350")).thenReturn(true);
        Appointment updated = byId ? validator.validateUpdate(request, 1L)
            : validator.validateUpdate(request, existing);
        assertEquals("J350", updated.getClinicalReasonCode());
        verify(cid10, times(1)).existsByCode("J350");
        verifyNoInteractions(ciap2);
    }

    @ParameterizedTest
    @ValueSource(strings = {"j350", "J35.0", " J350 "})
    void doesNotNormalizeCodeToMatchCatalog(String suppliedCode) {
        when(cid10.existsByCode("J350")).thenReturn(true);
        reason("CID-10", suppliedCode);
        assertThrows(InvalidDataException.class, () -> validator.validateSave(request));
        verify(cid10).existsByCode(suppliedCode);
    }
}
