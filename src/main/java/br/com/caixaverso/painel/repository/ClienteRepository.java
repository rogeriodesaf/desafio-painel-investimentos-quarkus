package br.com.caixaverso.painel.repository;

import br.com.caixaverso.painel.model.Cliente;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ClienteRepository implements PanacheRepository<Cliente> {

    // Buscar cliente por ID
    public Cliente findById(Long id) {
        return find("id", id).firstResult();
    }
}
