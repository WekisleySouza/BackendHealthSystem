package com.project.healthsystem.service.datasus;

import com.project.healthsystem.controller.dto.basic_responses.SigtapImportResponseDTO;
import com.project.healthsystem.exceptions.InvalidDataException;
import com.project.healthsystem.model.datasus_data.sigtap.SigtapVersion;
import com.project.healthsystem.repository.datasus.SigtapVersionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
@Slf4j
public class SigtapService {
    private final SigtapVersionRepository versionRepository;
    private final SigtapFileProcessor fileProcessor;
    private final SigtapImportService importService;
    private final ReentrantLock updateLock = new ReentrantLock();

    public SigtapImportResponseDTO importSigtapIfNecessary() throws IOException {
        return update(false);
    }

    public SigtapImportResponseDTO importSigtap() throws IOException {
        return update(true);
    }

    private SigtapImportResponseDTO update(boolean force) throws IOException {
        if (!updateLock.tryLock()) {
            throw new InvalidDataException("Já existe uma atualização do SIGTAP em andamento.");
        }
        Path zip = null;
        Path directory = null;
        try {
            String fileName = fileProcessor.findLatestFileName();
            SigtapVersion available = SigtapVersion.build(Path.of(fileName));
            var current = versionRepository.findFirstByOrderByCompetenceDesc();
            if (current.isPresent() && current.get().getCompetence().compareTo(available.getCompetence()) > 0) {
                throw new InvalidDataException("O FTP oferece uma competência anterior à já importada.");
            }
            if (!force && current.isPresent() && fileName.equals(current.get().getFileName())) {
                return new SigtapImportResponseDTO(false, available.getCompetence(), fileName, 0, 0, 0, 0);
            }
            zip = fileProcessor.download(fileName);
            directory = fileProcessor.extract(zip);
            return importService.importData(directory, available, force);
        } finally {
            try {
                if (directory != null) fileProcessor.deleteDirectory(directory);
            } catch (IOException e) {
                log.warn("Não foi possível remover o diretório temporário do SIGTAP.", e);
            }
            try {
                if (zip != null) fileProcessor.deleteZip(zip);
            } catch (IOException e) {
                log.warn("Não foi possível remover o ZIP temporário do SIGTAP.", e);
            } finally {
                updateLock.unlock();
            }
        }
    }

    public String findLatestCompetence() {
        return versionRepository.findFirstByOrderByCompetenceDesc()
            .map(SigtapVersion::getCompetence).orElse(null);
    }
}
