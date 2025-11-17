package br.com.caixaverso.painel.service;

import br.com.caixaverso.painel.model.Telemetria;
import br.com.caixaverso.painel.repository.TelemetriaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Service responsável por:
 *
 * 1. Registrar telemetria por serviço.
 *    - Incrementa quantidadeChamadas
 *    - Recalcula média de tempo (mediaTempoRespostaMs)
 *    - Atualiza a data da última atualização
 *
 * 2. Fornecer estatísticas agregadas
 *    utilizadas pelo endpoint GET /telemetria.
 *
 * Diferente de uma tabela "log" que grava cada chamada,
 * neste desafio a tabela telemetria armazena UM REGISTRO POR SERVIÇO,
 * contendo valores agregados.
 */
@ApplicationScoped
public class TelemetriaService {

    @Inject
    TelemetriaRepository telemetriaRepository;

    /**
     * Registra o tempo de resposta de um serviço específico.
     *
     * Regras:
     *  - Se o serviço ainda não existir, cria um novo registro.
     *  - Se já existir, atualiza:
     *        quantidadeChamadas += 1
     *        mediaTempoRespostaMs = nova média
     *  - A data é atualizada para o momento atual.
     *
     * @param servico nome do serviço (ex.: "simulacao")
     * @param tempoRespostaMs tempo de resposta da requisição em milissegundos
     */
    public void registrar(String servico, long tempoRespostaMs) {

        if (servico == null || servico.isBlank()) {
            throw new IllegalArgumentException("O nome do serviço não pode ser nulo ou vazio.");
        }

        if (tempoRespostaMs < 0) {
            throw new IllegalArgumentException("tempoRespostaMs deve ser >= 0.");
        }

        // Tenta encontrar o registro existente
        Telemetria existente = telemetriaRepository.findByServico(servico);

        if (existente == null) {
            // Cria novo registro para este serviço
            Telemetria novo = new Telemetria();
            novo.setServico(servico);
            novo.setQuantidadeChamadas(1);
            novo.setMediaTempoRespostaMs((double) tempoRespostaMs);

            String dataAtual = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            novo.setData(dataAtual);

            telemetriaRepository.persist(novo);
            return;
        }

        // Atualiza registro existente

        int chamadasAntigas = existente.getQuantidadeChamadas();
        double mediaAntiga = existente.getMediaTempoRespostaMs();

        // Cálculo da nova média ponderada
        double novaMedia = ((mediaAntiga * chamadasAntigas) + tempoRespostaMs)
                / (chamadasAntigas + 1);

        existente.setQuantidadeChamadas(chamadasAntigas + 1);
        existente.setMediaTempoRespostaMs(novaMedia);

        String dataAtual = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        existente.setData(dataAtual);

        telemetriaRepository.persist(existente);
    }

    /**
     * Calcula estatísticas gerais exigidas pelo endpoint GET /telemetria.
     *
     * O desafio pede:
     *  - mediaTempoResposta
     *  - servicoMaisLento
     *  - totalRequisicoes
     *
     * Como nossa tabela já possui dados agregados por serviço,
     * usamos esses valores diretamente.
     */
    public Map<String, Object> calcularEstatisticas() {

        List<Telemetria> registros = telemetriaRepository.listAll();

        if (registros.isEmpty()) {
            return Map.of(
                    "mediaTempoResposta", 0,
                    "servicoMaisLento", "nenhum",
                    "totalRequisicoes", 0
            );
        }

        // Média geral ponderada por quantidadeChamadas
        double soma = 0;
        int totalChamadas = 0;

        for (Telemetria t : registros) {
            soma += t.getMediaTempoRespostaMs() * t.getQuantidadeChamadas();
            totalChamadas += t.getQuantidadeChamadas();
        }

        double mediaGeral = soma / totalChamadas;

        // Serviço mais lento = maior mediaTempoRespostaMs
        String servicoMaisLento = registros.stream()
                .max((a, b) -> Double.compare(a.getMediaTempoRespostaMs(), b.getMediaTempoRespostaMs()))
                .map(Telemetria::getServico)
                .orElse("indefinido");

        return Map.of(
                "mediaTempoResposta", (long) mediaGeral,
                "servicoMaisLento", servicoMaisLento,
                "totalRequisicoes", totalChamadas
        );
    }
}
