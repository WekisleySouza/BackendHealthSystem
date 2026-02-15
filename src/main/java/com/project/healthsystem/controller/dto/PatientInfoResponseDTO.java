package com.project.healthsystem.controller.dto;

import java.time.LocalDate;
import java.util.List;

public record PatientInfoResponseDTO(
    long id,

    String teamName,
    String teamINE,
    String microArea,
    String origin,
    PatientInfoAgentResponseDTO agent,
    PatientInfoResponsibleResponseDTO responsible,
    List<ConditionResponseDTO> conditions,
    List<PatientInfoAppointmentResponseDTO> appointments,
    String name,
    String gender,
    String sex,
    String motherName,
    LocalDate birthday,
    String cns,
    String cpf,
    String address,
    String cellPhone,
    String residentialPhone,
    String contactPhone,
    String email
) {
}
