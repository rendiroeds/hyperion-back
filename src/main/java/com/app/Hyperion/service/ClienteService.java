package com.app.Hyperion.service;

import com.app.Hyperion.domain.Cliente;
import com.app.Hyperion.domain.TipoContribuyente;
import com.app.Hyperion.requests.CreateClienteJsonRequest;

import java.util.List;

public interface ClienteService {

    Cliente createCliente(CreateClienteJsonRequest cliente);

    Cliente updateCliente(Long id, Cliente cliente);

    Cliente getClienteById(Long id);

    void deleteCliente(Long id);

    List<Cliente> getAllClientes();

    List<TipoContribuyente> getTipoContribuyentes();
}
