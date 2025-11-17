package br.com.caixaverso.painel.service;

import br.com.caixaverso.painel.model.SimulacaoDia;
import br.com.caixaverso.painel.repository.SimulacaoDiaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service responsável por fornecer estatísticas diárias de simulação por produto.
 *
 * O desafio exige:
 *      GET /simulacoes/por-produto-dia
 *
 * Retornando:
 *  - produto
 *  - data
 *  - quantidadeSimulacoes
 *  - mediaValorFinal
 *
 * A tabela simulacao_dia é AGREGADA, não detalhada.
 * Cada linha representa um resumo das simulações de um determinado produto em um dia específico.
 */
@ApplicationScoped
public class SimulacaoDiaService {

    @Inject
    SimulacaoDiaRepository simulacaoDiaRepository;

    /**
     * Lista todas as estatísticas diárias registradas.
     *
     * Usado pelo endpoint GET /simulacoes/por-produto-dia
     */
    public List<SimulacaoDia> listarTudo() {
        return simulacaoDiaRepository.listAll();
    }

    /**
     * Registra estatísticas diárias de um produto (caso sua aplicação deseje registrar dinamicamente).
     *
     * NÃO é obrigatório no desafio, mas deixa o sistema mais completo.
     */
    public void registrar(String produto, int quantidade, double media) {

        if (produto == null || produto.isBlank()) {
            throw new IllegalArgumentException("produto não pode ser vazio.");
        }

        if (quantidade < 0) {
            throw new IllegalArgumentException("quantidadeSimulacoes deve ser >= 0");
        }

        if (media < 0) {
            throw new IllegalArgumentException("mediaValorFinal deve ser >= 0");
        }

        SimulacaoDia registro = new SimulacaoDia();

        registro.setProduto(produto);
        registro.setQuantidadeSimulacoes(quantidade);
        registro.setMediaValorFinal(media);

        String hoje = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        registro.setData(hoje);

        simulacaoDiaRepository.persist(registro);
    }
}
