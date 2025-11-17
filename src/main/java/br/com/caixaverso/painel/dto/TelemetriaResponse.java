package br.com.caixaverso.painel.dto;

import java.util.List;

public record TelemetriaResponse(
        List<Servico> servicos,
        Periodo periodo
) {

    public record Servico(
            String nome,
            int quantidadeChamadas,
            double mediaTempoRespostaMs
    ) {}

    public record Periodo(
            String inicio,
            String fim
    ) {}
}
