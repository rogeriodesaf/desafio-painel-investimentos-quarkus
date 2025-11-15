package br.com.caixaverso.painel.repository;

import br.com.caixaverso.painel.model.Investimento;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class InvestimentoRepository implements PanacheRepository<Investimento> {

    // Buscar todos os investimentos de um cliente específico
    public List<Investimento> findByClienteId(Long clienteId) {
        return list("clienteId", clienteId);
    }
}

