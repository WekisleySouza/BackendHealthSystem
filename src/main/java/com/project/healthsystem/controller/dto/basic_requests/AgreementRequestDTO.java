package com.project.healthsystem.controller.dto.basic_requests;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Schema(name="Agreement")
public class AgreementRequestDTO {

    @NotBlank(message = "O nome é obrigatório!")
    @Size(max = 150, message = "O nome não pode ultrapassar 150 caracteres!")
    @Schema(name = "name")
    private String name;
}
