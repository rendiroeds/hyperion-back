package com.app.Hyperion.service;

import com.app.Hyperion.domain.Usuario;
import com.app.Hyperion.requests.LoginJsonRequest;
import com.app.Hyperion.requests.RegisterJsonRequest;
import com.app.Hyperion.responses.JwtResponse;

public interface UsuarioService {

    Usuario findByNombreUsuario(String username);

    JwtResponse login(LoginJsonRequest request);

    void register(RegisterJsonRequest request);
}
