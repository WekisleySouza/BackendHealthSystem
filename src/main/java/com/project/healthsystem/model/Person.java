package com.project.healthsystem.model;

import com.project.healthsystem.model.abstractions.BasicEntityAbstraction;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tb_person")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Person extends BasicEntityAbstraction {

    @Column(name = "name", length = 100, nullable = false)
    protected String name;
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 100)
    protected Gender gender;
    @Enumerated(EnumType.STRING)
    @Column(name = "sex", length = 100, nullable = false)
    protected Sex sex;
    @Column(name = "cpf", unique = true, length = 15)
    protected String cpf;
    @Column(name = "cell_phone")
    protected String cellPhone;
    @Column(name = "residential_phone")
    protected String residentialPhone;
    @Column(name = "contact_phone")
    protected String contactPhone;
    @Column(name = "address")
    protected String address;
    @Column(name="birthday", nullable = false)
    protected LocalDate birthday;
    @Column(name="email", length=320)
    protected String email;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "person_roles",
        joinColumns = @JoinColumn(name = "person_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    public Person addRole(Role role){
        roles.add(role);
        return this;
    }

    public Person removeRole(Roles roleLabel){
        this.roles.removeIf(role ->
            role.getRole() == roleLabel
        );
        return this;
    }
}
