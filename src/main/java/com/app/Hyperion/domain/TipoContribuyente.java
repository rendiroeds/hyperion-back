package com.app.Hyperion.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TIPO_CONTRIBUYENTE")
@Getter
@Setter
public class TipoContribuyente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TIPO_CONTRIBUYENTE_ID")
    private Long tipoContribuyenteId;

    @Column(name = "NOMBRE")
    private String nombre;

    @Column(name = "DESCRIPCION")
    private String descripcion;

    public TipoContribuyente() {
    }
}
