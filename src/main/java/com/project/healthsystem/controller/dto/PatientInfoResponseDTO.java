package com.project.healthsystem.controller.dto;

import java.time.LocalDate;
import java.util.List;

public record PatientInfoResponseDTO(
    long id,
    PatientInfoAgentResponseDTO agent,
    PatientInfoResponsibleResponseDTO responsible,
    List<ConditionResponseDTO> conditions,
    List<PatientInfoAppointmentResponseDTO> appointments,
    String name,
    String gender,
    String motherName,
    LocalDate birthday,
    String cns,
    String cpf,
    String address,
    String phone,
    String email
) {
}
