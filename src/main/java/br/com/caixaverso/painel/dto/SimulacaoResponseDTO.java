package br.com.caixaverso.painel.dto;

public record SimulacaoResponseDTO(
        ProdutoValidadoDTO produtoValidado,
        ResultadoSimulacaoDTO resultadoSimulacao,
        String dataSimulacao
) {

    public record ProdutoValidadoDTO(
            Long id,
            String nome,
            String tipo,
            double rentabilidade,
            String risco
    ) {
    }

    public record ResultadoSimulacaoDTO(
            double valorFinal,
            double rentabilidadeEfetiva,
            int prazoMeses
    ) {
    }
}
