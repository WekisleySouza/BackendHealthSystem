package com.project.healthsystem.model.datasus_data.cnes;

import com.project.healthsystem.model.abstractions.IDAbstraction;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="tb_cnes_estabilishments")
@Data
@NoArgsConstructor
public class Estabilishment extends IDAbstraction {
  
  private String unitCode;
  private String cnesCode;
  private String cnpjMaintainer;
  private String legalName;
  private String tradeName;
  private String cep;
  private String phone;
  private String email;
  private String cpf;
  private String cnpj;
  private String address;
  
  public static Estabilishment parseLine(String line) {
    
    String[] columns = line.split(";", -1);
    
    Estabilishment estabilishment = new Estabilishment();
    
    estabilishment.setUnitCode(columns[0]);
    estabilishment.setCnesCode(columns[1]);
    estabilishment.setCnpjMaintainer(columns[2]);
    estabilishment.setLegalName(columns[3]);
    estabilishment.setTradeName(columns[4]);
    estabilishment.setCep(columns[5]);
    estabilishment.setPhone(columns[6]);
    estabilishment.setEmail(columns[7]);
    estabilishment.setCpf(columns[8]);
    estabilishment.setCnpj(columns[9]);
    estabilishment.setAddress(columns[10]);
    
    return estabilishment;
  }
  
  @Override
  public String toString() {
    return "Estabilishment {\n" +
      "  id=" + getId() + ",\n" +
      "  unitCode='" + unitCode + "',\n" +
      "  cnesCode='" + cnesCode + "',\n" +
      "  cnpjMaintainer='" + cnpjMaintainer + "',\n" +
      "  legalName='" + legalName + "',\n" +
      "  tradeName='" + tradeName + "',\n" +
      "  cep='" + cep + "',\n" +
      "  phone='" + phone + "',\n" +
      "  email='" + email + "',\n" +
      "  cpf='" + cpf + "',\n" +
      "  cnpj='" + cnpj + "',\n" +
      "  address='" + address + "'\n" +
      '}';
  }
}