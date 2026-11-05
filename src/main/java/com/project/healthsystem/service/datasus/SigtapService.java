package com.project.healthsystem.service.datasus;

import com.project.healthsystem.model.datasus_data.sigtap.*;
import com.project.healthsystem.repository.datasus.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SigtapService {
  
  private final SigtapGroupRepository sigtapGroupRepository;
  private final SigtapSubGroupRepository sigtapSubGroupRepository;
  private final SigtapOrganizationRepository sigtapOrganizationRepository;
  private final SigtapProcedureRepository sigtapProcedureRepository;
  private final SigtapVersionRepository sigtapVersionRepository;
  
  private final SigtapFileProcessor sigtapFileProcessor;
  
  public void importSigtapIfNecessary() throws IOException {
    String currentCompetence = YearMonth.now()
      .format(DateTimeFormatter.ofPattern("yyyyMM"));
    
    String latestCompetence = this.findLatestCompetence();
    
    if(currentCompetence.equals(latestCompetence)) {
      return;
    }
    
    importSigtap();
  }
  
  @Transactional
  public void importSigtap() throws IOException {
    Path zip = sigtapFileProcessor.downloadLatest();
    
    Path directory = sigtapFileProcessor.extract(zip);
    
    // Delete old data(Don't invert to children after):
    this.sigtapProcedureRepository.deleteAllInBatch();
    this.sigtapOrganizationRepository.deleteAllInBatch();
    this.sigtapSubGroupRepository.deleteAllInBatch();
    this.sigtapGroupRepository.deleteAllInBatch();
    
    // Insert new data:
    this.updateGroups(directory.resolve("tb_grupo.txt"));
    this.updateSubGroups(directory.resolve("tb_sub_grupo.txt"));
    this.updateOrganizations(directory.resolve("tb_forma_organizacao.txt"));
    this.updateProcedures(directory.resolve("tb_procedimento.txt"));
    
    SigtapVersion sigtapVersion = SigtapVersion.build(zip);
    this.sigtapVersionRepository.save(sigtapVersion);
    
    this.sigtapFileProcessor.deleteZip(zip);
    this.sigtapFileProcessor.deleteDirectory(directory);
  }
  
  public void updateGroups(Path path) throws IOException {
    List<SigtapGroup> groups = SigtapFileReader.read(
      path,
      SigtapGroup::fromLine
    );
    
    this.sigtapGroupRepository.saveAll(groups);
  }
  
  public void updateSubGroups(Path path) throws IOException {
    Map<String, SigtapGroup> groups =
      sigtapGroupRepository.findAll()
        .stream()
        .collect(Collectors.toMap(
          SigtapGroup::getCode,
          Function.identity(),
          (existing, duplicate) -> existing
        ));
    
    List<SigtapSubGroup> subGroups = SigtapFileReader.read(
      path,
      SigtapSubGroup::fromLine
    );
    
    subGroups.forEach(subGroup -> {
      String code = subGroup.getGroup().getCode();
      
      subGroup.setGroup(
        groups.get(code)
      );
    });
    
    this.sigtapSubGroupRepository.saveAll(subGroups);
  }
  
  public void updateOrganizations(Path path) throws IOException {
    Map<String, SigtapSubGroup> subGroups =
      sigtapSubGroupRepository.findAll()
        .stream()
        .collect(Collectors.toMap(
          SigtapSubGroup::getCode,
          Function.identity(),
          (existing, duplicate) -> existing
        ));
    
    List<SigtapOrganization> organizations = SigtapFileReader.read(
      path,
      SigtapOrganization::fromLine
    );
    
    organizations.forEach(organization -> {
      System.out.println(organization.getCode());
      String code = organization.getSubGroup().getCode();
      
      organization.setSubGroup(
        subGroups.get(code)
      );
    });
    
    this.sigtapOrganizationRepository.saveAll(organizations);
  }
  
  public void updateProcedures(Path path) throws IOException {
    Map<String, SigtapOrganization> organizations =
      sigtapOrganizationRepository.findAll()
        .stream()
        .collect(Collectors.toMap(
          SigtapOrganization::getCode,
          Function.identity(),
          (existing, duplicate) -> existing
        ));
    
    List<SigtapProcedure> procedures = SigtapFileReader.read(
      path,
      SigtapProcedure::fromLine
    );
    
    procedures.forEach(procedure -> {
      String code = procedure.getOrganization().getCode();
      
      procedure.setOrganization(
        organizations.get(code)
      );
    });
    
    this.sigtapProcedureRepository.saveAll(procedures);
  }
  
  public String findLatestCompetence() {
    return sigtapVersionRepository
      .findFirstByOrderByCompetenceDesc()
      .map(SigtapVersion::getCompetence)
      .orElse(null);
  }
}
