package br.com.caixaverso.painel.service;

import br.com.caixaverso.painel.dto.InvestimentoResponseDTO;
import br.com.caixaverso.painel.model.Cliente;
import br.com.caixaverso.painel.model.Investimento;
import br.com.caixaverso.painel.repository.ClienteRepository;
import br.com.caixaverso.painel.repository.InvestimentoRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

@QuarkusTest
public class InvestimentoServiceTest {

    @Inject
    InvestimentoService investimentoService;

    @InjectMock
    InvestimentoRepository investimentoRepository;

    @InjectMock
    ClienteRepository clienteRepository;

    @InjectMock
    TelemetriaService telemetriaService;

//utilitário
    private Cliente criarCliente(Long id) {
        Cliente c = new Cliente();
        c.setId(id);
        c.setNome("Cliente Teste");
        return c;
    }


    private Investimento criarInvest(Long id, String tipo, Double valor, Double rentabilidade, String data) {
        Investimento i = new Investimento();
        i.setId(id);
        i.setTipo(tipo);
        i.setValor(valor);
        i.setRentabilidade(rentabilidade);
        i.setData(data);
        return i;
    }


    // TESTE 1 — clienteId nulo
    @Test
    public void testClienteIdNulo() {

        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> investimentoService.listarPorCliente(null)
        );

        Assertions.assertEquals("clienteId é obrigatório.", ex.getMessage());
    }


    // TESTE 2 — cliente não encontrado
    @Test
    public void testClienteNaoEncontrado() {

        Mockito.when(clienteRepository.findById(99L)).thenReturn(null);

        NotFoundException ex = Assertions.assertThrows(
                NotFoundException.class,
                () -> investimentoService.listarPorCliente(99L)
        );

        Assertions.assertEquals("Cliente não encontrado para o id: 99", ex.getMessage());
    }


    // TESTE 3 — cliente encontrado, mas sem investimentos
    @Test
    public void testSemInvestimentos() {

        Cliente cliente = criarCliente(1L);
        Mockito.when(clienteRepository.findById(1L)).thenReturn(cliente);

        // Mock de PanacheQuery
        PanacheQuery<Investimento> queryMock = Mockito.mock(PanacheQuery.class);
        Mockito.when(queryMock.list()).thenReturn(List.of());

        Mockito.when(
                investimentoRepository.find(Mockito.eq("clienteId"), Mockito.any(Object[].class))
        ).thenReturn(queryMock);

        List<InvestimentoResponseDTO> resp = investimentoService.listarPorCliente(1L);

        Assertions.assertTrue(resp.isEmpty());

        Mockito.verify(telemetriaService).registrar(Mockito.eq("investimentos"), Mockito.anyLong());
    }


    // TESTE 4 — cliente encontrado com investimentos
    @Test
    public void testComInvestimentos() {

        Cliente cliente = criarCliente(2L);
        Mockito.when(clienteRepository.findById(2L)).thenReturn(cliente);

        List<Investimento> lista = List.of(
                criarInvest(10L, "CDB", 5000.0, 0.12, "2025-03-10"),
                criarInvest(11L, "Fundo", 3000.0, 0.08, "2025-02-05")
        );

        // PanacheQuery mockado corretamente
        PanacheQuery<Investimento> queryMock = Mockito.mock(PanacheQuery.class);
        Mockito.when(queryMock.list()).thenReturn(lista);

        Mockito.when(
                investimentoRepository.find(Mockito.eq("clienteId"), Mockito.any(Object[].class))
        ).thenReturn(queryMock);

        List<InvestimentoResponseDTO> resp = investimentoService.listarPorCliente(2L);

        Assertions.assertEquals(2, resp.size());
        Assertions.assertEquals("CDB", resp.get(0).tipo());
        Assertions.assertEquals(5000.0, resp.get(0).valor());
        Assertions.assertEquals(0.12, resp.get(0).rentabilidade());
        Assertions.assertEquals("2025-03-10", resp.get(0).data());

        Mockito.verify(telemetriaService).registrar(Mockito.eq("investimentos"), Mockito.anyLong());
    }
}
