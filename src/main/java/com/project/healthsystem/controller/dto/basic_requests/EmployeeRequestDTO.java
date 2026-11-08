package com.project.healthsystem.controller.dto.basic_requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Schema(name = "EmployeeRequestDTO", description = "Dados enviados pelo frontend. EmployeeRequestDTO.")
public class EmployeeRequestDTO {
    @NotBlank(message = "O nome é obrigatório!")
    @Size(max = 150, message = "O nome não pode ultrapassar 150 caracteres!")
    
    @Schema(description = "Nome de exibição do registro.", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 150)
    private String name;

    @NotBlank(message = "O sexo é obrigatório!")
    @Size(max = 20, message = "O sexo não pode ultrapassar 20 caracteres!")

    @Schema(description = "Sexo informado no cadastro ou na fonte de origem. Campo textual; não é um enum do Vitalya.", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 20)
    private String sex;
    @NotBlank(message = "O gênder é obrigatório!")
    @Size(max = 50, message = "O gênero não pode ultrapassar 50 caracteres!")

    @Schema(description = "Identidade de gênero informada no cadastro. Campo textual; não é um enum do Vitalya.", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 50)
    private String gender;
    @Size(max = 20, message = "O telefone celular não pode ultrapassar 20 caracteres!")
    
    @Schema(description = "Telefone celular de contato.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, maxLength = 20, nullable = true)
    private String cellPhone;
    @Size(max = 20, message = "O telefone residencial não pode ultrapassar 20 caracteres!")
    
    @Schema(description = "Telefone residencial.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, maxLength = 20, nullable = true)
    private String residentialPhone;
    @Size(max = 20, message = "O telefone de contato não pode ultrapassar 20 caracteres!")
    
    @Schema(description = "Telefone de contato alternativo.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, maxLength = 20, nullable = true)
    private String contactPhone;

    @NotBlank(message = "O CPF é obrigatório!")
    @Size(max = 14, message = "O CPF deve ter no máximo 14 caracteres!")
    @CPF(message = "CPF inválido!")
    
    @Schema(description = "CPF como texto; preserve zeros à esquerda. No cadastro é validado e normalizado pelo backend.", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 14)
    private String cpf;

    @NotNull(message = "A data de nascimento é obrigatória!")
    
    @Schema(description = "Data de nascimento. Formato AAAA-MM-DD, por exemplo 2026-11-08.", requiredMode = Schema.RequiredMode.REQUIRED, type = "string", format = "date", example = "1990-05-20")
    private LocalDate birthday;

    @NotBlank(message = "O e-mail é obrigatório!")
    @Email(message = "Formato de e-mail inválido!")
    @Size(max = 320, message = "O e-mail não pode ultrapassar 320 caracteres!")
    
    @Schema(description = "Endereço de e-mail de contato.", requiredMode = Schema.RequiredMode.REQUIRED, example = "usuario@example.com", format = "email", maxLength = 320)
    private String email;

    @Size(max = 255, message = "O endereço não pode ultrapassar 255 caracteres!")
    
    @Schema(description = "Endereço completo informado no cadastro.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, maxLength = 255, nullable = true)
    private String address;
    
    @Schema(description = "Indica se o funcionário está ativo.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "false")
    private boolean active;

    @NotNull(message = "O papel do usuário deve ser informado!")
    @Size(min = 1, message = "Deve haver pelo menos um papel associado ao usuário!")
    @Schema(description = "Papéis de acesso atribuídos ao usuário. Possibilidades: API, ADMIN, MANAGER, EMPLOYEE, PATIENT.", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<
            @Size(max = 50, message = "O papel não pode ultrapassar 50 caracteres!")
    
    
            String> roles;

    @Schema(description = "CPF normalizado calculado pelo backend; não envie este campo.", accessMode = Schema.AccessMode.READ_ONLY)
    public String getCpfNormalized() {
        return cpf == null ? null : cpf.replaceAll("\\D", "");
    }
}
