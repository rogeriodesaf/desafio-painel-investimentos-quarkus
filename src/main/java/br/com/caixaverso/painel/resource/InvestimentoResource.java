package br.com.caixaverso.painel.resource;

import br.com.caixaverso.painel.dto.InvestimentoResponseDTO;
import br.com.caixaverso.painel.service.InvestimentoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/investimentos")
@Tag(name = "Investimentos")
@Produces(MediaType.APPLICATION_JSON)
public class InvestimentoResource {

    @Inject
    InvestimentoService investimentoService;

    @GET
    @Path("/{clienteId}")
    @Operation(summary = "Retorna o histórico de investimentos de um cliente")
    public List<InvestimentoResponseDTO> listarPorCliente(@PathParam("clienteId") Long clienteId) {
        return investimentoService.listarPorCliente(clienteId);
    }
}
