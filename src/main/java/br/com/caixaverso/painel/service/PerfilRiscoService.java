package br.com.caixaverso.painel.service;

import br.com.caixaverso.painel.dto.PerfilRiscoResponseDTO;
import br.com.caixaverso.painel.model.Cliente;
import br.com.caixaverso.painel.repository.ClienteRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PerfilRiscoService {

    @Inject
    ClienteRepository clienteRepository;

    @Inject
    TelemetriaService telemetriaService;

    public PerfilRiscoResponseDTO calcularPerfil(Long clienteId) {

        long inicio = System.currentTimeMillis(); // medir início do processamento

        try {
            if (clienteId == null) {
                throw new IllegalArgumentException("clienteId é obrigatório.");
            }

            Cliente cliente = clienteRepository.findById(clienteId);

            if (cliente == null) {
                throw new IllegalArgumentException("Cliente não encontrado para o id: " + clienteId);
            }

            int pontuacao = calcularPontuacaoSimples(cliente);

            String perfil;
            String descricao;

            if (pontuacao <= 40) {
                perfil = "Conservador";
                descricao = "Perfil conservador, com foco em liquidez e segurança.";
            } else if (pontuacao <= 80) {
                perfil = "Moderado";
                descricao = "Perfil equilibrado entre liquidez e rentabilidade.";
            } else {
                perfil = "Agressivo";
                descricao = "Perfil agressivo, com busca de maior rentabilidade aceitando maior risco.";
            }

            return new PerfilRiscoResponseDTO(
                    clienteId,
                    perfil,
                    pontuacao,
                    descricao
            );

        } finally {
            // medir fim e registrar telemetria SEMPRE, mesmo em caso de erro
            long fim = System.currentTimeMillis();
            telemetriaService.registrar("perfil-risco", fim - inicio);
        }
    }

    private int calcularPontuacaoSimples(Cliente cliente) {

        int pontos = 0;

        // 1. Volume (valorTotalInvestido)
        double volume = cliente.getValorTotalInvestido() != null
                ? cliente.getValorTotalInvestido() : 0.0;

        if (volume <= 5000) pontos += 10;
        else if (volume <= 20000) pontos += 20;
        else if (volume <= 50000) pontos += 30;
        else pontos += 40; // acima de 50k → agressivo


        //  Movimentação Mensal
        int mov = cliente.getMovimentacaoMensal() != null
                ? cliente.getMovimentacaoMensal() : 0;

        if (mov <= 1) pontos += 10;
        else if (mov <= 4) pontos += 20;
        else if (mov <= 10) pontos += 30;
        else pontos += 40; // mais de 10 movimentações


        //Preferência por Liquidez ou Rentabilidade
        Integer pref = cliente.getPrefereLiquidez();

        if (pref != null && pref == 1) {
            pontos += 5;   // prefere liquidez → conservador
        } else {
            pontos += 35;  // prefere rentabilidade → agressivo
        }

        return pontos;
    }
    public int testarPontuacao(Cliente cliente) {
        return calcularPontuacaoSimples(cliente);
    }

}
