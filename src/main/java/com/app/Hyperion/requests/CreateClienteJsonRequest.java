package com.app.Hyperion.requests;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateClienteJsonRequest {

    private String nombre;

    private String apellido;

    private String direccion;

    private String cuit;

    private String whatsapp;

    private String email;

    private Long tipoContribuyente;

    private Long provincia;
}
