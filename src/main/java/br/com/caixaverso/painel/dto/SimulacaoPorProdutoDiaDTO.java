package br.com.caixaverso.painel.dto;

public record SimulacaoPorProdutoDiaDTO(
        String produto,
        String data,
        int quantidadeSimulacoes,
        double mediaValorFinal
) {
}

