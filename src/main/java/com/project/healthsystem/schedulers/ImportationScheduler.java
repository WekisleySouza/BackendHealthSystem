package com.project.healthsystem.schedulers;

import com.project.healthsystem.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImportationScheduler {
    private final PatientService patientService;

    @Scheduled(
        cron = "0 0 0 * * *",
        zone = "America/Sao_Paulo"
    )
    public void runDailyImportation(){
        System.out.println("Iniciando importação de pacientes do PEC...");

        patientService.updatePatientsFromExternalDB();

        System.out.println("Backup finalizado!");
    }
}
