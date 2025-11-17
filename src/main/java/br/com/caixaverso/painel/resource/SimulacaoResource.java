package br.com.caixaverso.painel.resource;

import br.com.caixaverso.painel.dto.SimulacaoRequestDTO;
import br.com.caixaverso.painel.dto.SimulacaoResponseDTO;
import br.com.caixaverso.painel.model.Simulacao;
import br.com.caixaverso.painel.service.SimulacaoDiaService;
import br.com.caixaverso.painel.service.SimulacaoService;
import br.com.caixaverso.painel.service.TelemetriaService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * ============================================================================
 *  RESOURCE: SimulacaoResource
 * ============================================================================
 *
 * Endpoints ESPECIFICADOS PELO DESAFIO:
 *
 *  ✔ POST /simular-investimento
 *      → recebe JSON com clienteId, valor, prazoMeses e tipoProduto
 *      → retorna envelope JSON com produtoValidado e resultado da simulação
 *
 *  ✔ GET /simulacoes
 *      → retorna histórico de todas as simulações persistidas
 *
 *  ✔ GET /simulacoes/por-produto-dia
 *      → retorna agregação diária por produto (quantidade e média)
 *
 * Cada chamada também registra telemetria (quantidade e tempo).
 *
 * ============================================================================
 */
@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SimulacaoResource {

    @Inject
    SimulacaoService simulacaoService;

    @Inject
    SimulacaoDiaService simulacaoDiaService;

    @Inject
    TelemetriaService telemetriaService;

    /**
     * ------------------------------------------------------------------------
     *  POST /simular-investimento
     *
     *  Fluxo exigido pelo desafio:
     *      1. Receber JSON com dados da simulação
     *      2. Chamar SimulacaoService para executar a regra de negócio
     *      3. Registrar tempo de resposta na tabela de telemetria
     *      4. Retornar SimulacaoResponseDTO conforme o PDF
     * ------------------------------------------------------------------------
     */
    @POST
    @Path("/simular-investimento")
    public Response simularInvestimento(SimulacaoRequestDTO request) {

        long inicio = System.currentTimeMillis();

        try {
            SimulacaoResponseDTO resposta = simulacaoService.simular(request);

            long fim = System.currentTimeMillis();
            telemetriaService.registrar("simular-investimento", fim - inicio);

            return Response.ok(resposta).build();

        } catch (IllegalArgumentException e) {

            long fim = System.currentTimeMillis();
            telemetriaService.registrar("simular-investimento", fim - inicio);

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    /**
     * ------------------------------------------------------------------------
     *  GET /simulacoes
     *
     *  Retorna todas as simulações persistidas no banco.
     * ------------------------------------------------------------------------
     */
    @GET
    @Path("/simulacoes")
    public Response listarTodas() {

        long inicio = System.currentTimeMillis();

        List<Simulacao> lista = simulacaoService.listarTodas();

        long fim = System.currentTimeMillis();
        telemetriaService.registrar("listar-simulacoes", fim - inicio);

        return Response.ok(lista).build();
    }

    /**
     * ------------------------------------------------------------------------
     *  GET /simulacoes/por-produto-dia
     *
     *  Retorna dados agregados conforme o PDF:
     *
     *      [
     *        {
     *          "produto": "CDB Caixa 2026",
     *          "data": "2025-10-30",
     *          "quantidadeSimulacoes": 15,
     *          "mediaValorFinal": 11050.00
     *        }
     *      ]
     * ------------------------------------------------------------------------
     */
    @GET
    @Path("/simulacoes/por-produto-dia")
    public Response listarPorProdutoDia() {

        long inicio = System.currentTimeMillis();

        var lista = simulacaoDiaService.listarTudo();

        long fim = System.currentTimeMillis();
        telemetriaService.registrar("simulacoes-por-produto-dia", fim - inicio);

        return Response.ok(lista).build();
    }
}
