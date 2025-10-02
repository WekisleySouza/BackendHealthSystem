package com.project.healthsystem.model;


import com.project.healthsystem.controller.dto.ProfessionalDTO;
import com.project.healthsystem.model.abstractions.UserBasicAbstraction;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Entity
@Table(name="tb_professional")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Professional extends UserBasicAbstraction {

    @OneToMany(mappedBy = "professional")
    List<Appointment> appointment;

    public void coppingFromProfessionalDTO(ProfessionalDTO professionalDto){
        this.cpf = professionalDto.getCpf();
        this.birthday = LocalDate.parse(
                professionalDto.getBirthday(),
                DateTimeFormatter.ofPattern("dd/MM/yyyy")
        );
        this.name = professionalDto.getName();
        this.email = professionalDto.getEmail();
        this.phone = professionalDto.getPhone();
    }
}
