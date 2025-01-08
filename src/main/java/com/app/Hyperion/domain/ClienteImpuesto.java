package com.app.Hyperion.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "CLIENTE_IMPUESTO")
@Getter
@Setter
public class ClienteImpuesto {

    @EmbeddedId
    private ClienteImpuestoId id;

    @ManyToOne
    @MapsId("clienteId")
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne
    @MapsId("impuestoId")
    @JoinColumn(name = "impuesto_id")
    private Impuesto impuesto;

    public ClienteImpuesto() {
    }
}
