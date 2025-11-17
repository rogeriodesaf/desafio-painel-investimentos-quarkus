package br.com.caixaverso.painel.dto;

/**
 * DTO de saída do endpoint de simulação de investimentos.
 * Representa exatamente o formato exigido pelo desafio.
 */
public record SimulacaoResponseDTO(
        ProdutoDTO produtoValidado,
        ResultadoSimulacaoDTO resultadoSimulacao,
        String dataSimulacao
) {}
