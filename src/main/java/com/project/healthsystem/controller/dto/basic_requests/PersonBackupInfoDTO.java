package com.project.healthsystem.controller.dto.basic_requests;

import com.project.healthsystem.model.Patient;
import com.project.healthsystem.model.Person;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

@Data
@NoArgsConstructor
@Schema(name = "PersonBackupInfoDTO", description = "Dados enviados pelo frontend. PersonBackupInfoDTO.")
public class PersonBackupInfoDTO {

    @Schema(description = "Data e hora da última atualização na origem PEC. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, type = "string", example = "2026-11-08T09:30:00", nullable = true)
    private LocalDateTime lastUpdate;

    @Schema(description = "Identificação da microárea de acompanhamento.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private String microArea;
    @Schema(description = "Número do Cartão Nacional de Saúde como texto; preserve zeros à esquerda.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "000000000000001", nullable = true)
    private String cns;
    @Schema(description = "Nome da mãe do paciente.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private String motherName;

    @Schema(description = "Identificador sequencial do cidadão no PEC, como texto.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private String citizenSeqId;
    @Schema(description = "Identificador único do cidadão na origem PEC.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private String uniqueCitizenId;

    @Schema(description = "Nome do paciente.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private String patientName;
    @Schema(description = "Campo recebido do PEC que a implementação atual copia para sex; verifique a semântica na origem.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private String sexualOrientation;
    @Schema(description = "Identidade de gênero informada no cadastro. Campo textual; não é um enum do Vitalya.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private String gender;

    @Schema(description = "CPF como texto; preserve zeros à esquerda. No cadastro é validado e normalizado pelo backend.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private String cpf;

    @Schema(description = "Telefone celular recebido do PEC.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private String cellphone;
    @Schema(description = "Telefone residencial.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private String residentialPhone;
    @Schema(description = "Telefone de contato alternativo.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private String contactPhone;

    @Schema(description = "Data de nascimento recebida do PEC. Formato AAAA-MM-DD, por exemplo 2026-11-08.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, type = "string", format = "date", example = "1990-05-20", nullable = true)
    private LocalDate birthDate;

    @Schema(description = "Endereço de e-mail de contato.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "usuario@example.com", format = "email", nullable = true)
    private String email;

    @Schema(description = "Endereço completo informado no cadastro.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private String address;

    @Schema(hidden = true)
    public Patient getPersonToUpdate(List<Patient> patients){
        Patient patient = patients.get(0);

        setIfPresent(this.microArea, patient::setMicroArea);
        setIfPresent(this.cns, patient::setCns);
        setIfPresent(this.citizenSeqId, patient.getPerson()::setPersonSequenceId);
        setIfPresent(this.uniqueCitizenId, patient.getPerson()::setUniquePersonId);
        setIfPresent(this.cpf, patient.getPerson()::setCpf);
        setIfPresent(this.gender, patient.getPerson()::setGender);
        setIfPresent(this.sexualOrientation, patient.getPerson()::setSex);
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
        person.setGender(this.gender);
        person.setSex(this.sexualOrientation);
        person.setContactPhone(this.contactPhone);
        person.setResidentialPhone(this.residentialPhone);
        person.setCellPhone(this.cellphone);
        person.setPersonSequenceId(this.citizenSeqId);
        patient.setPerson(person);
        return patient;
    }

    @Schema(hidden = true)
    public boolean hasCpf(){ return this.cpf != null && !this.cpf.isEmpty(); }

    @Schema(hidden = true)
    public boolean hasCns(){ return this.cns != null && !this.cns.isEmpty(); }

    @Schema(hidden = true)
    public boolean hasCitizenSeqId(){ return this.citizenSeqId != null && !this.citizenSeqId.isEmpty(); }

    @Schema(hidden = true)
    private void setIfPresent(String value, Consumer<String> setter) {
        if (value != null && !value.isBlank()) {
            setter.accept(value);
        } else {
            setter.accept("");
        }
    }

    @Schema(hidden = true)
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
