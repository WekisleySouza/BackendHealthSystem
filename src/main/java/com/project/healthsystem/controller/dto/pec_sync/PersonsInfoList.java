package com.project.healthsystem.controller.dto.pec_sync;

import io.swagger.v3.oas.annotations.media.Schema;

import com.project.healthsystem.controller.dto.basic_requests.PersonBackupInfoDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Schema(name = "PersonsInfoList", description = "Dados retornados pela API. PersonsInfoList.")
public class PersonsInfoList {
    @Schema(description = "Lista de dados de pacientes vindos do PEC.")
    List<PersonBackupInfoDTO> personBackupInfoDTOS;
}
