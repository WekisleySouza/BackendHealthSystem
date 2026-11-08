package com.project.healthsystem.controller.dto.basic_requests;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "SurgeryRequestDTO", description = "Dados enviados pelo frontend. SurgeryRequestDTO.")
public class SurgeryRequestDTO {

    @Schema(description = "ID do tipo de cirurgia cadastrado. Consulte GET /surgery-type.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private long surgeryTypeId;

    @Schema(description = "Data e hora do registro cirúrgico. Formato AAAA-MM-DDTHH:mm:ss, sem offset/fuso, por exemplo 2026-11-08T09:30:00.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, type = "string", example = "2026-11-08T09:30:00", nullable = true)
    private LocalDateTime dateTime;
    @Schema(description = "Nome da pessoa vinculada ao registro cirúrgico.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private String personName;
    @Schema(description = "Informação textual sobre o risco cirúrgico.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private String surgeryRisk;
    @Schema(description = "Local associado ao registro cirúrgico.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private String location;
    @Schema(description = "Conclusão registrada para a cirurgia.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private String conclusion;
    @Schema(description = "Referência textual ao SUS Fácil, conforme cadastro.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private String susEasy;
    @Schema(description = "Referência textual à SESAP, conforme cadastro.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private String sesap;
    @Schema(description = "Data de realização do procedimento. Formato AAAA-MM-DD, por exemplo 2026-11-08.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, type = "string", format = "date", example = "1990-05-20", nullable = true)
    private LocalDate procedureDate;
    @Schema(description = "Informação textual sobre risco anestésico.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private String anesthesicRisk;
    @Schema(description = "Observações do registro cirúrgico.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private String observation;

}
