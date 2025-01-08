package com.app.Hyperion.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.sql.Timestamp;

@Entity
@Table(name = "DEUDA")
@Getter
@Setter
public class Deuda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DEUDA_ID")
    private Long deudaId;

    @Column(name = "MONTO")
    private BigInteger monto;

    @ManyToOne
    @JoinColumn(name = "CLIENTE_ID", referencedColumnName = "CLIENTE_ID")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "IMPUESTO_ID", referencedColumnName = "IMPUESTO_ID")
    private Impuesto impuesto;

    @Column(name = "FECHA_VENCIMIENTO")
    private Timestamp fechaVencimiento;

    @Column(name = "fechaPeriodo")
    private Timestamp fechaPeriodo;

    @Column(name = "PAGADA")
    private Boolean pagada;

    public Deuda() {
    }
}
