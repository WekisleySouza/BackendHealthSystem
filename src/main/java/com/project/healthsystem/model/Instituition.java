package com.project.healthsystem.model;

import com.project.healthsystem.model.abstractions.IDAbstraction;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "instituition")
@Data
public class Instituition extends IDAbstraction {
    @Column(name = "name")
    private String name;
    @Column(name = "cep")
    private String cep;
    @Column(name = "city_name")
    private String cityName;
    @Column(name = "address")
    private String address;
    @Column(name = "phone")
    private String phone;
    @Column(name = "link_logo")
    private String linkLogo;
}
