package br.com.caixaverso.painel.service;

import br.com.caixaverso.painel.model.Cliente;
import br.com.caixaverso.painel.model.Produto;
import br.com.caixaverso.painel.model.Simulacao;
import br.com.caixaverso.painel.repository.ClienteRepository;
import br.com.caixaverso.painel.repository.SimulacaoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@ApplicationScoped
public class SimulacaoService {

    @Inject
    SimulacaoRepository simulacaoRepository;

    @Inject
    ProdutoService produtoService;

    @Inject
    ClienteRepository clienteRepository;

    /**
     * Regra principal do desafio:
     * - valida dados de entrada
     * - garante existência de cliente e produto
     * - calcula valor final com base na rentabilidade do produto e prazo
     * - persiste a simulação e retorna o registro
     */
    public Simulacao simular(Long clienteId,
                             Long produtoId,
                             Double valorInvestido,
                             Integer prazoMeses) {

        // Validações básicas
        if (clienteId == null) {
            throw new IllegalArgumentException("clienteId não pode ser nulo.");
        }
        if (produtoId == null) {
            throw new IllegalArgumentException("produtoId não pode ser nulo.");
        }
        if (valorInvestido == null || valorInvestido <= 0) {
            throw new IllegalArgumentException("valorInvestido deve ser maior que zero.");
        }
        if (prazoMeses == null || prazoMeses <= 0) {
            throw new IllegalArgumentException("prazoMeses deve ser maior que zero.");
        }

        // Verifica se o cliente existe (desafio trabalha com clienteId consistente)
        Cliente cliente = clienteRepository.findById(clienteId);
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não encontrado para o id: " + clienteId);
        }

        // Busca o produto e já valida dentro do ProdutoService (pode lançar IllegalArgumentException)
        Produto produto = produtoService.buscarPorId(produtoId);

        // Regra de cálculo da simulação:
        // Ex.: valorFinal = valorInvestido * (1 + rentabilidade * prazoMeses/12)
        double taxaAnual = produto.getRentabilidade(); // ex: 0.12 = 12% ao ano
        double fator = 1 + taxaAnual * (prazoMeses / 12.0);
        double valorFinal = valorInvestido * fator;

        // Monta objeto de Simulação
        Simulacao simulacao = new Simulacao();
        simulacao.setClienteId(clienteId);
        simulacao.setProduto(produto.getNome());
        simulacao.setValorInvestido(valorInvestido);
        simulacao.setValorFinal(valorFinal);
        simulacao.setPrazoMeses(prazoMeses);

        // Data da simulação no formato ISO (compatível com o JSON do desafio)
        String dataAtual = LocalDateTime.now()
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        simulacao.setDataSimulacao(dataAtual);

        // Persiste e retorna
        simulacaoRepository.persist(simulacao);

        return simulacao;
    }

    /**
     * Lista todas as simulações registradas.
     */
    public List<Simulacao> listarTodas() {
        return simulacaoRepository.listAll();
    }

    /**
     * Lista simulações de um cliente específico.
     */
    public List<Simulacao> listarPorCliente(Long clienteId) {
        if (clienteId == null) {
            throw new IllegalArgumentException("clienteId não pode ser nulo.");
        }
        return simulacaoRepository.findByClienteId(clienteId);
    }
}
