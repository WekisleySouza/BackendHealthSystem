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
public class EmployeeRequestDTO {
    @NotBlank(message = "O nome é obrigatório!")
    @Size(max = 150, message = "O nome não pode ultrapassar 150 caracteres!")
    private String name;

    @NotBlank(message = "O sexo é obrigatório!")
    @Size(max = 20, message = "O sexo não pode ultrapassar 20 caracteres!")
    @Schema(name = "sexo")
    private String sex;
    @NotBlank(message = "O gênder é obrigatório!")
    @Size(max = 50, message = "O gênero não pode ultrapassar 50 caracteres!")
    @Schema(name = "gênder")
    private String gender;
    @Size(max = 20, message = "O telefone celular não pode ultrapassar 20 caracteres!")
    private String cellPhone;
    @Size(max = 20, message = "O telefone residencial não pode ultrapassar 20 caracteres!")
    private String residentialPhone;
    @Size(max = 20, message = "O telefone de contato não pode ultrapassar 20 caracteres!")
    private String contactPhone;

    @NotBlank(message = "O CPF é obrigatório!")
    @Size(max = 14, message = "O CPF deve ter no máximo 14 caracteres!")
    @CPF(message = "CPF inválido!")
    private String cpf;

    @NotNull(message = "A data de nascimento é obrigatória!")
    private LocalDate birthday;

    @Email(message = "Formato de e-mail inválido!")
    @Size(max = 320, message = "O e-mail não pode ultrapassar 320 caracteres!")
    private String email;

    @Size(max = 255, message = "O endereço não pode ultrapassar 255 caracteres!")
    private String address;
    private boolean active;

    @NotNull(message = "O papel do usuário deve ser informado!")
    @Size(min = 1, message = "Deve haver pelo menos um papel associado ao usuário!")
    private List<
            @Size(max = 50, message = "O papel não pode ultrapassar 50 caracteres!")
            String> roles;

    public String getCpfNormalized() {
        return cpf == null ? null : cpf.replaceAll("\\D", "");
    }
}
