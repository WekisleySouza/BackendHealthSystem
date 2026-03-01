package com.project.healthsystem.controller.dto;

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
    private String name;

    @NotBlank(message = "O sexo é obrigatório!")
    @Schema(name = "sexo")
    private String sex;
    @NotBlank(message = "O gênder é obrigatório!")
    @Schema(name = "gênder")
    private String gender;
    private String cellPhone;
    private String residentialPhone;
    private String contactPhone;

    @NotBlank(message = "O CPF é obrigatório!")
    @CPF(message = "CPF inválido!")
    private String cpf;

    @NotNull(message = "A data de nascimento é obrigatória!")
    private LocalDate birthday;

    @Email(message = "Formato de e-mail inválido!")
    @Size(max = 320, message = "O e-mail não pode ultrapassar 320 caracteres!")
    private String email;

    private String address;

    @NotNull(message = "O papel do usuário deve ser informado!")
    private List<String> roles;

    public String getCpfNormalized() {
        return cpf == null ? null : cpf.replaceAll("\\D", "");
    }
}
