package br.com.caixaverso.painel.service;

import br.com.caixaverso.painel.dto.SimulacaoRequestDTO;
import br.com.caixaverso.painel.dto.SimulacaoResponseDTO;
import br.com.caixaverso.painel.model.Produto;
import br.com.caixaverso.painel.model.Simulacao;
import br.com.caixaverso.painel.repository.ProdutoRepository;
import br.com.caixaverso.painel.repository.SimulacaoRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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

    @Inject
    SimulacaoDiaService simulacaoDiaService;

    @Transactional
    public SimulacaoResponseDTO simular(SimulacaoRequestDTO request) {
        long inicio = System.currentTimeMillis();

        validarRequest(request);

        // Buscar produtos do tipo solicitado
        List<Produto> produtos = produtoRepository.find("tipo", request.tipoProduto()).list();
        if (produtos == null || produtos.isEmpty()) {
            throw new IllegalArgumentException("Nenhum produto encontrado para o tipo: " + request.tipoProduto());
        }

        // Escolher o melhor produto que atende aos parâmetros
        Produto escolhido = produtos.stream()
                .filter(p -> produtoAtendeParametros(p, request))
                .max(Comparator.comparingDouble(p -> p.getRentabilidade() != null ? p.getRentabilidade() : 0.0))
                .orElseThrow(() -> new IllegalArgumentException("Nenhum produto atende aos parâmetros informados."));

        // Fórmula correta proporcional ao prazo (conforme exemplo do PDF)
        double rentabilidadeAnual = escolhido.getRentabilidade() != null ? escolhido.getRentabilidade() : 0.0;
        double jurosProporcionais = rentabilidadeAnual * (request.prazoMeses() / 12.0);
        double valorFinal = request.valor() * (1 + jurosProporcionais);

        // Formatando data exatamente como "2025-10-31T14:00:00Z"
        String dataSimulacao = Instant.now()
                .truncatedTo(ChronoUnit.SECONDS)
                .toString();


        // Persistindo no banco
        Simulacao simulacao = new Simulacao();
        simulacao.setClienteId(request.clienteId());
        simulacao.setProduto(escolhido.getNome());
        simulacao.setValorInvestido(request.valor());
        simulacao.setValorFinal(valorFinal);
        simulacao.setPrazoMeses(request.prazoMeses());
        simulacao.setDataSimulacao(dataSimulacao);

        simulacaoRepository.persist(simulacao);

        // Registrar estatística diária SEM duplicar valores
        String dataHoje = dataSimulacao.substring(0, 10);

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

        // Construindo resposta
        SimulacaoResponseDTO.ProdutoValidadoDTO produtoDTO =
                new SimulacaoResponseDTO.ProdutoValidadoDTO(
                        escolhido.getId(),
                        escolhido.getNome(),
                        escolhido.getTipo(),
                        rentabilidadeAnual,
                        escolhido.getRisco()
                );

        SimulacaoResponseDTO.ResultadoSimulacaoDTO resultadoDTO =
                new SimulacaoResponseDTO.ResultadoSimulacaoDTO(
                        valorFinal,
                        rentabilidadeAnual,
                        request.prazoMeses()
                );

        long fim = System.currentTimeMillis();
        telemetriaService.registrar("simular-investimento", fim - inicio);

        return new SimulacaoResponseDTO(produtoDTO, resultadoDTO, dataSimulacao);
    }

    public List<Simulacao> listarTodas() {
        return simulacaoRepository.listAll();
    }

    private void validarRequest(SimulacaoRequestDTO request) {
        if (request == null) throw new IllegalArgumentException("Request de simulação não pode ser nulo.");
        if (request.clienteId() == null) throw new IllegalArgumentException("clienteId é obrigatório.");
        if (request.valor() <= 0) throw new IllegalArgumentException("valor deve ser maior que zero.");
        if (request.prazoMeses() <= 0) throw new IllegalArgumentException("prazoMeses deve ser maior que zero.");
        if (request.tipoProduto() == null || request.tipoProduto().isBlank())
            throw new IllegalArgumentException("tipoProduto é obrigatório.");
    }

    private boolean produtoAtendeParametros(Produto produto, SimulacaoRequestDTO request) {
        Double valorMinimo = produto.getValorMinimo();
        Double valorMaximo = produto.getValorMaximo();
        Integer prazoMin = produto.getPrazoMinMeses();
        Integer prazoMax = produto.getPrazoMaxMeses();

        double valor = request.valor();
        int prazo = request.prazoMeses();

        boolean valorOk = (valorMinimo == null || valor >= valorMinimo)
                && (valorMaximo == null || valor <= valorMaximo);

        boolean prazoOk = (prazoMin == null || prazo >= prazoMin)
                && (prazoMax == null || prazo <= prazoMax);

        return valorOk && prazoOk;
    }
}
