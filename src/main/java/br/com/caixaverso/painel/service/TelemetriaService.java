package br.com.caixaverso.painel.service;

import br.com.caixaverso.painel.model.Telemetria;
import br.com.caixaverso.painel.repository.TelemetriaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class TelemetriaService {

    @Inject
    TelemetriaRepository telemetriaRepository;

    @Transactional
    public void registrar(String servico, long tempoRespostaMs) {
        if (servico == null || servico.isBlank()) {
            throw new IllegalArgumentException("O nome do serviço não pode ser nulo ou vazio.");
        }

        if (tempoRespostaMs < 0) {
            throw new IllegalArgumentException("tempoRespostaMs deve ser >= 0.");
        }

        Telemetria existente = telemetriaRepository.findByServico(servico);
        String dataAtual = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        if (existente == null) {
            Telemetria novo = new Telemetria();
            novo.setServico(servico);
            novo.setQuantidadeChamadas(1);
            novo.setMediaTempoRespostaMs((double) tempoRespostaMs);
            novo.setData(dataAtual);
            telemetriaRepository.persist(novo);
            return;
        }

        int chamadasAntigas = existente.getQuantidadeChamadas();
        double mediaAntiga = existente.getMediaTempoRespostaMs();

        double novaMedia = ((mediaAntiga * chamadasAntigas) + tempoRespostaMs)
                / (chamadasAntigas + 1);

        existente.setQuantidadeChamadas(chamadasAntigas + 1);
        existente.setMediaTempoRespostaMs(novaMedia);
        existente.setData(dataAtual);
    }

    public List<Telemetria> listarTudo() {
        return telemetriaRepository.listAll();
    }

    public Map<String, Object> calcularEstatisticas() {
        List<Telemetria> registros = telemetriaRepository.listAll();

        if (registros.isEmpty()) {
            return Map.of(
                    "mediaTempoResposta", 0,
                    "servicoMaisLento", "nenhum",
                    "totalRequisicoes", 0
            );
        }

        double soma = 0;
        int totalChamadas = 0;

        for (Telemetria t : registros) {
            soma += t.getMediaTempoRespostaMs() * t.getQuantidadeChamadas();
            totalChamadas += t.getQuantidadeChamadas();
        }

        double mediaGeral = soma / totalChamadas;

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
