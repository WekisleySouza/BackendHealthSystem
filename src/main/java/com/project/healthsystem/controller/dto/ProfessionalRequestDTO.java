package com.project.healthsystem.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfessionalRequestDTO {

    // Fields accordingly with SNES:
    // aDD CNS, CBO(OBG), VINCULAÇÃO, DESCRIÇÃO.
    private String cns;
    @NotBlank(message = "O cbo é obrigatório!")
    private String cbo;
    private String vinculation;
    private String description;

    // Existent Fields:
    @NotBlank(message = "O nome é obrigatório!")
    private String name;

    @NotBlank(message = "O sexo é obrigatório!")
    private String sex;
    @NotBlank(message = "O gênero é obrigatório!")
    private String gender;
    private String cellPhone;
    private String residentialPhone;
    private String contactPhone;

    private LocalDate birthday;

//    @Setter(AccessLevel.NONE)
//    @Pattern(
//        regexp = "(^$)|(^\\d{11}$)",
//        message = "CPF inválido!"
//    )
    private String cpf;

    private String address;
    private String phone;

    @Size(max = 320, message = "O e-mail não pode ultrapassar 320 caracteres!")
    @Email(message = "Formato de e-mail inválido!")
    private String email;

//    public void setCpf(String cpf) {
//        if (cpf == null || cpf.isBlank()) {
//            this.cpf = null;
//        } else {
//            this.cpf = cpf.replaceAll("\\D", "");
//        }
//    }

    public String getCpfNormalized() {
        return cpf == null ? null : cpf.replaceAll("\\D", "");
    }
}
