package com.project.healthsystem.repository;

import com.project.healthsystem.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long>, JpaSpecificationExecutor<Person> {
    Optional<Person> findByCpf(String cpf);

    Optional<Person> getReferenceByCpf(String cpf);

    boolean existsByCpf(String cpf);
}
