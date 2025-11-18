package br.com.caixaverso.painel.service;

import br.com.caixaverso.painel.dto.SimulacaoRequestDTO;
import br.com.caixaverso.painel.dto.SimulacaoResponseDTO;
import br.com.caixaverso.painel.model.Produto;
import br.com.caixaverso.painel.model.Simulacao;
import br.com.caixaverso.painel.repository.ProdutoRepository;
import br.com.caixaverso.painel.repository.SimulacaoRepository;
import br.com.caixaverso.painel.service.SimulacaoDiaService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;

@ApplicationScoped
public class SimulacaoService {

    @Inject
    ProdutoRepository produtoRepository;

    @Inject
    SimulacaoRepository simulacaoRepository;

    @Inject
    TelemetriaService telemetriaService;

    // ✔ agora devidamente injetado
    @Inject
    SimulacaoDiaService simulacaoDiaService;

    @Transactional
    public SimulacaoResponseDTO simular(SimulacaoRequestDTO request) {
        long inicio = System.currentTimeMillis();

        validarRequest(request);

        List<Produto> produtos = produtoRepository.find("tipo", request.tipoProduto()).list();
        if (produtos == null || produtos.isEmpty()) {
            throw new IllegalArgumentException("Nenhum produto encontrado para o tipo: " + request.tipoProduto());
        }

        Produto escolhido = produtos.stream()
                .filter(p -> produtoAtendeParametros(p, request))
                .max(Comparator.comparingDouble(p -> {
                    Double r = p.getRentabilidade();
                    return r != null ? r : 0.0;
                }))
                .orElseThrow(() -> new IllegalArgumentException("Nenhum produto atende aos parâmetros informados."));

        double rentabilidadeEfetiva = escolhido.getRentabilidade() != null ? escolhido.getRentabilidade() : 0.0;
        double valorFinal = request.valor() * (1 + rentabilidadeEfetiva);

        String dataSimulacao = OffsetDateTime.now().toString();

        // ===== Persistência da simulação =====
        Simulacao simulacao = new Simulacao();
        simulacao.setClienteId(request.clienteId());
        simulacao.setProduto(escolhido.getNome());
        simulacao.setValorInvestido(request.valor());
        simulacao.setValorFinal(valorFinal);
        simulacao.setPrazoMeses(request.prazoMeses());
        simulacao.setDataSimulacao(dataSimulacao);

        simulacaoRepository.persist(simulacao);

        // ======================================================
        //  NOVO TRECHO — REGISTRAR ESTATÍSTICA DIÁRIA
        // ======================================================
        String dataHoje = OffsetDateTime.now().toLocalDate().toString();

        List<Simulacao> simulacoesHoje = simulacaoRepository.find(
                "produto = ?1 AND substr(dataSimulacao, 1, 10) = ?2",
                escolhido.getNome(),
                dataHoje
        ).list();

        int quantidade = simulacoesHoje.size();

        double media = simulacoesHoje.stream()
                .mapToDouble(Simulacao::getValorFinal)
                .average()
                .orElse(0.0);

        simulacaoDiaService.registrar(escolhido.getNome(), quantidade, media);
        // ======================================================


        // ===== DTOs da resposta =====
        SimulacaoResponseDTO.ProdutoValidadoDTO produtoDTO =
                new SimulacaoResponseDTO.ProdutoValidadoDTO(
                        escolhido.getId(),
                        escolhido.getNome(),
                        escolhido.getTipo(),
                        rentabilidadeEfetiva,
                        escolhido.getRisco()
                );

        SimulacaoResponseDTO.ResultadoSimulacaoDTO resultadoDTO =
                new SimulacaoResponseDTO.ResultadoSimulacaoDTO(
                        valorFinal,
                        rentabilidadeEfetiva,
                        request.prazoMeses()
                );

        long fim = System.currentTimeMillis();
        long duracao = fim - inicio;
        telemetriaService.registrar("simular-investimento", duracao);

        return new SimulacaoResponseDTO(produtoDTO, resultadoDTO, dataSimulacao);
    }

    public List<Simulacao> listarTodas() {
        return simulacaoRepository.listAll();
    }

    private void validarRequest(SimulacaoRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Request de simulação não pode ser nulo.");
        }
        if (request.clienteId() == null) {
            throw new IllegalArgumentException("clienteId é obrigatório.");
        }
        if (request.valor() <= 0) {
            throw new IllegalArgumentException("valor deve ser maior que zero.");
        }
        if (request.prazoMeses() <= 0) {
            throw new IllegalArgumentException("prazoMeses deve ser maior que zero.");
        }
        if (request.tipoProduto() == null || request.tipoProduto().isBlank()) {
            throw new IllegalArgumentException("tipoProduto é obrigatório.");
        }
    }

    private boolean produtoAtendeParametros(Produto produto, SimulacaoRequestDTO request) {
        Double valorMinimo = produto.getValorMinimo();
        Double valorMaximo = produto.getValorMaximo();
        Integer prazoMin = produto.getPrazoMinMeses();
        Integer prazoMax = produto.getPrazoMaxMeses();

        double valor = request.valor();
        int prazo = request.prazoMeses();

        boolean valorOk = true;
        boolean prazoOk = true;

        if (valorMinimo != null && valor < valorMinimo) {
            valorOk = false;
        }
        if (valorMaximo != null && valor > valorMaximo) {
            valorOk = false;
        }
        if (prazoMin != null && prazo < prazoMin) {
            prazoOk = false;
        }
        if (prazoMax != null && prazo > prazoMax) {
            prazoOk = false;
        }

        return valorOk && prazoOk;
    }
}
