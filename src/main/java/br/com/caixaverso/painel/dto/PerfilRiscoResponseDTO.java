package br.com.caixaverso.painel.dto;

public record PerfilRiscoResponseDTO(
        Long clienteId,
        String perfil,
        int pontuacao,
        String descricao
) {
}
