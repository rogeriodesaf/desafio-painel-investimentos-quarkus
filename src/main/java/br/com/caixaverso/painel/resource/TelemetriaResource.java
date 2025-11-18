package br.com.caixaverso.painel.resource;

import br.com.caixaverso.painel.dto.TelemetriaResponse;
import br.com.caixaverso.painel.service.TelemetriaService;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/telemetria")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Telemetria", description = "Métricas de desempenho dos serviços da API")
public class TelemetriaResource {

    @Inject
    TelemetriaService telemetriaService;

    @GET
    @RolesAllowed("admin")
    @Operation(
            summary = "Retorna métricas por serviço",
            description = "Retorna quantidade de chamadas e tempo médio de resposta para cada serviço, além do período analisado."
    )
    public TelemetriaResponse listarTelemetria() {
        return telemetriaService.montarResposta();
    }
}
