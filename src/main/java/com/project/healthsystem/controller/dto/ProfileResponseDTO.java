package com.project.healthsystem.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.time.LocalDate;

@Data

@Schema(name = "ProfileResponseDTO", description = "Dados retornados pela API. ProfileResponseDTO.")
public class ProfileResponseDTO {

    // Common atributes
    
    @Schema(description = "Nome de exibição do registro.")
    private String name;
    
    @Schema(description = "CPF como texto; preserve zeros à esquerda. No cadastro é validado e normalizado pelo backend.")
    private String cpf;
    
    @Schema(description = "Endereço completo informado no cadastro.")
    private String address;
    
    @Schema(description = "Data de nascimento. Formato AAAA-MM-DD, por exemplo 2026-11-08.", type = "string", format = "date", example = "1990-05-20")
    private LocalDate birthday;
    
    @Schema(description = "Endereço de e-mail de contato.", example = "usuario@example.com", format = "email")
    private String email;
    
    @Schema(description = "Sexo informado no cadastro ou na fonte de origem. Campo textual; não é um enum do Vitalya.")
    private String sex;
    
    @Schema(description = "Telefone celular de contato.")
    private String cellPhone;
    
    @Schema(description = "Telefone residencial.")
    private String residentialPhone;
    
    @Schema(description = "Telefone de contato alternativo.")
    private String contactPhone;

    // Patient atributes
    
    @Schema(description = "Número do Cartão Nacional de Saúde como texto; preserve zeros à esquerda.", example = "000000000000001")
    private String cns;
    
    @Schema(description = "Nome da mãe do paciente.")
    private String motherName;
    
    @Schema(description = "Nome da equipe de saúde.")
    private String teamName;
    
    @Schema(description = "Identificador Nacional de Equipe (INE), mantido como texto.")
    private String teamINE;
    
    @Schema(description = "Identificação da microárea de acompanhamento.")
    private String microArea;
    
    @Schema(description = "Origem do cadastro ou encaminhamento.")
    private String origin;
}
