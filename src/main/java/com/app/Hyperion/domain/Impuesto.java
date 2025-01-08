package com.app.Hyperion.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "IMPUESTO")
@Getter
@Setter
public class Impuesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IMPUESTO_ID")
    private Long impuestoId;

    @Column(name = "NOMBRE")
    private String nombre;

    @Column(name = "DESCRIPCION")
    private String descripcion;

    public Impuesto() {
    }
}
