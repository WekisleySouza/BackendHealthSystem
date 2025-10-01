package com.project.healthsystem.controller.dto;

import com.project.healthsystem.model.Professional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    private String name;
    private String birthday;
    private String cpf;
    private String phone;
    private String email;

    public ProfessionalDTO(String name, LocalDate birthday, String email, String cpf){
        this.name = name;
        this.birthday = birthday.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        this.cpf = cpf;
        this.email = email;
    }

    public ProfessionalDTO(Professional professional){
        this.id = professional.getId();
        this.name = professional.getName();
        this.birthday = professional.getBirthday().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        this.cpf = professional.getCpf();
        this.phone = professional.getPhone();
        this.email = professional.getEmail();
    }

    public Professional mappingToProfessional(){
        Professional professional = new Professional();

        professional.setName(this.name);
        professional.setBirthday(LocalDate.parse(this.birthday, DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        professional.setCpf(this.cpf);
        professional.setPhone(this.phone);
        professional.setEmail(this.email);

        return professional;
    }
}
