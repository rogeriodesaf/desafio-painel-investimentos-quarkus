package br.com.caixaverso.painel.resource;

import br.com.caixaverso.painel.dto.PerfilRiscoResponseDTO;
import br.com.caixaverso.painel.service.PerfilRiscoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/")
@Tag(name = "Perfil de Risco")
@Produces(MediaType.APPLICATION_JSON)
public class PerfilRiscoResource {

    @Inject
    PerfilRiscoService perfilRiscoService;

    @GET
    @Path("/perfil-risco/{clienteId}")
    @Operation(summary = "Obtém o perfil de risco do cliente com base em volume, frequência e preferência")
    public PerfilRiscoResponseDTO obterPerfilRisco(@PathParam("clienteId") Long clienteId) {
        return perfilRiscoService.calcularPerfil(clienteId);
    }
}
