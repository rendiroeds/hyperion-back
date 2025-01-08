package com.app.Hyperion.domain;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ClienteImpuestoId implements Serializable {

    private Long clienteId;
    private Long impuestoId;

    // Constructores
    public ClienteImpuestoId() {}

    public ClienteImpuestoId(Long clienteId, Long impuestoId) {
        this.clienteId = clienteId;
        this.impuestoId = impuestoId;
    }

    // Getters y Setters
    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Long getImpuestoId() {
        return impuestoId;
    }

    public void setImpuestoId(Long impuestoId) {
        this.impuestoId = impuestoId;
    }

    // Métodos equals y hashCode (obligatorio para claves compuestas)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClienteImpuestoId that = (ClienteImpuestoId) o;
        return Objects.equals(clienteId, that.clienteId) &&
                Objects.equals(impuestoId, that.impuestoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clienteId, impuestoId);
    }
}

