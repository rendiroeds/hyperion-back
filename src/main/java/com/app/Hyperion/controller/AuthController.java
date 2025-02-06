package com.app.Hyperion.controller;

import com.app.Hyperion.requests.LoginJsonRequest;
import com.app.Hyperion.requests.RegisterJsonRequest;
import com.app.Hyperion.service.UsuarioService;
import com.app.Hyperion.utils.JwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UsuarioService userService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginJsonRequest loginRequest) {
        return ResponseEntity.ok(userService.login(loginRequest));
    }

    @PostMapping(value = "register")
    public ResponseEntity<?> register(@RequestBody RegisterJsonRequest request)
    {
        this.userService.register(request);
        return ResponseEntity.ok("registrado");
    }
}