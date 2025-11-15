package br.com.caixaverso.painel.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/status")
public class StatusResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String status() {
        return "API de Investimentos Quarkus está no ar 🚀";
    }
}
