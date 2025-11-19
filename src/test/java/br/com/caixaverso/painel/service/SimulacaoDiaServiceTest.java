package br.com.caixaverso.painel.service;

import br.com.caixaverso.painel.dto.SimulacaoPorProdutoDiaDTO;
import br.com.caixaverso.painel.model.SimulacaoDia;
import br.com.caixaverso.painel.repository.SimulacaoDiaRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;

@QuarkusTest
public class SimulacaoDiaServiceTest {

    @Inject
    SimulacaoDiaService simulacaoDiaService;

    @InjectMock
    SimulacaoDiaRepository simulacaoDiaRepository;

    @InjectMock
    TelemetriaService telemetriaService;

    // ------------------------------------------
    // Criador de entidade SimulacaoDia
    // ------------------------------------------
    private SimulacaoDia criar(String produto, String data, int qtd, double media) {
        SimulacaoDia s = new SimulacaoDia();
        s.setProduto(produto);
        s.setDataSimples(data);
        s.setQuantidadeSimulacoes(qtd);
        s.setMediaValorFinal(media);
        return s;
    }

    // ============================================================
    // TESTE 1 — listarTudo()
    // ============================================================
    @Test
    public void testListarTudo() {

        List<SimulacaoDia> dados = List.of(
                criar("CDB TOP", "2025-01-01", 5, 11000.0),
                criar("Tesouro Selic", "2025-01-01", 3, 5700.0)
        );

        Mockito.when(simulacaoDiaRepository.listAll()).thenReturn(dados);

        List<SimulacaoPorProdutoDiaDTO> resp = simulacaoDiaService.listarTudo();

        Assertions.assertEquals(2, resp.size());
        Assertions.assertEquals("CDB TOP", resp.get(0).produto());
        Assertions.assertEquals(5, resp.get(0).quantidadeSimulacoes());
        Assertions.assertEquals(11000.0, resp.get(0).mediaValorFinal());

        // Verifica telemetria
        Mockito.verify(telemetriaService)
                .registrar(Mockito.eq("simulacoes-por-produto-dia"), Mockito.anyLong());
    }

    // ============================================================
    // TESTE 2 — registrar(): atualiza registro existente
    // ============================================================
    @Test
    public void testRegistrarAtualizaExistente() {

        String hoje = LocalDate.now().toString();

        SimulacaoDia existente = criar("CDB TOP", hoje, 2, 10000.0);

        PanacheQuery<SimulacaoDia> queryMock = Mockito.mock(PanacheQuery.class);
        Mockito.when(queryMock.firstResult()).thenReturn(existente);

        Mockito.when(
                simulacaoDiaRepository.find(
                        Mockito.anyString(),
                        Mockito.any(Object[].class)
                )
        ).thenReturn(queryMock);

        simulacaoDiaService.registrar("CDB TOP", 10, 12000.0);

        Assertions.assertEquals(10, existente.getQuantidadeSimulacoes());
        Assertions.assertEquals(12000.0, existente.getMediaValorFinal());

        Mockito.verify(simulacaoDiaRepository, Mockito.never())
                .persist(Mockito.any(SimulacaoDia.class));

    }

    // ============================================================
    // TESTE 3 — registrar(): cria novo registro quando não existe
    // ============================================================
    @Test
    public void testRegistrarCriaNovo() {

        String hoje = LocalDate.now().toString();

        PanacheQuery<SimulacaoDia> queryMock = Mockito.mock(PanacheQuery.class);
        Mockito.when(queryMock.firstResult()).thenReturn(null);

        Mockito.when(
                simulacaoDiaRepository.find(
                        Mockito.anyString(),
                        Mockito.any(Object[].class)
                )
        ).thenReturn(queryMock);

        Mockito.doNothing().when(simulacaoDiaRepository).persist(Mockito.any(SimulacaoDia.class));

        simulacaoDiaService.registrar("Tesouro Selic", 7, 5800.0);

        Mockito.verify(simulacaoDiaRepository)
                .persist(Mockito.any(SimulacaoDia.class));
    }
}
