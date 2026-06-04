package com.project.healthsystem.utils;

import com.project.healthsystem.model.Gender;
import com.project.healthsystem.model.Patient;
import com.project.healthsystem.model.Person;
import com.project.healthsystem.model.Sex;
import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Data
public class PersonBackupInfo {
    private LocalDateTime lastUpdate;

    private String microArea;
    private String cns;
    private String motherName;

    private String citizenSeqId;
    private String uniqueCitizenId;

    private String patientName;
    private Sex sexualOrientation;
    private Gender gender;

    private String cpf;

    private String cellphone;
    private String residentialPhone;
    private String contactPhone;

    private LocalDate birthDate;

    private String email;

    private String address;

    public PersonBackupInfo(Map<String, Object> content) {
        this.lastUpdate = ((Timestamp) content.get("dt_atualizado"))
            .toLocalDateTime();
        this.microArea = String.valueOf(content.get("nu_micro_area"));
        this.cns = String.valueOf(content.get("nu_cns"));
        this.cpf = String.valueOf(content.get("nu_cpf"));
        this.motherName = String.valueOf(content.get("no_mae"));
        this.citizenSeqId = String.valueOf(content.get("co_seq_cidadao"));
        this.uniqueCitizenId = String.valueOf(content.get("co_unico_cidadao"));
        this.patientName = String.valueOf(content.get("no_cidadao"));
        this.setSexualOrientationNormalized(String.valueOf(content.get("tp_orientacao_sexual")));
        this.setGenderNormalized(String.valueOf(content.get("no_sexo")));
        this.cellphone = String.valueOf(content.get("nu_telefone_celular"));
        this.residentialPhone = String.valueOf(content.get("nu_telefone_residencial"));
        this.contactPhone = String.valueOf(content.get("nu_telefone_contato"));
        this.birthDate = ((Date) content.get("dt_nascimento")).toLocalDate();
        this.email = String.valueOf(content.get("ds_email"));
        this.address = String.valueOf(content.get("address"));

        this.normalize();
    }

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
        person.setGender(this.gender);
        person.setSex(this.sexualOrientation);
        person.setContactPhone(this.contactPhone);
        person.setResidentialPhone(this.residentialPhone);
        person.setCellPhone(this.cellphone);
        person.setPersonSequenceId(this.citizenSeqId);
        patient.setPerson(person);
        return patient;
    }

    public boolean hasCpf(){
        return !this.cpf.isEmpty();
    }

    public boolean hasCns(){
        return !this.cns.isEmpty();
    }

    private void setIfPresent(String value, Consumer<String> setter) {
        if (value != null && !value.isBlank()) {
            setter.accept(value);
        }
    }

    private void setSexualOrientationNormalized(String sexLabel){
        if(sexLabel.equals("FEMININO")){
            this.setSexualOrientation(Sex.FEMALE);
        } else if (sexLabel.equals("INDETERMINADO")) {
            this.setSexualOrientation(Sex.UNKNOW);
        } else {
            this.setSexualOrientation(Sex.MALE);
        }
    }

    private void setGenderNormalized(String genderLabel){
        if(genderLabel.equals("BISSEXUAL")){
            this.setGender(Gender.BISEXUAL);
        } else if (genderLabel.equals("GAY")) {
            this.setGender(Gender.HOMOSEXUAL);
        } else if (genderLabel.equals("HETEROSSEXUAL")) {
            this.setGender(Gender.MALE);
        } else if (genderLabel.equals("HOMOSSEXUAL")) {
            this.setGender(Gender.HOMOSEXUAL);
        } else if (genderLabel.equals("LESBICA")) {
            this.setGender(Gender.HOMOSEXUAL);
        } else {
            this.setGender(Gender.NOT_INFORMED);
        }
    }

    public void normalize() {
        this.cns = onlyNumbers(this.cns);
        this.cpf = onlyNumbers(this.cpf);

        this.cellphone = onlyNumbers(this.cellphone);
        this.residentialPhone = onlyNumbers(this.residentialPhone);
        this.contactPhone = onlyNumbers(this.contactPhone);
    }

    private String onlyNumbers(String value) {
        if (value == null) {
            return null;
        }

        return value.replaceAll("\\D", "");
    }

    @Override
    public String toString() {
        return """
        PersonBackupInfo {
            patientName='%s',
            uniqueCitizenId='%s',
            citizenSeqId='%s',
            cpf='%s',

            gender='%s',
            sexualOrientation='%s',
            birthDate=%s,
            motherName='%s',

            cns='%s',
            microArea='%s',

            cellphone='%s',
            residentialPhone='%s',
            contactPhone='%s',
            email='%s',

            address='%s',

            lastUpdate=%s
        }
        """.formatted(
                patientName,
                uniqueCitizenId,
                citizenSeqId,
                cpf,
                gender,
                sexualOrientation,
                birthDate,
                motherName,
                cns,
                microArea,
                cellphone,
                residentialPhone,
                contactPhone,
                email,
                address,
                lastUpdate
        );
    }
}
