package br.com.caixaverso.painel.dto;

public record ProdutoResponseDTO(
        Long id,
        String nome,
        String tipo,
        double rentabilidade,
        String risco // Baixo/Médio/Alto
) {
}
