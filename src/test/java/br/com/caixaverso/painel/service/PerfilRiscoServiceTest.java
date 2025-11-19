package br.com.caixaverso.painel.service;

import br.com.caixaverso.painel.model.Cliente;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PerfilRiscoServiceTest {

    PerfilRiscoService service = new PerfilRiscoService();

    @Test
    void deveRetornarPontuacaoConservador() {
        Cliente cliente = new Cliente();
        cliente.setValorTotalInvestido(3000.0);  // → 10 pontos
        cliente.setMovimentacaoMensal(1);        // → 10 pontos
        cliente.setPrefereLiquidez(1);           // → 5 pontos (prefere liquidez)

        int pontuacao = service.testarPontuacao(cliente);

        Assertions.assertEquals(25, pontuacao); // 10 + 10 + 5
    }

    @Test
    void deveRetornarPontuacaoModerado() {
        Cliente cliente = new Cliente();
        cliente.setValorTotalInvestido(15000.0); // → 20 pontos
        cliente.setMovimentacaoMensal(3);        // → 20 pontos
        cliente.setPrefereLiquidez(0);           // → 35 pontos (rentabilidade)

        int pontuacao = service.testarPontuacao(cliente);

        Assertions.assertEquals(75, pontuacao); // 20 + 20 + 35
    }

    @Test
    void deveRetornarPontuacaoAgressivo() {
        Cliente cliente = new Cliente();
        cliente.setValorTotalInvestido(60000.0); // → 40 pontos
        cliente.setMovimentacaoMensal(12);       // → 40 pontos
        cliente.setPrefereLiquidez(0);           // → 35 pontos

        int pontuacao = service.testarPontuacao(cliente);

        Assertions.assertEquals(115, pontuacao); // 40 + 40 + 35
    }
}
