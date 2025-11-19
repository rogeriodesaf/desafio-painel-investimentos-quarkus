package br.com.caixaverso.painel.resource;

import br.com.caixaverso.painel.dto.ProdutoResponseDTO;
import br.com.caixaverso.painel.service.ProdutoService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/produtos-recomendados")
@Tag(name = "Produtos Recomendados")
@Produces(MediaType.APPLICATION_JSON)
public class ProdutoResource {

    @Inject
    ProdutoService produtoService;

    @GET
    @Path("/{perfil}")
    @RolesAllowed({"user", "admin"})
    @Operation(summary = "Retorna produtos recomendados com base no perfil de risco informado")
    public List<ProdutoResponseDTO> recomendar(@PathParam("perfil") String perfil) {
        return produtoService.recomendarPorPerfil(perfil);
    }
}
