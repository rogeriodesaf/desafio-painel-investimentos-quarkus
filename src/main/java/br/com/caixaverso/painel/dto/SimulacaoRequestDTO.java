package br.com.caixaverso.painel.dto;

public record SimulacaoRequestDTO(
        Long clienteId,
        double valor,
        int prazoMeses,
        String tipoProduto
) {
}
