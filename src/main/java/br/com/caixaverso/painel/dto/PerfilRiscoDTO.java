package br.com.caixaverso.painel.dto;

public record PerfilRiscoDTO(
        Long clienteId,
        String perfil,
        Integer pontuacao,
        String descricao
) {}
