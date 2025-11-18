package br.com.caixaverso.painel.resource;

import io.smallrye.jwt.build.Jwt;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Set;
import java.util.Objects;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    @POST
    @Path("/login")
    public Response login(LoginRequest request) {



        // Validação básica do request
        if (request == null || isBlank(request.username()) || isBlank(request.password())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("username and password are required"))
                    .build();
        }

        String username = request.username();
        String password = request.password();

        String role;

        // USUÁRIO ADMIN
        if ("admin".equals(username) && "123".equals(password)) {
            role = "admin";
        }
        // USUÁRIO COMUM
        else if ("caixa".equals(username) && "123".equals(password)) {
            role = "user";
        }
        else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        String token = Jwt.issuer("caixa-investimentos")
                .upn(username)
                .groups(Set.of(role))
                .expiresAt(System.currentTimeMillis() / 1000 + 3600)
                .sign();

        return Response.ok(new TokenResponse(token)).build();
    }

    // utilitários simples
    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}

record LoginRequest(String username, String password) {}
record TokenResponse(String token) {}
record ErrorResponse(String message) {}

