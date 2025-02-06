package com.app.Hyperion.service;

import com.app.Hyperion.dao.ClienteRepository;
import com.app.Hyperion.dao.ProvinciaRepository;
import com.app.Hyperion.dao.TipoContribuyenteRepository;
import com.app.Hyperion.domain.Cliente;

import com.app.Hyperion.domain.Provincia;
import com.app.Hyperion.domain.TipoContribuyente;
import com.app.Hyperion.requests.CreateClienteJsonRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private TipoContribuyenteRepository tipoContribuyenteRepository;

    @Autowired
    private ProvinciaRepository provinciaRepository;

    @Override
    public Cliente createCliente(CreateClienteJsonRequest request) {
        Cliente cliente = new Cliente(request);
        TipoContribuyente tipoContribuyente = this.tipoContribuyenteRepository.findById(1l).get();
        Provincia provincia = this.provinciaRepository.findById(1l).get();
        cliente.setTipoContribuyente(tipoContribuyente);
        cliente.setProvincia(provincia);
        cliente.setDebe(false);
        cliente.setEstado(true);
        return clienteRepository.save(cliente);
    }

    @Override
    public Cliente updateCliente(Long id, Cliente cliente) {
        Optional<Cliente> optionalCliente = clienteRepository.findById(id);
        if (optionalCliente.isPresent()) {
            cliente.setClienteId(id);
            return clienteRepository.save(cliente);
        } else {
            throw new RuntimeException("Cliente no encontrado con ID: " + id);
        }
    }

    @Override
    public Cliente getClienteById(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + id));
    }

    @Override
    public void deleteCliente(Long id) {
        clienteRepository.deleteById(id);
    }

    @Override
    public List<Cliente> getAllClientes() {
        return (List<Cliente>) clienteRepository.findAll();
    }

    @Override
    public List<TipoContribuyente> getTipoContribuyentes() {
        return (List<TipoContribuyente>) this.tipoContribuyenteRepository.findAll();
    }
}
