package com.app.Hyperion.service;

import com.app.Hyperion.dao.UsuarioRepository;
import com.app.Hyperion.domain.Usuario;
import com.app.Hyperion.requests.LoginJsonRequest;
import com.app.Hyperion.requests.RegisterJsonRequest;
import com.app.Hyperion.responses.JwtResponse;
import com.app.Hyperion.responses.UsuarioResponse;
import com.app.Hyperion.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService{

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Usuario findByNombreUsuario(String username) {
        Optional<Usuario> usuario = usuarioRepository.findByUsername(username);
        return usuario.orElse(null);
    }

    @Override
    public JwtResponse login(LoginJsonRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        Usuario usuario = this.usuarioRepository.findByUsername(request.getUsername()).orElseThrow();
        String token = jwtService.getToken(usuario);
        return new JwtResponse(token);
    }

    @Override
    public void register(RegisterJsonRequest request) {
        Usuario user = Usuario.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getName())
                .lastName(request.getLastName())
                .rol("admin")
                .email(request.getEmail())
                .build();

        usuarioRepository.save(user);
    }

    @Override
    public List<UsuarioResponse> getUsuarios() {
        List<UsuarioResponse> usuarioResponses = new ArrayList<>();
        for (Usuario usuario : this.usuarioRepository.findAll()) {
            UsuarioResponse usuarioResponse = new UsuarioResponse();
            usuarioResponse.setId(usuario.getUsuarioId());
            usuarioResponse.setFirstName(usuario.getFirstName());
            usuarioResponse.setLastName(usuario.getLastName());
            usuarioResponse.setEmail(usuario.getEmail());
            usuarioResponse.setRol(usuario.getRol());
            usuarioResponses.add(usuarioResponse);
            usuarioResponse.setUsername(usuario.getUsername());
        }
        return usuarioResponses;
    }

    @Override
    public Usuario saveUsuario(UsuarioResponse usuarioResponse) {
        if(usuarioResponse.getId() == null) {
            Usuario usuario = new Usuario();
            usuario.setFirstName(usuarioResponse.getFirstName());
            usuario.setLastName(usuarioResponse.getLastName());
            usuario.setUsername(usuarioResponse.getUsername());
            usuario.setEmail(usuarioResponse.getEmail());
            usuario.setRol(usuarioResponse.getRol());
            return this.usuarioRepository.save(usuario);
        } else {
            Usuario usuario = this.usuarioRepository.findById(usuarioResponse.getId()).orElseThrow();
            usuario.setFirstName(usuarioResponse.getFirstName());
            usuario.setLastName(usuarioResponse.getLastName());
            usuario.setEmail(usuarioResponse.getEmail());
            return this.usuarioRepository.save(usuario);
        }

    }
}
