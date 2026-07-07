package com.project.healthsystem.service;

import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.Person;
import com.project.healthsystem.repository.PersonRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;


import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @InjectMocks
    PersonService personService;
    @Mock
    PersonRepository personRepository;

    @Test
    void findById_shouldReturnPerson_whenPersonExists() {
        long id = 1L;
        Person person = new Person();
        person.setId(id);
        person.setName("Wekisley");

        when(personRepository.findById(id)).thenReturn(Optional.of(person));

        Person result = personService.findById(id);

        assertEquals(person, result);
        assertEquals(person.getName(), result.getName());
        verify(personRepository).findById(id);
    }

    @Test
    @DisplayName("Find by id when person doesn't exist")
    void findById_shouldThrowNotFoundException_whenPersonDoesNotExist() {
        long id = 1L;
        Person person = new Person();
        person.setId(id);
        person.setName("Wekisley");

        when(personRepository.findById(id)).thenReturn(Optional.empty());

//        catch(() -> personService.findById(id));
        assertThatThrownBy(() -> personService.findById(id))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Usuário não encontrado!");

        verify(personRepository).findById(id);
    }

}