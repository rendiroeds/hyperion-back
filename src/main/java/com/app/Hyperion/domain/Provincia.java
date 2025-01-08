package com.app.Hyperion.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "PROVINCIA")
@Getter
@Setter
public class Provincia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROVINCIA_ID")
    private Long provinciaId;

    @Column(name = "NOMBRE")
    private String nombre;

    public Provincia() {
    }
}
