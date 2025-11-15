package br.com.caixaverso.painel.repository;

import br.com.caixaverso.painel.model.SimulacaoDia;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class SimulacaoDiaRepository implements PanacheRepository<SimulacaoDia> {

    // Buscar todos os registros de um dia específico
    public List<SimulacaoDia> findByData(String data) {
        return list("data", data);
    }

    // Buscar todas as estatísticas de um produto específico
    public List<SimulacaoDia> findByProduto(String produto) {
        return list("produto", produto);
    }
}
