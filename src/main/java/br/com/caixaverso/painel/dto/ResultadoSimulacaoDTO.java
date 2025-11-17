package br.com.caixaverso.painel.dto;

/**
 * DTO que representa o resultado final da simulação,
 * conforme especificado no desafio.
 */
public record ResultadoSimulacaoDTO(
        Double valorFinal,
        Double rentabilidadeEfetiva,
        Integer prazoMeses
) {}
