package br.com.caixaverso.painel.service;

import br.com.caixaverso.painel.dto.SimulacaoPorProdutoDiaDTO;
import br.com.caixaverso.painel.model.SimulacaoDia;
import br.com.caixaverso.painel.repository.SimulacaoDiaRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@ApplicationScoped
public class SimulacaoDiaService {

    @Inject
    SimulacaoDiaRepository simulacaoDiaRepository;

    @Inject
    TelemetriaService telemetriaService; // ← TELEMETRIA ADICIONADA

    public List<SimulacaoPorProdutoDiaDTO> listarTudo() {

        long inicio = System.currentTimeMillis(); // medir início

        try {
            // Converte entidade → DTO
            return simulacaoDiaRepository.listAll().stream()
                    .map(s -> new SimulacaoPorProdutoDiaDTO(
                            s.getProduto(),
                            s.getDataSimples(),
                            s.getQuantidadeSimulacoes(),
                            s.getMediaValorFinal()
                    ))
                    .toList();

        } finally {
            long fim = System.currentTimeMillis();
            telemetriaService.registrar("simulacoes-por-produto-dia", fim - inicio);
        }
    }

    @Transactional
    public void registrar(String produto, int quantidade, double media) {

        String hoje = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

        // Verificar se já existe registro para este produto e dia
        SimulacaoDia existente = simulacaoDiaRepository.find(
                "produto = ?1 AND dataSimples = ?2", produto, hoje
        ).firstResult();

        if (existente != null) {
            existente.setQuantidadeSimulacoes(quantidade);
            existente.setMediaValorFinal(media);
            return;
        }

        // Criar novo registro para o dia
        SimulacaoDia novo = new SimulacaoDia();
        novo.setProduto(produto);
        novo.setDataSimples(hoje);
        novo.setQuantidadeSimulacoes(quantidade);
        novo.setMediaValorFinal(media);

        simulacaoDiaRepository.persist(novo);
    }
}
