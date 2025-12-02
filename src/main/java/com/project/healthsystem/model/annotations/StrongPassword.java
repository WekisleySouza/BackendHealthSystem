package com.project.healthsystem.model.annotations;


import com.project.healthsystem.validator.StrongPasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StrongPasswordValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface StrongPassword {

    String message() default "Senha fraca: a senha deve conter letras, números, símbolos e ter no mínimo 6 caracteres.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
