package br.com.caixaverso.painel.service;

import br.com.caixaverso.painel.model.Produto;
import br.com.caixaverso.painel.repository.ProdutoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

/**
 * ============================================================================
 *  SERVICE: ProdutoService
 * ============================================================================
 *
 * Responsabilidades conforme o DESAFIO:
 *
 *  ✔ Consultar produtos armazenados no banco SQLite
 *  ✔ Validar dados antes de retorná-los
 *  ✔ Fornecer método de busca por TIPO (CDB, LCI, Fundo, Tesouro...)
 *        → ESSENCIAL para a simulação de investimentos
 *
 *  O desafio exige que o endpoint POST /simular-investimento receba:
 *
 *      {
 *          "clienteId": 123,
 *          "valor": 10000,
 *          "prazoMeses": 12,
 *          "tipoProduto": "CDB"
 *      }
 *
 *  Portanto, a API precisa identificar o produto **pelo tipo**, e não pelo ID.
 *
 *  Este service existe para encapsular as regras de leitura e validação
 *  de produtos antes de sua utilização em simulações, recomendações
 *  e consultas gerais.
 *
 * ============================================================================
 */
@ApplicationScoped
public class ProdutoService {

    @Inject
    ProdutoRepository repository;

    /**
     * ------------------------------------------------------------------------
     *  Lista todos os produtos cadastrados no banco.
     *
     *  Usado em endpoints auxiliares (não obrigatórios no desafio), mas útil
     *  para debugging, documentação e inspeção dos dados parametrizados.
     * ------------------------------------------------------------------------
     */
    public List<Produto> listarTodos() {
        return repository.listAll();
    }

    /**
     * ------------------------------------------------------------------------
     *  Busca produto pelo ID.
     *
     *  Embora o desafio NÃO peça simulação baseada em ID,
     *  deixamos esse método como utilitário para endpoints secundários.
     *
     *  Valida:
     *      - ID não nulo
     *      - Produto existente
     * ------------------------------------------------------------------------
     */
    public Produto buscarPorId(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo.");
        }

        Produto produto = repository.findById(id);

        if (produto == null) {
            throw new IllegalArgumentException("Produto não encontrado para o ID informado.");
        }

        return produto;
    }

    /**
     * ------------------------------------------------------------------------
     *  Busca produto pelo TIPO EXATAMENTE como exige o PDF do DESAFIO.
     *
     *  O JSON da simulação envia:
     *      "tipoProduto": "CDB"
     *
     *  Portanto, este é o método OFICIAL que será usado no SimulacaoService.
     *
     *  Valida:
     *      - tipo não pode ser vazio
     *      - produto deve existir no banco
     *
     *  Exemplo de tipos válidos:
     *      "CDB", "LCI", "LCA", "Tesouro", "Fundo"
     * ------------------------------------------------------------------------
     */
    public Produto buscarPorTipo(String tipo) {

        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("tipoProduto não pode ser vazio.");
        }

        Produto produto = repository.findByTipo(tipo);

        if (produto == null) {
            throw new IllegalArgumentException(
                    "Nenhum produto encontrado para o tipo informado: " + tipo
            );
        }

        return produto;
    }
}
