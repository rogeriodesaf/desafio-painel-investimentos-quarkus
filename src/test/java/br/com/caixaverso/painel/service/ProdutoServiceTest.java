package br.com.caixaverso.painel.service;

import br.com.caixaverso.painel.dto.ProdutoResponseDTO;
import br.com.caixaverso.painel.model.Produto;
import br.com.caixaverso.painel.repository.ProdutoRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

@QuarkusTest
public class ProdutoServiceTest {

    @Inject
    ProdutoService produtoService;

    @InjectMock
    ProdutoRepository produtoRepository;

    @InjectMock
    TelemetriaService telemetriaService;

    private Produto prod(Long id, String nome, String tipo, Double rent, String risco) {
        Produto p = new Produto();
        p.setId(id);
        p.setNome(nome);
        p.setTipo(tipo);
        p.setRentabilidade(rent);
        p.setRisco(risco);
        return p;
    }

    @Test
    public void testPerfilConservador() {

        List<Produto> mockLista = Arrays.asList(
                prod(1L, "CDB Liquidez Diária", "CDB", 0.10, "Baixo"),
                prod(2L, "Tesouro Selic", "Tesouro", 0.09, "Baixo"),
                prod(3L, "Fundo Agressivo XPTO", "Fundo", 0.20, "Alto")
        );

        Mockito.when(produtoRepository.listAll()).thenReturn(mockLista);

        List<ProdutoResponseDTO> resposta = produtoService.recomendarPorPerfil("conservador");

        Assertions.assertEquals(2, resposta.size());
        Assertions.assertTrue(
                resposta.stream().allMatch(p -> p.risco().equalsIgnoreCase("Baixo"))
        );

        Mockito.verify(telemetriaService).registrar(Mockito.eq("produtos-recomendados"), Mockito.anyLong());
    }

    @Test
    public void testPerfilModerado() {

        List<Produto> mockLista = Arrays.asList(
                prod(1L, "CDB Baixo Risco", "CDB", 0.10, "Baixo"),
                prod(2L, "Fundo Moderado A", "Fundo", 0.13, "Médio"),
                prod(3L, "Fundo Agressivo XPTO", "Fundo", 0.20, "Alto")
        );

        Mockito.when(produtoRepository.listAll()).thenReturn(mockLista);

        List<ProdutoResponseDTO> resposta = produtoService.recomendarPorPerfil("Moderado");

        Assertions.assertEquals(2, resposta.size());

        Assertions.assertTrue(
                resposta.stream().noneMatch(p -> p.risco().equalsIgnoreCase("Alto"))
        );

        Mockito.verify(telemetriaService).registrar(Mockito.eq("produtos-recomendados"), Mockito.anyLong());
    }

    @Test
    public void testPerfilAgressivo() {

        List<Produto> mockLista = Arrays.asList(
                prod(1L, "CDB Conservador", "CDB", 0.09, "Baixo"),
                prod(2L, "Fundo Médio XPTO", "Fundo", 0.12, "Médio"),
                prod(3L, "Fundo Agressivo TOP", "Fundo", 0.22, "Alto")
        );

        Mockito.when(produtoRepository.listAll()).thenReturn(mockLista);

        List<ProdutoResponseDTO> resposta = produtoService.recomendarPorPerfil("AGRESSIVO");

        Assertions.assertEquals(1, resposta.size());
        Assertions.assertEquals("Fundo Agressivo TOP", resposta.get(0).nome());

        Mockito.verify(telemetriaService).registrar(Mockito.eq("produtos-recomendados"), Mockito.anyLong());
    }

    @Test
    public void testPerfilInvalido() {

        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> produtoService.recomendarPorPerfil("teste")
        );

        Assertions.assertEquals("Perfil inválido. Use Conservador, Moderado ou Agressivo.", ex.getMessage());
    }

    @Test
    public void testPerfilNulo() {

        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> produtoService.recomendarPorPerfil(null)
        );

        Assertions.assertEquals("perfil é obrigatório.", ex.getMessage());
    }
}
