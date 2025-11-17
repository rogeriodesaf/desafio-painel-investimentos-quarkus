package br.com.caixaverso.painel.service;

import br.com.caixaverso.painel.dto.PerfilRiscoDTO;
import br.com.caixaverso.painel.model.Cliente;
import br.com.caixaverso.painel.repository.ClienteRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Service responsável por calcular o perfil de risco de um cliente.
 *
 * O desafio define que o perfil é determinado com base em:
 *  - movimentação mensal
 *  - valor total investido
 *  - preferência por liquidez
 *
 * O objetivo é retornar um objeto contendo:
 *  - perfil (Conservador, Moderado ou Agressivo)
 *  - pontuação numérica (ex.: 65)
 *  - descrição (ex.: “Perfil equilibrado entre segurança e rentabilidade.”)
 *
 * Este service não faz persistência; apenas calcula e devolve o resultado.
 * Todas as validações necessárias são feitas ANTES do cálculo.
 */
@ApplicationScoped
public class PerfilRiscoService {

    @Inject
    ClienteRepository clienteRepository;

    /**
     * Calcula o perfil de risco de um cliente existente.
     *
     * Regras gerais alinhadas ao PDF do desafio:
     *  - Perfis:
     *      Conservador (≤ 40 pontos)
     *      Moderado    (41–70)
     *      Agressivo   (> 70)
     *
     *  - Pontuação vem de:
     *      movimentação mensal      (baixa, média ou alta)
     *      valor total investido    (baixo, médio ou alto)
     *      prefere liquidez         (puxa para conservador)
     *
     * @param clienteId identificador do cliente cujo perfil será calculado
     * @return PerfilRiscoDTO contendo perfil, pontuação e descrição
     */
    public PerfilRiscoDTO calcularPerfilRisco(Long clienteId) {

        /**
         * 1. Validação básica — garante que o parâmetro não veio nulo.
         *    Isso evita NullPointerException e gera erro explícito.
         */
        if (clienteId == null) {
            throw new IllegalArgumentException("clienteId não pode ser nulo.");
        }

        /**
         * 2. Busca o cliente no banco.
         *    Se não existir, retornamos um erro claro.
         */
        Cliente cliente = clienteRepository.findById(clienteId);
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não encontrado para o id: " + clienteId);
        }

        /**
         * 3. Calculamos a pontuação bruta.
         *    Este método encapsula toda a lógica de negócio.
         */
        int pontuacao = calcularPontuacao(cliente);

        /**
         * 4. Traduzimos a pontuação em categoria de risco + descrição.
         *    As faixas são coerentes com os exemplos incluídos no desafio.
         */
        String perfil;
        String descricao;

        if (pontuacao <= 40) {
            perfil = "Conservador";
            descricao = "Perfil conservador, com foco em segurança e liquidez.";
        } else if (pontuacao <= 70) {
            perfil = "Moderado";
            descricao = "Perfil equilibrado entre segurança e rentabilidade.";
        } else {
            perfil = "Agressivo";
            descricao = "Perfil com maior tolerância a risco, buscando alta rentabilidade.";
        }

        /**
         * 5. Retorna um DTO imutável (record),
         *    seguindo o padrão moderno do Java e alinhado ao enunciado.
         */
        return new PerfilRiscoDTO(
                clienteId,
                perfil,
                pontuacao,
                descricao
        );
    }


    /**
     * Método responsável por gerar a pontuação do cliente.
     *
     * Quanto maior a pontuação, maior a tolerância a risco.
     * A pontuação final costuma variar aproximadamente entre 10 e 90.
     *
     * Critérios usados:
     *
     * 1. Movimentação mensal (quanto mais movimenta, mais tolera risco)
     *    - ≤ 2000  → baixa movimentação  → 10 pts
     *    - ≤ 8000  → média movimentação  → 25 pts
     *    - > 8000  → alta movimentação   → 40 pts
     *
     * 2. Valor total investido (maior patrimônio sugere maior tolerância a risco)
     *    - ≤ 10 mil    → 10 pts
     *    - ≤ 50 mil    → 25 pts
     *    - > 50 mil    → 40 pts
     *
     * 3. Preferência por liquidez:
     *    - 1 → Prefere liquidez → reduz risco → -10 pts
     *    - 0 → Prefere rentabilidade → aumenta risco → +10 pts
     *
     * Ao final, garantimos que a pontuação não fique negativa.
     *
     * @param cliente objeto com dados necessários para pontuação
     * @return pontuação final do cliente
     */
    private int calcularPontuacao(Cliente cliente) {

        int pontos = 0;

        Integer mov = cliente.getMovimentacaoMensal();
        Double total = cliente.getValorTotalInvestido();
        Integer prefereLiquidez = cliente.getPrefereLiquidez();

        /** 1. Pontuação por movimentação mensal */
        if (mov == null || mov <= 2000) {
            pontos += 10;
        } else if (mov <= 8000) {
            pontos += 25;
        } else {
            pontos += 40;
        }

        /** 2. Pontuação por valor total investido */
        if (total == null || total <= 10000.0) {
            pontos += 10;
        } else if (total <= 50000.0) {
            pontos += 25;
        } else {
            pontos += 40;
        }

        /**
         * 3. Ajuste por preferência de liquidez
         *    • 1 → liquidez → perfil mais conservador
         *    • 0 → rentabilidade → perfil mais agressivo
         */
        if (prefereLiquidez != null && prefereLiquidez == 1) {
            pontos -= 10;
        } else {
            pontos += 10;
        }

        /** Evita pontuação negativa */
        if (pontos < 0) {
            pontos = 0;
        }

        return pontos;
    }
}
