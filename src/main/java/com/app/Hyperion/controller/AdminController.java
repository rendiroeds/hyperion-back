package com.app.Hyperion.controller;

import com.app.Hyperion.domain.Cliente;
import com.app.Hyperion.domain.TipoContribuyente;
import com.app.Hyperion.domain.Usuario;
import com.app.Hyperion.requests.CreateClienteJsonRequest;
import com.app.Hyperion.responses.UsuarioResponse;
import com.app.Hyperion.service.ClienteService;
import com.app.Hyperion.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private UsuarioService usuarioService;

    // Crear cliente
    @PostMapping("/clientes")
    public ResponseEntity<Cliente> createCliente(@RequestBody CreateClienteJsonRequest cliente) {
        Cliente createdCliente = clienteService.createCliente(cliente);
        return ResponseEntity.ok(createdCliente);
    }

    // Modificar cliente
    @PutMapping("/clientes/{id}")
    public ResponseEntity<Cliente> updateCliente(@PathVariable Long id, @RequestBody Cliente cliente) {
        Cliente updatedCliente = clienteService.updateCliente(id, cliente);
        return ResponseEntity.ok(updatedCliente);
    }

    // Obtener un cliente por ID
    @GetMapping("/clientes/{id}")
    public ResponseEntity<Cliente> getClienteById(@PathVariable Long id) {
        Cliente cliente = clienteService.getClienteById(id);
        return ResponseEntity.ok(cliente);
    }

    // Eliminar un cliente
    @DeleteMapping("/clientes/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id) {
        clienteService.deleteCliente(id);
        return ResponseEntity.noContent().build();
    }

    // Obtener todos los clientes
    @GetMapping("/clientes")
    public ResponseEntity<List<Cliente>> getAllClientes() {
        List<Cliente> clientes = clienteService.getAllClientes();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/contribuyente")
    public ResponseEntity<List<TipoContribuyente>> getTipoContribuyentes() {
        List<TipoContribuyente> contribuyentes = clienteService.getTipoContribuyentes();
        return ResponseEntity.ok(contribuyentes);
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioResponse>> getUsuarios() {
        List<UsuarioResponse> usuarios = usuarioService.getUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @PostMapping("/usuario")
    public ResponseEntity createUsuario(@RequestBody UsuarioResponse usuario) {
        Usuario usuario1 = this.usuarioService.saveUsuario(usuario);
        return ResponseEntity.ok(usuario1);
    }

    @PutMapping("/usuario")
    public ResponseEntity<Usuario> updateUsuario(@RequestBody UsuarioResponse usuario) {
        Usuario usuario1 = this.usuarioService.saveUsuario(usuario);
        return ResponseEntity.ok(usuario1);
    }
}
