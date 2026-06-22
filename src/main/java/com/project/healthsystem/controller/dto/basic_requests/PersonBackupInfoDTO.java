package com.project.healthsystem.controller.dto.basic_requests;

import com.project.healthsystem.model.Gender;
import com.project.healthsystem.model.Patient;
import com.project.healthsystem.model.Person;
import com.project.healthsystem.model.Sex;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

@Data
@NoArgsConstructor
public class PersonBackupInfoDTO {

    private LocalDateTime lastUpdate;

    private String microArea;
    private String cns;
    private String motherName;

    private String citizenSeqId;
    private String uniqueCitizenId;

    private String patientName;
    private String sexualOrientation;
    private String gender;

    private String cpf;

    private String cellphone;
    private String residentialPhone;
    private String contactPhone;

    private LocalDate birthDate;

    private String email;

    private String address;

    @Schema(hidden = true)
    public Patient getPersonToUpdate(List<Patient> patients){
        Patient patient = patients.get(0);

        setIfPresent(this.microArea, patient::setMicroArea);
        setIfPresent(this.cns, patient::setCns);
        setIfPresent(this.citizenSeqId, patient.getPerson()::setPersonSequenceId);
        setIfPresent(this.uniqueCitizenId, patient.getPerson()::setUniquePersonId);
        setIfPresent(this.cpf, patient.getPerson()::setCpf);
        setIfPresent(this.patientName, patient.getPerson()::setName);
        setIfPresent(this.address, patient.getPerson()::setAddress);
        setIfPresent(this.email, patient.getPerson()::setEmail);
        setIfPresent(this.contactPhone, patient.getPerson()::setContactPhone);
        setIfPresent(this.residentialPhone, patient.getPerson()::setResidentialPhone);
        setIfPresent(this.cellphone, patient.getPerson()::setCellPhone);

        return patient;
    }

    @Schema(hidden = true)
    public Patient getPersonToSave(){
        Patient patient = new Patient();
        Person person = new Person();
        patient.setOrigin("PEC");
        patient.setTeamINE("");
        patient.setTeamName("");
        patient.setMicroArea(this.microArea);
        patient.setCns(this.cns);
        person.setPersonSequenceId(this.citizenSeqId);
        person.setUniquePersonId(this.uniqueCitizenId);
        person.setCpf(this.cpf);
        person.setName(this.patientName);
        person.setAddress(this.address);
        person.setBirthday(this.birthDate);
        person.setEmail(this.email);
        person.setGender(Gender.fromLabel(this.gender));
        person.setSex(Sex.fromLabel(this.sexualOrientation));
        person.setContactPhone(this.contactPhone);
        person.setResidentialPhone(this.residentialPhone);
        person.setCellPhone(this.cellphone);
        person.setPersonSequenceId(this.citizenSeqId);
        patient.setPerson(person);
        return patient;
    }

    @Schema(hidden = true)
    public boolean hasCpf(){
        return !this.cpf.isEmpty();
    }

    @Schema(hidden = true)
    public boolean hasCns(){
        return !this.cns.isEmpty();
    }

    @Schema(hidden = true)
    private void setIfPresent(String value, Consumer<String> setter) {
        if (value != null && !value.isBlank()) {
            setter.accept(value);
        }
    }
}
