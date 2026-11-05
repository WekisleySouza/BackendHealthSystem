package com.project.healthsystem.service.datasus;

import com.project.healthsystem.model.datasus_data.cnes.CnesProfessional;
import com.project.healthsystem.model.datasus_data.cnes.Estabilishment;
import com.project.healthsystem.repository.datasus.CnesEstabilishmentsRepository;
import com.project.healthsystem.repository.datasus.CnesProfessionalsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CnesService {
  
  private final CnesEstabilishmentsRepository cnesEstabilishmentsRepository;
  private final CnesProfessionalsRepository cnesProfessionalsRepository;
  
  public void importDataEstabilishments(MultipartFile file) {
    final int BATCH_SIZE = 1000;
    List<Estabilishment> estabilishments = new ArrayList<>(BATCH_SIZE);
    int total = 0;
    
    try (BufferedReader reader = new BufferedReader(
      new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
      cnesEstabilishmentsRepository.deleteAll();
      reader.readLine();
      String line;
      
      while ((line = reader.readLine()) != null) {
        if (line.isBlank()) { continue; }
        
        Estabilishment estabilishment = Estabilishment.parseLine(line);
        
        estabilishments.add(estabilishment);
        total++;
        
        if (estabilishments.size() >= BATCH_SIZE) {
          cnesEstabilishmentsRepository.saveAll(estabilishments);
          estabilishments.clear();
          
          System.out.println("Estabelecimentos processados: " + total);
        }
      }
      
      // Salva o último lote
      if (!estabilishments.isEmpty()) {
        cnesEstabilishmentsRepository.saveAll(estabilishments);
      }
      
      System.out.println("Estabelecimentos encontrados: " + total);
      
    } catch (IOException e) {
      throw new RuntimeException("Erro ao ler arquivo de estabelecimentos", e);
    }
  }
  
  public void importDataProfessionals(MultipartFile file) {
    final int BATCH_SIZE = 1000;
    List<CnesProfessional> cnesProfessionals = new ArrayList<>(BATCH_SIZE);
    int total = 0;
    
    try (BufferedReader reader = new BufferedReader(
      new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)
    )) {
      
      cnesProfessionalsRepository.deleteAll();
      
      String line;
      reader.readLine();
      
      while ((line = reader.readLine()) != null) {
        if (line.isBlank()) { continue; }
        
        CnesProfessional professional = CnesProfessional.parseLine(line);
        cnesProfessionals.add(professional);
        
        total++;
        
        if (cnesProfessionals.size() >= BATCH_SIZE) {
          cnesProfessionalsRepository.saveAll(cnesProfessionals);
          cnesProfessionals.clear();
          
          System.out.println("Profissionais processados: " + total);
        }
      }
      
      // Salva o lote final, caso tenha menos que o tamanho do lote
      if (!cnesProfessionals.isEmpty()) {
        cnesProfessionalsRepository.saveAll(cnesProfessionals);
      }
      
      System.out.println("Profissionais encontrados: " + total);
      
    } catch (IOException e) {
      throw new RuntimeException("Error reading CNES professionals file", e);
    }
  }
}
