package br.com.caixaverso.painel.service;

import br.com.caixaverso.painel.dto.TelemetriaResponse;
import br.com.caixaverso.painel.model.Telemetria;
import br.com.caixaverso.painel.repository.TelemetriaRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@ApplicationScoped
public class TelemetriaService {

    @Inject
    TelemetriaRepository telemetriaRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_INSTANT;



    // REGISTRAR MÉTRICA POR SERVIÇO (chamado a cada request)
    @Transactional
    public void registrar(String servico, long tempoRespostaMs) {

        if (servico == null || servico.isBlank()) {
            throw new IllegalArgumentException("O nome do serviço não pode ser nulo ou vazio.");
        }

        if (tempoRespostaMs < 0) {
            throw new IllegalArgumentException("tempoRespostaMs deve ser >= 0.");
        }

        Telemetria existente = telemetriaRepository.findByServico(servico);

        // Ex.: "2025-10-31T14:00:00Z"
        String dataAtual = OffsetDateTime.now(ZoneOffset.UTC).format(FORMATTER);

        // Criar registro novo se não existir
        if (existente == null) {
            Telemetria novo = new Telemetria();
            novo.setServico(servico);
            novo.setQuantidadeChamadas(1);
            novo.setMediaTempoRespostaMs((double) tempoRespostaMs);
            novo.setData(dataAtual);
            telemetriaRepository.persist(novo);
            return;
        }

        // Atualizar média incremental
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


    public TelemetriaResponse montarResposta() {

        List<Telemetria> registros = telemetriaRepository.listAll();

        // Converter entidades -> DTO TelemetriaResponse.Servico
        List<TelemetriaResponse.Servico> servicos = registros.stream()
                .map(t -> new TelemetriaResponse.Servico(
                        t.getServico(),
                        t.getQuantidadeChamadas(),
                        t.getMediaTempoRespostaMs()
                ))
                .toList();

        // Determinar início do período
        String inicio = registros.isEmpty()
                ? null
                : registros.stream()
                .map(Telemetria::getData)
                .sorted()
                .findFirst()
                .orElse(null);

        // Determinar fim (agora)
        String fim = OffsetDateTime.now(ZoneOffset.UTC)
                .format(FORMATTER);

        TelemetriaResponse.Periodo periodo = new TelemetriaResponse.Periodo(inicio, fim);

        return new TelemetriaResponse(servicos, periodo);
    }


    public double mediaGeralTempoResposta() {
        List<Telemetria> registros = telemetriaRepository.listAll();
        if (registros.isEmpty()) return 0;

        double soma = 0;
        int total = 0;

        for (Telemetria t : registros) {
            soma += t.getMediaTempoRespostaMs() * t.getQuantidadeChamadas();
            total += t.getQuantidadeChamadas();
        }

        return soma / total;
    }

    public String servicoMaisLento() {
        return telemetriaRepository.listAll().stream()
                .max(Comparator.comparingDouble(Telemetria::getMediaTempoRespostaMs))
                .map(Telemetria::getServico)
                .orElse("nenhum");
    }
}
