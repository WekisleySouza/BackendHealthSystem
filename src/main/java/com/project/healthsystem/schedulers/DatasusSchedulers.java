package com.project.healthsystem.schedulers;

import com.project.healthsystem.service.datasus.SigtapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatasusSchedulers {
    private final SigtapService sigtapService;

    @Scheduled(cron = "0 0 3 * * *", zone = "America/Sao_Paulo")
    public void scheduledSigtapUpdate() {
        try {
            var result = sigtapService.importSigtapIfNecessary();
            if (result.imported()) {
                log.info("SIGTAP importado: arquivo={}, competência={}, procedimentos={}",
                    result.fileName(), result.competence(), result.procedures());
            } else {
                log.info("SIGTAP já atualizado: arquivo={}, competência={}", result.fileName(), result.competence());
            }
        } catch (Exception e) {
            log.error("Erro ao atualizar SIGTAP.", e);
        }
    }
}
