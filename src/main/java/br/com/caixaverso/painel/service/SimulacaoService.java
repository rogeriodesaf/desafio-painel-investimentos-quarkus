package br.com.caixaverso.painel.service;

import br.com.caixaverso.painel.dto.*;
import br.com.caixaverso.painel.model.Cliente;
import br.com.caixaverso.painel.model.Produto;
import br.com.caixaverso.painel.model.Simulacao;
import br.com.caixaverso.painel.repository.ClienteRepository;
import br.com.caixaverso.painel.repository.SimulacaoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class SimulacaoService {

    @Inject
    SimulacaoRepository simulacaoRepository;

    @Inject
    ProdutoService produtoService;

    @Inject
    ClienteRepository clienteRepository;

    /**
     * Serviço de simulação totalmente aderente ao desafio.
     * Recebe SimulacaoRequestDTO e retorna SimulacaoResponseDTO.
     */
    public SimulacaoResponseDTO simular(SimulacaoRequestDTO request) {

        // 1. Validações do desafio
        if (request.clienteId() == null)
            throw new IllegalArgumentException("clienteId é obrigatório.");

        if (request.valor() == null || request.valor() <= 0)
            throw new IllegalArgumentException("valor deve ser maior que zero.");

        if (request.prazoMeses() == null || request.prazoMeses() <= 0)
            throw new IllegalArgumentException("prazoMeses deve ser maior que zero.");

        if (request.tipoProduto() == null || request.tipoProduto().isBlank())
            throw new IllegalArgumentException("tipoProduto é obrigatório.");

        // 2. Verifica cliente
        Cliente cliente = clienteRepository.findById(request.clienteId());
        if (cliente == null)
            throw new IllegalArgumentException("Cliente não encontrado.");

        // 3. Busca produto por tipo (como exige o PDF)
        Produto produto = produtoService.buscarPorTipo(request.tipoProduto());
        if (produto == null)
            throw new IllegalArgumentException("Produto não encontrado para o tipo informado.");

        // 4. Cálculo do valor final
        double taxa = produto.getRentabilidade();
        double fator = 1 + taxa * (request.prazoMeses() / 12.0);
        double valorFinal = request.valor() * fator;

        // 5. Persiste simulação no banco
        Simulacao simulacao = new Simulacao();
        simulacao.setClienteId(request.clienteId());
        simulacao.setProduto(produto.getNome());
        simulacao.setValorInvestido(request.valor());
        simulacao.setValorFinal(valorFinal);
        simulacao.setPrazoMeses(request.prazoMeses());
        simulacao.setDataSimulacao(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        simulacaoRepository.persist(simulacao);

        // 6. Monta DTOs conforme o desafio
        ProdutoDTO produtoDTO = new ProdutoDTO(
                produto.getId(),
                produto.getNome(),
                produto.getTipo(),
                produto.getRentabilidade(),
                produto.getRisco()
        );

        ResultadoSimulacaoDTO resultadoDTO = new ResultadoSimulacaoDTO(
                valorFinal,
                taxa,
                request.prazoMeses()
        );

        return new SimulacaoResponseDTO(
                produtoDTO,
                resultadoDTO,
                simulacao.getDataSimulacao()
        );
    }

    public java.util.List<Simulacao> listarTodas() {
        return simulacaoRepository.listAll();
    }
}
