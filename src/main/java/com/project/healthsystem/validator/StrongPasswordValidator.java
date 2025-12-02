package com.project.healthsystem.validator;

import com.project.healthsystem.model.annotations.StrongPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.passay.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context){

        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("passay_messages.properties")) {
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível carregar passay_messages.properties", e);
        }
        MessageResolver resolver = new PropertiesMessageResolver(props);


        PasswordValidator validator = new PasswordValidator(
            resolver,
            Arrays.asList(
                new LengthRule(6, 128),
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new CharacterRule(EnglishCharacterData.Special, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new WhitespaceRule()
        ));
        RuleResult result = validator.validate(new PasswordData(password));

        if(result.isValid()){ return true; }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                String.join(", ", validator.getMessages(result))
        ).addConstraintViolation();

        return false;
    }
}
