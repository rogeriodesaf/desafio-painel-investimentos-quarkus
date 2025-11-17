package br.com.caixaverso.painel.repository;

import br.com.caixaverso.painel.model.Produto;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Repositório de Produto usando Panache.
 *
 * O desafio exige:
 *  - Consultar produtos no banco SQLite
 *  - Filtrar produto pelo "tipo" informado na solicitação de simulação
 *
 * Portanto, este repositório contém:
 *  - métodos padrão do Panache (listAll, findById, persist...)
 *  - um método findByTipo(), que será usado no ProdutoService
 */
@ApplicationScoped
public class ProdutoRepository implements PanacheRepository<Produto> {

    /**
     * Busca um produto pelo campo "tipo".
     *
     * Exemplo de tipos conforme o desafio:
     *  - "CDB"
     *  - "LCI"
     *  - "Fundo"
     *  - "Tesouro"
     *
     * @param tipo tipo do produto informado na requisição
     * @return Produto encontrado ou null caso não exista
     */
    public Produto findByTipo(String tipo) {
        return find("tipo", tipo).firstResult();
    }
}
