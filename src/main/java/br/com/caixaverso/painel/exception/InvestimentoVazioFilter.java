package br.com.caixaverso.painel.exception;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

import java.util.List;

@Provider
public class InvestimentoVazioFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) {

        String path = request.getUriInfo().getPath();

        // Normaliza o path para evitar falhas
        path = path.replaceAll("^/+", "").replaceAll("/+$", "");

        // Agora verificamos se a rota é do endpoint de investimentos
        if (!path.matches("investimentos(/.*)?")) {
            return;
        }

        Object entity = response.getEntity();

        // Verifica se é lista e se está vazia
        if (entity instanceof List<?> list && list.isEmpty()) {
            response.getHeaders().add("X-Info",
                    "Cliente existe, mas não possui investimentos registrados.");
        }
    }
}
