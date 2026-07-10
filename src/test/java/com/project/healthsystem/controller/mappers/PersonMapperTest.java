package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.basic_requests.AgentRequestDTO;
import com.project.healthsystem.controller.dto.basic_requests.EmployeeRequestDTO;
import com.project.healthsystem.controller.dto.basic_requests.PatientRequestDTO;
import com.project.healthsystem.controller.dto.basic_requests.ProfessionalRequestDTO;
import com.project.healthsystem.model.Person;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PersonMapperTest {
    private final PersonMapper mapper = new PersonMapper();

    @Test
    void fromAgentToPersonEntity() {
        AgentRequestDTO dto = this.createAgentRequestDTO();
        Person person = mapper.toPersonEntity(dto);
        this.assertMappedPerson(person);
    }

    @Test
    void updatePersonEntityFromAgent() {
        Person existingPerson = this.createExistingPerson();
        AgentRequestDTO dto = this.createAgentRequestDTO();
        Person updatedPerson = mapper.updatePersonEntity(existingPerson, dto);
        assertThat(updatedPerson).isSameAs(existingPerson);
        this.assertMappedPerson(updatedPerson);
    }

    @Test
    void fromProfessionalToPersonEntity() {
        ProfessionalRequestDTO dto = this.createProfessionalRequestDTO();
        Person person = mapper.toPersonEntity(dto);
        this.assertMappedPerson(person);
    }

    @Test
    void updatePersonEntityFromProfessional() {
        Person existingPerson = this.createExistingPerson();
        ProfessionalRequestDTO dto = this.createProfessionalRequestDTO();
        Person updatedPerson = mapper.updatePersonEntity(existingPerson, dto);
        assertThat(updatedPerson).isSameAs(existingPerson);
        this.assertMappedPerson(updatedPerson);
    }

    @Test
    void fromPatientToPersonEntity() {
        PatientRequestDTO dto = this.createPatientRequestDTO();
        Person person = mapper.toPersonEntity(dto);
        this.assertMappedPerson(person);
    }

    @Test
    void updatePersonEntityFromPatient() {
        Person existingPerson = this.createExistingPerson();
        PatientRequestDTO dto = this.createPatientRequestDTO();
        Person updatedPerson = mapper.updatePersonEntity(existingPerson, dto);
        assertThat(updatedPerson).isSameAs(existingPerson);
        this.assertMappedPerson(updatedPerson);
    }

    @Test
    void fromEmployeeToPersonEntity() {
        EmployeeRequestDTO dto = this.createEmployeeRequestDTO();
        Person person = mapper.toPersonEntity(dto);
        this.assertMappedPerson(person);
    }

    @Test
    void updatePersonEntityFromEmployee() {
        Person existingPerson = this.createExistingPerson();
        EmployeeRequestDTO dto = this.createEmployeeRequestDTO();
        Person updatedPerson = mapper.updatePersonEntity(existingPerson, dto);
        assertThat(updatedPerson).isSameAs(existingPerson);
        this.assertMappedPerson(updatedPerson);
    }

    private void assertMappedPerson(Person person) {
        assertThat(person).isNotNull();
        assertThat(person.getName()).isEqualTo("Andressa");
        assertThat(person.getGender()).isEqualTo("Heterosexual");
        assertThat(person.getCpf()).isEqualTo("00000000000");
        assertThat(person.getBirthday()).isEqualTo(LocalDate.of(1997, 3, 17));
        assertThat(person.getEmail()).isEqualTo("andressa@gmail.com");
        assertThat(person.getSex()).isEqualTo("Feminino");
        assertThat(person.getCellPhone()).isEqualTo("(xx) xxxxx-xxxx");
        assertThat(person.getContactPhone()).isEqualTo("(xx) xxxxx-xxxx");
        assertThat(person.getResidentialPhone()).isEqualTo("(xx) xxxxx-xxxx");
        assertThat(person.getAddress()).isEqualTo("Rua x, Cidade x, Número x");
    }

    private AgentRequestDTO createAgentRequestDTO(){
        AgentRequestDTO agentRequestDTO = new AgentRequestDTO();
        agentRequestDTO.setName("Andressa");
        agentRequestDTO.setGender("Heterosexual");
        agentRequestDTO.setCpf("00000000000");
        agentRequestDTO.setBirthday(LocalDate.of(1997, 3, 17));
        agentRequestDTO.setEmail("andressa@gmail.com");
        agentRequestDTO.setSex("Feminino");
        agentRequestDTO.setCellPhone("(xx) xxxxx-xxxx");
        agentRequestDTO.setContactPhone("(xx) xxxxx-xxxx");
        agentRequestDTO.setResidentialPhone("(xx) xxxxx-xxxx");
        agentRequestDTO.setAddress("Rua x, Cidade x, Número x");
        return agentRequestDTO;
    }

    private ProfessionalRequestDTO createProfessionalRequestDTO(){
        ProfessionalRequestDTO professionalRequestDTO = new ProfessionalRequestDTO();
        professionalRequestDTO.setName("Andressa");
        professionalRequestDTO.setGender("Heterosexual");
        professionalRequestDTO.setCpf("00000000000");
        professionalRequestDTO.setBirthday(LocalDate.of(1997, 3, 17));
        professionalRequestDTO.setEmail("andressa@gmail.com");
        professionalRequestDTO.setSex("Feminino");
        professionalRequestDTO.setCellPhone("(xx) xxxxx-xxxx");
        professionalRequestDTO.setContactPhone("(xx) xxxxx-xxxx");
        professionalRequestDTO.setResidentialPhone("(xx) xxxxx-xxxx");
        professionalRequestDTO.setAddress("Rua x, Cidade x, Número x");
        return professionalRequestDTO;
    }

    private PatientRequestDTO createPatientRequestDTO(){
        PatientRequestDTO patientRequestDTO = new PatientRequestDTO();
        patientRequestDTO.setName("Andressa");
        patientRequestDTO.setGender("Heterosexual");
        patientRequestDTO.setCpf("00000000000");
        patientRequestDTO.setBirthday(LocalDate.of(1997, 3, 17));
        patientRequestDTO.setEmail("andressa@gmail.com");
        patientRequestDTO.setSex("Feminino");
        patientRequestDTO.setCellPhone("(xx) xxxxx-xxxx");
        patientRequestDTO.setContactPhone("(xx) xxxxx-xxxx");
        patientRequestDTO.setResidentialPhone("(xx) xxxxx-xxxx");
        patientRequestDTO.setAddress("Rua x, Cidade x, Número x");
        return patientRequestDTO;
    }

    private EmployeeRequestDTO createEmployeeRequestDTO(){
        EmployeeRequestDTO employeeRequestDTO = new EmployeeRequestDTO();
        employeeRequestDTO.setName("Andressa");
        employeeRequestDTO.setGender("Heterosexual");
        employeeRequestDTO.setCpf("00000000000");
        employeeRequestDTO.setBirthday(LocalDate.of(1997, 3, 17));
        employeeRequestDTO.setEmail("andressa@gmail.com");
        employeeRequestDTO.setSex("Feminino");
        employeeRequestDTO.setCellPhone("(xx) xxxxx-xxxx");
        employeeRequestDTO.setContactPhone("(xx) xxxxx-xxxx");
        employeeRequestDTO.setResidentialPhone("(xx) xxxxx-xxxx");
        employeeRequestDTO.setAddress("Rua x, Cidade x, Número x");
        return employeeRequestDTO;
    }

    private Person createExistingPerson(){
        Person person = new Person();
        person.setName("Jordana");
        person.setGender("Heterosexual");
        person.setCpf("00000000000");
        person.setBirthday(LocalDate.of(1997, 3, 17));
        person.setEmail("andressa@gmail.com");
        person.setSex("Feminino");
        person.setCellPhone("(xx) xxxxx-xxxx");
        person.setContactPhone("(xx) xxxxx-xxxx");
        person.setResidentialPhone("(xx) xxxxx-xxxx");
        person.setAddress("Rua x, Cidade x, Número x");
        return person;
    }
}