package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.basic_requests.AgentRequestDTO;
import com.project.healthsystem.controller.dto.basic_responses.AgentResponseDTO;
import com.project.healthsystem.model.Agent;
import com.project.healthsystem.model.Person;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class AgentMapperTest {

    private final AgentMapper mapper = new AgentMapperImpl();

    @Test
    void shouldMapRequestDtoToAgentEntity() {
        AgentRequestDTO dto = createRequestDTO();

        Agent agent = mapper.toEntity(dto);

        assertThat(agent).isNotNull();
        assertThat(agent.getPerson()).isNotNull();

        Person person = agent.getPerson();

        assertThat(person.getName()).isEqualTo("Maria da Silva");
        assertThat(person.getCpf()).isEqualTo("12345678900");
        assertThat(person.getGender()).isEqualTo("Mulher Cisgênero");
        assertThat(person.getSex()).isEqualTo("Mulher Cisgênero");
        assertThat(person.getCellPhone()).isEqualTo("32999999999");
        assertThat(person.getResidentialPhone()).isEqualTo("3233333333");
        assertThat(person.getContactPhone()).isEqualTo("32988888888");
        assertThat(person.getBirthday()).isEqualTo(LocalDate.of(1995, 6, 15));
        assertThat(person.getAddress()).isEqualTo("Rua das Flores, 100");
        assertThat(person.getEmail()).isEqualTo("maria@email.com");
    }

    private AgentRequestDTO createRequestDTO() {
        AgentRequestDTO dto = new AgentRequestDTO();

        dto.setCpf("123.456.789-00");
        dto.setName("Maria da Silva");
        dto.setGender("Mulher Cisgênero");
        dto.setSex("Mulher Cisgênero");
        dto.setCellPhone("32999999999");
        dto.setResidentialPhone("3233333333");
        dto.setContactPhone("32988888888");
        dto.setBirthday(LocalDate.of(1995, 6, 15));
        dto.setAddress("Rua das Flores, 100");
        dto.setEmail("maria@email.com");

        return dto;
    }

    private AgentRequestDTO createUpdatedRequestDTO() {
        AgentRequestDTO dto = new AgentRequestDTO();

        dto.setCpf("987.654.321-00");
        dto.setName("Ana Souza");
        dto.setGender("Mulher Cisgênero");
        dto.setSex("Mulher Cisgênero");
        dto.setCellPhone("32977777777");
        dto.setResidentialPhone("3232222222");
        dto.setContactPhone("32966666666");
        dto.setBirthday(LocalDate.of(2000, 1, 10));
        dto.setAddress("Avenida Central, 200");
        dto.setEmail("ana@email.com");

        return dto;
    }

    private Agent createAgent() {
        Person person = new Person();

        person.setName("Maria da Silva");
        person.setSex("Feminino");
        person.setGender("Mulher Cisgênero");
        person.setCellPhone("32999999999");
        person.setResidentialPhone("3233333333");
        person.setContactPhone("32988888888");
        person.setCpf("12345678900");
        person.setAddress("Rua das Flores, 100");
        person.setBirthday(LocalDate.of(1995, 6, 15));
        person.setEmail("maria@email.com");

        Agent agent = new Agent();
        agent.setPerson(person);

        return agent;
    }

    private AgentRequestDTO createValidRequestDTO() {
        AgentRequestDTO dto = new AgentRequestDTO();

        dto.setName("Maria da Silva");
        dto.setSex("Feminino");
        dto.setGender("Mulher Cisgênero");
        dto.setCellPhone("32999999999");
        dto.setResidentialPhone("3233333333");
        dto.setContactPhone("32988888888");
        dto.setCpf("123.456.789-00");
        dto.setAddress("Rua das Flores, 100");
        dto.setBirthday(LocalDate.of(1995, 6, 15));
        dto.setEmail("maria@email.com");

        return dto;
    }

    @Test
    void shouldMapRequestDtoToPerson() {
        TestableAgentMapper testableMapper = new TestableAgentMapper();
        AgentRequestDTO dto = createValidRequestDTO();

        Person person = testableMapper.mapPublic(dto);

        assertThat(person).isNotNull();
        assertThat(person.getCpf()).isEqualTo("12345678900");
        assertThat(person.getName()).isEqualTo("Maria da Silva");
        assertThat(person.getGender()).isEqualTo("Mulher Cisgênero");
        assertThat(person.getSex()).isEqualTo("Feminino");
        assertThat(person.getCellPhone()).isEqualTo("32999999999");
        assertThat(person.getResidentialPhone()).isEqualTo("3233333333");
        assertThat(person.getContactPhone()).isEqualTo("32988888888");
        assertThat(person.getBirthday()).isEqualTo(LocalDate.of(1995, 6, 15));
        assertThat(person.getAddress()).isEqualTo("Rua das Flores, 100");
        assertThat(person.getEmail()).isEqualTo("maria@email.com");

    }

    private static class TestableAgentMapper extends AgentMapper {

        @Override
        public Agent toEntity(AgentRequestDTO dto) {
            return null;
        }

        @Override
        public AgentResponseDTO toDto(Agent agent) {
            return null;
        }

        public Person mapPublic(AgentRequestDTO dto) {
            return map(dto);
        }

    }

    @Test
    void shouldUpdateExistingAgentWhenHasId() {
        Agent agent = createAgent();
        Person originalPerson = agent.getPerson();
        AgentRequestDTO dto = createUpdatedRequestDTO();

        Agent updatedAgent = mapper.toEntityWhenHasId(agent, dto);

        assertThat(updatedAgent).isSameAs(agent);
        assertThat(updatedAgent.getPerson()).isSameAs(originalPerson);

        Person person = updatedAgent.getPerson();

        assertThat(person.getCpf()).isEqualTo("98765432100");
        assertThat(person.getName()).isEqualTo("Ana Souza");
        assertThat(person.getGender()).isEqualTo("Mulher Cisgênero");
        assertThat(person.getSex()).isEqualTo("Mulher Cisgênero");
        assertThat(person.getCellPhone()).isEqualTo("32977777777");
        assertThat(person.getResidentialPhone()).isEqualTo("3232222222");
        assertThat(person.getContactPhone()).isEqualTo("32966666666");
        assertThat(person.getBirthday()).isEqualTo(LocalDate.of(2000, 1, 10));
        assertThat(person.getAddress()).isEqualTo("Avenida Central, 200");
        assertThat(person.getEmail()).isEqualTo("ana@email.com");
    }

    @Test
    void shouldMapAgentToResponseDto() {
        Agent agent = createAgent();

        AgentResponseDTO dto = mapper.toDto(agent);

        assertThat(dto).isNotNull();
        assertThat(dto.getCpf()).isEqualTo("12345678900");
        assertThat(dto.getName()).isEqualTo("Maria da Silva");
        assertThat(dto.getGender()).isEqualTo("Mulher Cisgênero");
        assertThat(dto.getSex()).isEqualTo("Feminino");
        assertThat(dto.getCellPhone()).isEqualTo("32999999999");
        assertThat(dto.getResidentialPhone()).isEqualTo("3233333333");
        assertThat(dto.getContactPhone()).isEqualTo("32988888888");
        assertThat(dto.getBirthday()).isEqualTo(LocalDate.of(1995, 6, 15));
        assertThat(dto.getAddress()).isEqualTo("Rua das Flores, 100");
        assertThat(dto.getEmail()).isEqualTo("maria@email.com");

    }
}