package br.com.caixaverso.painel.resource;

import br.com.caixaverso.painel.dto.SimulacaoPorProdutoDiaDTO;
import br.com.caixaverso.painel.dto.SimulacaoRequestDTO;
import br.com.caixaverso.painel.dto.SimulacaoResponseDTO;
import br.com.caixaverso.painel.model.Simulacao;
import br.com.caixaverso.painel.service.SimulacaoDiaService;
import br.com.caixaverso.painel.service.SimulacaoService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/")
@Tag(name = "Simulações")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SimulacaoResource {

    @Inject
    SimulacaoService simulacaoService;

    @Inject
    SimulacaoDiaService simulacaoDiaService;

    @POST
    @Path("/simular-investimento")
    @RolesAllowed({"user", "admin"})
    @Operation(summary = "Simula um investimento com base nos parâmetros informados")
    public SimulacaoResponseDTO simularInvestimento(SimulacaoRequestDTO request) {
        return simulacaoService.simular(request);
    }

    @GET
    @Path("/simulacoes")
    @RolesAllowed({"user", "admin"})
    @Operation(summary = "Retorna todas as simulações realizadas")
    public List<Simulacao> listarTodas() {
        return simulacaoService.listarTodas();
    }

    @GET
    @Path("/simulacoes/por-produto-dia")
    @RolesAllowed({"user", "admin"})
    @Operation(summary = "Retorna valores simulados agregados por produto e dia")
    public List<SimulacaoPorProdutoDiaDTO> listarPorProdutoDia() {
        return simulacaoDiaService.listarTudo();
    }
}
