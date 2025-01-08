package com.app.Hyperion.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "CLIENTE")
@Getter
@Setter
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CLIENTE_ID")
    private Long clienteId;

    @Column(name = "NOMBRE")
    private String nombre;

    @Column(name = "APELLIDO")
    private String apellido;

    @Column(name = "DIRECCION")
    private String direccion;

    @Column(name = "CUIT")
    private String cuit;

    @Column(name = "WHATSAPP")
    private String whatsapp;

    @Column(name = "EMAIL")
    private String email;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "TIPO_CONTRIBUYENTE_ID")
    private TipoContribuyente tipoContribuyente;

    @Column(name = "DEBE")
    private Boolean debe;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "PROVINCIA_ID")
    private Provincia provincia;

    @Column(name = "FECHA_ALTA")
    private Timestamp fechaAlta;

    @Column(name = "FECHA_BAJA")
    private Timestamp fechaBaja;

    @Column(name = "ESTADO")
    private Boolean estado;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Deuda> deudas;

    @ManyToMany
    @JoinTable(
            name = "CLIENTE_ACTIVIDAD",
            joinColumns = @JoinColumn(name = "CLIENTE_ID"),
            inverseJoinColumns = @JoinColumn(name = "ACTIVIDAD_ID")
    )
    private List<Actividad> actividades;

    public Cliente() {
    }
}
