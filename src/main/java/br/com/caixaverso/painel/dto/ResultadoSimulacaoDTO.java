package br.com.caixaverso.painel.dto;


public record ResultadoSimulacaoDTO(
        Double valorFinal,
        Double rentabilidadeEfetiva,
        Integer prazoMeses
) {}
