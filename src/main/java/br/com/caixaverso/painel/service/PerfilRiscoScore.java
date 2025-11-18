package br.com.caixaverso.painel.service;

public record PerfilRiscoScore(
        double volumeScore,
        double frequenciaScore,
        double preferenciaScore,
        int pontuacaoFinal
) {
}
