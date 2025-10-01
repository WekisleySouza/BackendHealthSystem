package com.project.healthsystem.repository;

import com.project.healthsystem.model.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.UUID;

@SpringBootTest
public class PersonRepositoryTest {

    @Autowired
    PersonRepository personRepository;

    @Test
    public void saveTest() {
        Person person = new Person();
        person.setName("Wekisley");
        person.setCns("12232414132");
        person.setCpf("21333333333");
        person.setBirthday(LocalDate.of(1999, 8, 1));

        var savedPerson = personRepository.save(person);
        System.out.println("Pessoa salva: " + savedPerson);
    }

    @Test
    public void updateTest(){
//        Person person = personRepository
//                .findById(UUID.fromString("6f27d5c1-ebea-487b-b0d9-23bfe3913f2d"))
//                .orElse(null);
//
//        if(person != null){
//            person.setName("Aline");
//            personRepository.save(person);
//        }
    }

    @Test
    public void deleteTest(){

    }

    @Test
    public void readTest(){

    }

    @Test
    public void findByCpfTest(){
        Person person = personRepository.findByCpf("21333333333");
        System.out.println(person);
    }
}
