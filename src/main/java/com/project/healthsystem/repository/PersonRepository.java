package com.project.healthsystem.repository;

import com.project.healthsystem.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Long> {

    Person findByCpf(String cpf);

    Person findByCns(String cns);

    List<Person> findByName(String name);

}
