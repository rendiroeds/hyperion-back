package com.app.Hyperion.controller;

import com.app.Hyperion.domain.Cliente;
import com.app.Hyperion.domain.TipoContribuyente;
import com.app.Hyperion.requests.CreateClienteJsonRequest;
import com.app.Hyperion.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ClienteService clienteService;

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
}
