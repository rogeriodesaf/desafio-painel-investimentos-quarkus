package br.com.caixaverso.painel.resource;

import br.com.caixaverso.painel.dto.TelemetriaResponse;
import br.com.caixaverso.painel.model.Telemetria;
import br.com.caixaverso.painel.service.TelemetriaService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.time.LocalDate;
import java.util.List;

@Path("/telemetria")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Telemetria")
public class TelemetriaResource {

    @Inject
    TelemetriaService telemetriaService;

    @GET
    @RolesAllowed("admin")
    @Operation(summary = "Lista dados de telemetria por serviço")
    public TelemetriaResponse listarTelemetria() {
        List<Telemetria> registros = telemetriaService.listarTudo();

        List<TelemetriaResponse.Servico> servicos = registros.stream()
                .map(t -> new TelemetriaResponse.Servico(
                        t.getServico(),
                        t.getQuantidadeChamadas(),
                        t.getMediaTempoRespostaMs()
                ))
                .toList();

        String inicio = LocalDate.now().minusDays(30).toString();
        String fim = LocalDate.now().toString();
        TelemetriaResponse.Periodo periodo = new TelemetriaResponse.Periodo(inicio, fim);

        return new TelemetriaResponse(servicos, periodo);
    }
}
