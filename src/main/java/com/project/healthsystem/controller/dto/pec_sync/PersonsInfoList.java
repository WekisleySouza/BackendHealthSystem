package com.project.healthsystem.controller.dto.pec_sync;

import com.project.healthsystem.controller.dto.basic_requests.PersonBackupInfoDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PersonsInfoList {
    List<PersonBackupInfoDTO> personBackupInfoDTOS;
}
