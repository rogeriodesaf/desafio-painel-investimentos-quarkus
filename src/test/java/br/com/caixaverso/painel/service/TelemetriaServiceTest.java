package br.com.caixaverso.painel.service;

import br.com.caixaverso.painel.dto.TelemetriaResponse;
import br.com.caixaverso.painel.model.Telemetria;
import br.com.caixaverso.painel.repository.TelemetriaRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

@QuarkusTest
public class TelemetriaServiceTest {

    @Inject
    TelemetriaService telemetriaService;

    @InjectMock
    TelemetriaRepository telemetriaRepository;

    // Utilitário para criar Telemetria
    private Telemetria criar(String servico, int chamadas, double media, String data) {
        Telemetria t = new Telemetria();
        t.setServico(servico);
        t.setQuantidadeChamadas(chamadas);
        t.setMediaTempoRespostaMs(media);
        t.setData(data);
        return t;
    }

    // ------------------------------------------------------------
    // TESTE 1 — REGISTRAR (criando novo)
    // ------------------------------------------------------------
    @Test
    public void testRegistrarCriarNovo() {

        Mockito.when(telemetriaRepository.findByServico("simular")).thenReturn(null);

        Mockito.doNothing().when(telemetriaRepository).persist(Mockito.any(Telemetria.class));

        telemetriaService.registrar("simular", 250);

        Mockito.verify(telemetriaRepository).persist(Mockito.any(Telemetria.class));
    }

    // ------------------------------------------------------------
    // TESTE 2 — REGISTRAR (atualizando média)
    // ------------------------------------------------------------
    @Test
    public void testRegistrarAtualizar() {

        Telemetria existente = criar("perfil-risco", 2, 200.0, "2025-01-10");

        Mockito.when(telemetriaRepository.findByServico("perfil-risco"))
                .thenReturn(existente);

        telemetriaService.registrar("perfil-risco", 400);

        Assertions.assertEquals(3, existente.getQuantidadeChamadas());

        double novaMedia = ((200 * 2) + 400) / 3.0;
        Assertions.assertEquals(novaMedia, existente.getMediaTempoRespostaMs());
    }

    // ------------------------------------------------------------
    // TESTE 3 — REGISTRAR (erros)
    // ------------------------------------------------------------
    @Test
    public void testRegistrarErroServicoVazio() {

        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> telemetriaService.registrar("", 100)
        );

        Assertions.assertEquals("O nome do serviço não pode ser nulo ou vazio.", ex.getMessage());
    }

    @Test
    public void testRegistrarErroTempoNegativo() {

        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> telemetriaService.registrar("simular", -1)
        );

        Assertions.assertEquals("tempoRespostaMs deve ser >= 0.", ex.getMessage());
    }

    // ------------------------------------------------------------
    // TESTE 4 — montarResposta()
    // ------------------------------------------------------------
    @Test
    public void testMontarResposta() {

        List<Telemetria> dados = List.of(
                criar("simular-investimento", 10, 250.0, "2025-01-01"),
                criar("perfil-risco", 5, 175.0, "2025-01-02")
        );

        Mockito.when(telemetriaRepository.listAll()).thenReturn(dados);

        TelemetriaResponse resp = telemetriaService.montarResposta();

        Assertions.assertEquals(2, resp.servicos().size());
        Assertions.assertEquals("2025-01-01", resp.periodo().inicio());
        Assertions.assertNotNull(resp.periodo().fim());
    }

    // ------------------------------------------------------------
    // TESTE 5 — mediaGeralTempoResposta()
    // ------------------------------------------------------------
    @Test
    public void testMediaGeral() {

        List<Telemetria> dados = List.of(
                criar("simular", 2, 200.0, "2025-01-01"), // 400
                criar("perfil", 1, 500.0, "2025-01-01")   // 500
        ); // total = 900 / 3 = 300

        Mockito.when(telemetriaRepository.listAll()).thenReturn(dados);

        double media = telemetriaService.mediaGeralTempoResposta();

        Assertions.assertEquals(300.0, media);
    }

    // ------------------------------------------------------------
    // TESTE 6 — servicoMaisLento()
    // ------------------------------------------------------------
    @Test
    public void testServicoMaisLento() {

        List<Telemetria> dados = List.of(
                criar("simular", 5, 150.0, "2025-01-01"),
                criar("perfil", 5, 300.0, "2025-01-01"),
                criar("recomendacao", 5, 250.0, "2025-01-01")
        );

        Mockito.when(telemetriaRepository.listAll()).thenReturn(dados);

        String servico = telemetriaService.servicoMaisLento();

        Assertions.assertEquals("perfil", servico);
    }
}
