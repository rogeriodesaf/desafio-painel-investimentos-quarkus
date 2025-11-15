package br.com.caixaverso.painel.repository;

import br.com.caixaverso.painel.model.Simulacao;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class SimulacaoRepository implements PanacheRepository<Simulacao> {

    // Buscar simulações por cliente (opcional, útil)
    public List<Simulacao> findByClienteId(Long clienteId) {
        return list("clienteId", clienteId);
    }

    // Buscar simulações por produto
    public List<Simulacao> findByProduto(String produto) {
        return list("produto", produto);
    }

    // Buscar simulações por data (útil para estatísticas)
    public List<Simulacao> findByData(String data) {
        return list("dataSimulacao", data);
    }
}
