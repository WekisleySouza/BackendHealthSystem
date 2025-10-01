package com.project.healthsystem.repository;

import com.project.healthsystem.model.Login;
import com.project.healthsystem.model.Person;
import com.project.healthsystem.model.Roles;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
public class LoginRepositoryTest {

    @Autowired
    PersonRepository personRepository;

    @Autowired
    LoginRepository loginRepository;

    @Test
    public void saveTest(){
//        Login login = new Login();
//        login.setRole(Roles.ADMIN);
//        login.setPassword("1234567");
//
//        Person person = personRepository
//                .findById(1)
//                .orElse(null);
//
//        login.setPerson(person);
//        loginRepository.save(login);
    }
}
