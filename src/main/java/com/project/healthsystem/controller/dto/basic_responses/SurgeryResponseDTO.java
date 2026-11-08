package com.project.healthsystem.controller.dto.basic_responses;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Schema(name = "SurgeryResponseDTO", description = "Dados retornados pela API. SurgeryResponseDTO.")
public class SurgeryResponseDTO {

    @Schema(description = "Identificador interno do registro no Vitalya; não é o código do catálogo externo.")
    private long id;
    @Schema(description = "ID do tipo de cirurgia cadastrado. Consulte GET /surgery-type.")
    private long surgeryTypeId;

    @Schema(description = "Data e hora do registro cirúrgico. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00.", type = "string", example = "2026-11-08T09:30:00")
    private LocalDateTime dateTime;
    @Schema(description = "Nome da pessoa vinculada ao registro cirúrgico.")
    private String personName;
    @Schema(description = "Informação textual sobre o risco cirúrgico.")
    private String surgeryRisk;
    @Schema(description = "Local associado ao registro cirúrgico.")
    private String location;
    @Schema(description = "Conclusão registrada para a cirurgia.")
    private String conclusion;
    @Schema(description = "Referência textual ao SUS Fácil, conforme cadastro.")
    private String susEasy;
    @Schema(description = "Referência textual à SESAP, conforme cadastro.")
    private String sesap;
    @Schema(description = "Data de realização do procedimento. Formato AAAA-MM-DD, por exemplo 2026-11-08.", type = "string", format = "date", example = "1990-05-20")
    private LocalDate procedureDate;
    @Schema(description = "Informação textual sobre risco anestésico.")
    private String anesthesicRisk;
    @Schema(description = "Observações do registro cirúrgico.")
    private String observation;
}
