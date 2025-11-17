package br.com.caixaverso.painel.resource;

import br.com.caixaverso.painel.dto.SimulacaoRequestDTO;
import br.com.caixaverso.painel.dto.SimulacaoResponseDTO;
import br.com.caixaverso.painel.model.Simulacao;
import br.com.caixaverso.painel.service.SimulacaoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
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

    @POST
    @Path("/simular-investimento")
    @Operation(summary = "Simula um investimento com base nos parâmetros informados")
    public SimulacaoResponseDTO simularInvestimento(SimulacaoRequestDTO request) {
        return simulacaoService.simular(request);
    }

    @GET
    @Path("/simulacoes")
    @Operation(summary = "Retorna todas as simulações realizadas")
    public List<Simulacao> listarTodas() {
        return simulacaoService.listarTodas();
    }
}
