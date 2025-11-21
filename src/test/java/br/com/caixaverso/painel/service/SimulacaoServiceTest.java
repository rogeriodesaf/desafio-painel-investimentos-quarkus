package br.com.caixaverso.painel.service;

import br.com.caixaverso.painel.dto.SimulacaoRequestDTO;
import br.com.caixaverso.painel.dto.SimulacaoResponseDTO;
import br.com.caixaverso.painel.model.Produto;
import br.com.caixaverso.painel.model.Simulacao;
import br.com.caixaverso.painel.repository.ProdutoRepository;
import br.com.caixaverso.painel.repository.SimulacaoRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

@QuarkusTest
public class SimulacaoServiceTest {

    @Inject
    SimulacaoService simulacaoService;

    @InjectMock
    ProdutoRepository produtoRepository;

    @InjectMock
    SimulacaoRepository simulacaoRepository;

    @InjectMock
    TelemetriaService telemetriaService;

    @InjectMock
    SimulacaoDiaService simulacaoDiaService;

    // UTILITÁRIO PARA CRIAR PRODUTO DE TESTE
    private Produto criarProduto() {
        Produto p = new Produto();
        p.setId(1L);
        p.setNome("CDB TOP 2026");
        p.setTipo("CDB");
        p.setRentabilidade(0.12);
        p.setRisco("Baixo");
        p.setValorMinimo(500.0);
        p.setValorMaximo(20000.0);
        p.setPrazoMinMeses(6);
        p.setPrazoMaxMeses(36);
        return p;
    }


    // UTILITÁRIO PARA MOCKAR PanacheQuery
    private PanacheQuery<Produto> mockQuery(List<Produto> lista) {
        PanacheQuery<Produto> query = Mockito.mock(PanacheQuery.class);
        Mockito.when(query.list()).thenReturn(lista);
        return query;
    }

    @Test
    public void testSimulacaoComSucesso() {

        Produto produto = criarProduto();

        // 1) Criar um PanacheQuery mockado
        PanacheQuery<Produto> queryProduto = Mockito.mock(PanacheQuery.class);

        // 2) Mockar a lista de produtos retornada
        Mockito.when(queryProduto.list()).thenReturn(List.of(produto));

        // 3) Mockar o find() para retornar o query
        Mockito.when(produtoRepository.find("tipo", "CDB")).thenReturn(queryProduto);

        // Mock: query para buscar simulações do dia
        PanacheQuery<Simulacao> queryMock = Mockito.mock(PanacheQuery.class);
        Mockito.when(queryMock.list()).thenReturn(List.of());

        Mockito.when(
                simulacaoRepository.find(
                        Mockito.anyString(),
                        Mockito.any(Object[].class)
                )
        ).thenReturn(queryMock);

        Mockito.doNothing().when(simulacaoRepository).persist(Mockito.any(Simulacao.class));

        SimulacaoRequestDTO req = new SimulacaoRequestDTO(
                123L,
                10000.0,
                12,
                "CDB"
        );

        SimulacaoResponseDTO resp = simulacaoService.simular(req);

        Assertions.assertNotNull(resp);
        Assertions.assertEquals("CDB TOP 2026", resp.produtoValidado().nome());

        double esperado = 10000.0 * (1 + (0.12 * (12.0 / 12.0)));
        Assertions.assertEquals(esperado, resp.resultadoSimulacao().valorFinal());

        Mockito.verify(simulacaoRepository).persist(Mockito.any(Simulacao.class));
        Mockito.verify(simulacaoDiaService)
                .registrar(Mockito.eq("CDB TOP 2026"), Mockito.anyInt(), Mockito.anyDouble());
        Mockito.verify(telemetriaService)
                .registrar(Mockito.eq("simular-investimento"), Mockito.anyLong());
    }


    @Test
    public void testErroProdutoNaoEncontrado() {

        // Mock correto: find() → PanacheQuery → lista vazia
        PanacheQuery<Produto> queryVazia = Mockito.mock(PanacheQuery.class);
        Mockito.when(queryVazia.list()).thenReturn(List.of());
        Mockito.when(produtoRepository.find("tipo", "CDB")).thenReturn(queryVazia);

        SimulacaoRequestDTO req = new SimulacaoRequestDTO(
                1L, 1000.0, 10, "CDB"
        );

        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> simulacaoService.simular(req)
        );

        Assertions.assertEquals("Nenhum produto encontrado para o tipo: CDB", ex.getMessage());
    }


    // NENHUM PRODUTO ATENDE OS PARÂMETROS
    @Test
    public void testErroNenhumProdutoAtendeParametros() {

        // Mock query vazia (mas não null)
        PanacheQuery<Produto> query = mockQuery(List.of());
        Mockito.when(produtoRepository.find("tipo", "CDB")).thenReturn(query);

        SimulacaoRequestDTO req = new SimulacaoRequestDTO(
                1L,
                1000.0,
                12,
                "CDB"
        );

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            simulacaoService.simular(req);
        });
    }


    // REQUEST NULO
    @Test
    public void testErroRequestNulo() {
        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> simulacaoService.simular(null)
        );
        Assertions.assertEquals("Request de simulação não pode ser nulo.", ex.getMessage());
    }


    // VALOR INVÁLIDO
    @Test
    public void testErroValorZero() {
        SimulacaoRequestDTO req = new SimulacaoRequestDTO(1L, 0, 10, "CDB");

        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> simulacaoService.simular(req)
        );

        Assertions.assertEquals("valor deve ser maior que zero.", ex.getMessage());
    }
}
