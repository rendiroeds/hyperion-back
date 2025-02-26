package com.app.Hyperion.responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UsuarioResponse {

    private Long id;

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private String rol;

}
