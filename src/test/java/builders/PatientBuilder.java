package builders;

import com.project.healthsystem.model.*;

import java.util.ArrayList;
import java.util.List;

public class PatientBuilder {
    private String teamName = "Team Alpha";
    private String teamINE = "INE-001";
    private String microArea = "Area 01";
    private String origin = "SYSTEM";

    private String cns = "123456789012345";
    private String motherName = "Jane Doe";

    private Person person = PersonBuilder.builder().build();
    private Patient responsible = null;
    private List<Condition> conditions = new ArrayList<>();
    private Agent agent = null;
    private List<Appointment> appointments = new ArrayList<>();

    public static PatientBuilder builder() {
        return new PatientBuilder();
    }

    public PatientBuilder teamName(String teamName) {
        this.teamName = teamName;
        return this;
    }

    public PatientBuilder teamINE(String teamINE) {
        this.teamINE = teamINE;
        return this;
    }

    public PatientBuilder microArea(String microArea) {
        this.microArea = microArea;
        return this;
    }

    public PatientBuilder origin(String origin) {
        this.origin = origin;
        return this;
    }

    public PatientBuilder cns(String cns) {
        this.cns = cns;
        return this;
    }

    public PatientBuilder motherName(String motherName) {
        this.motherName = motherName;
        return this;
    }

    public PatientBuilder person(Person person) {
        this.person = person;
        return this;
    }

    public PatientBuilder responsible(Patient responsible) {
        this.responsible = responsible;
        return this;
    }

    public PatientBuilder conditions(List<Condition> conditions) {
        this.conditions = conditions;
        return this;
    }

    public PatientBuilder addCondition(Condition condition) {
        this.conditions.add(condition);
        return this;
    }

    public PatientBuilder agent(Agent agent) {
        this.agent = agent;
        return this;
    }

    public PatientBuilder appointments(List<Appointment> appointments) {
        this.appointments = appointments;
        return this;
    }

    public PatientBuilder addAppointment(Appointment appointment) {
        this.appointments.add(appointment);
        return this;
    }

    public Patient build() {
        Patient patient = new Patient();

        patient.setTeamName(teamName);
        patient.setTeamINE(teamINE);
        patient.setMicroArea(microArea);
        patient.setOrigin(origin);

        patient.setCns(cns);
        patient.setMotherName(motherName);

        patient.setPerson(person);
        patient.setResponsible(responsible);
        patient.setConditions(conditions);
        patient.setAgent(agent);
        patient.setAppointments(appointments);

        return patient;
    }
}
