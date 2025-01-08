package com.app.Hyperion.dao;

import com.app.Hyperion.domain.Cliente;
import org.springframework.data.repository.CrudRepository;

public interface ClienteRepository extends CrudRepository<Cliente, Long> {
}
