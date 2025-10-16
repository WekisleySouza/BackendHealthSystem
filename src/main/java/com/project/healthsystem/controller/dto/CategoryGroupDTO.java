package com.project.healthsystem.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryGroupDTO {
    private long id;
    @NotBlank(message = "O nome é obrigatório!")
    private String name;

}
