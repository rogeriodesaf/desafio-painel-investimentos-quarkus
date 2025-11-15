package br.com.caixaverso.painel.repository;

import br.com.caixaverso.painel.model.Produto;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProdutoRepository implements PanacheRepository<Produto> {

    // Buscar produtos por risco (Baixo / Médio / Alto)
    public java.util.List<Produto> findByRisco(String risco) {
        return list("risco", risco);
    }

    // Buscar produto por ID (opcional, mas útil para simulação)
    public Produto findById(Long id) {
        return find("id", id).firstResult();
    }
}
