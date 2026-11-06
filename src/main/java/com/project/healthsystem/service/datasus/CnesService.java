package com.project.healthsystem.service.datasus;

import com.project.healthsystem.controller.dto.basic_responses.CnesImportResponseDTO;
import com.project.healthsystem.exceptions.InvalidDataException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.*;
import java.util.concurrent.locks.ReentrantLock;
import static com.project.healthsystem.service.datasus.CnesCsvReader.Catalog.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CnesService {
    private final CnesCsvReader csvReader;
    private final CnesImportService importer;
    private final ReentrantLock establishmentsLock = new ReentrantLock();
    private final ReentrantLock professionalsLock = new ReentrantLock();

    public CnesImportResponseDTO importDataEstabilishments(MultipartFile file) {
        return importFile(file, ESTABLISHMENTS, establishmentsLock);
    }

    public CnesImportResponseDTO importDataProfessionals(MultipartFile file) {
        return importFile(file, PROFESSIONALS, professionalsLock);
    }

    private CnesImportResponseDTO importFile(MultipartFile file, CnesCsvReader.Catalog catalog,
            ReentrantLock lock) {
        if (file == null || file.isEmpty()) throw new InvalidDataException("Envie um arquivo CNES não vazio.");
        if (!lock.tryLock()) throw new InvalidDataException("Já existe uma importação de " + catalog + " em andamento.");
        Path copy = null;
        try {
            copy = Files.createTempFile("cnes-import-", ".csv");
            try (InputStream input = file.getInputStream()) {
                Files.copy(input, copy, StandardCopyOption.REPLACE_EXISTING);
            }
            long validated = csvReader.validate(copy, catalog);
            CnesImportResponseDTO result = importer.replace(copy, catalog, validated);
            log.info("CNES importado: catálogo={}, registros={}", result.catalog(), result.importedRecords());
            return result;
        } catch (IOException e) {
            throw new InvalidDataException("Não foi possível ler o arquivo enviado para importação CNES.");
        } finally {
            try {
                if (copy != null) Files.deleteIfExists(copy);
            } catch (IOException e) {
                log.warn("Não foi possível remover arquivo temporário da importação CNES.", e);
            } finally {
                lock.unlock();
            }
        }
    }
}
