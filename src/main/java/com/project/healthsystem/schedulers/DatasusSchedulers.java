package com.project.healthsystem.schedulers;

import com.project.healthsystem.service.datasus.SigtapService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatasusSchedulers {
  
  private final SigtapService sigtapService;
  
  // Every day at 03:00AM
  @Scheduled(cron = "0 0 3 * * *")
  public void scheduledSigtapUpdate() {
    try {
      sigtapService.importSigtapIfNecessary();
      log.info("Atualização do SIGTAP concluída.");
    } catch (Exception e) {
      log.error("Erro ao atualizar SIGTAP: ", e);
    }
  }
}
