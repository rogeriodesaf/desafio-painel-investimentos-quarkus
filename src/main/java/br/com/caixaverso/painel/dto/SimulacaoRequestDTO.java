package br.com.caixaverso.painel.dto;

/**
 * DTO de entrada para o endpoint de simulação de investimentos.
 * Segue exatamente o formato definido no desafio.
 */
public record SimulacaoRequestDTO(
        Long clienteId,
        Double valor,
        Integer prazoMeses,
        String tipoProduto
) {}
