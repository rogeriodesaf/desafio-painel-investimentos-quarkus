package br.com.caixaverso.painel.dto;

public record InvestimentoResponseDTO(
        Long id,
        String tipo,
        double valor,
        double rentabilidade,
        String data
) {
}
