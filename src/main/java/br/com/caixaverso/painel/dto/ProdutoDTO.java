package br.com.caixaverso.painel.dto;

/**
 * DTO que representa um produto de investimento,
 * conforme o formato exigido pelo desafio.
 */
public record ProdutoDTO(
        Long id,
        String nome,
        String tipo,
        Double rentabilidade,
        String risco
) {}
