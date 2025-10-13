package com.project.healthsystem.controller.dto;

import com.project.healthsystem.model.Professional;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfessionalDTO {

    private long id;

    @NotBlank(message = "O nome é obrigatório!")
    private String name;

    @NotBlank(message = "A data de nascimento é obrigatória!")
    private LocalDate birthday;

    @NotBlank(message = "O CPF é obrigatório!")
    @CPF(message = "CPF inválido!")
    private String cpf;

    private String phone;

    @Size(max = 320, message = "O e-mail não pode ultrapassar 320 caracteres!")
    @Email(message = "Formato de e-mail inválido!")
    private String email;

    public ProfessionalDTO(Professional professional){
        this.id = professional.getId();
        this.name = professional.getName();
        this.birthday = professional.getBirthday();
        this.cpf = professional.getCpf();
        this.phone = professional.getPhone();
        this.email = professional.getEmail();
    }

    public Professional mappingToProfessional(){
        Professional professional = new Professional();

        professional.setName(this.name);
        professional.setBirthday(this.birthday);
        professional.setCpf(this.cpf);
        professional.setPhone(this.phone);
        professional.setEmail(this.email);

        return professional;
    }
}
