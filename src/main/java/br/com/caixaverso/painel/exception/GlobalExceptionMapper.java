package br.com.caixaverso.painel.exception;

import br.com.caixaverso.painel.dto.ErrorResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
@ApplicationScoped
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(Throwable exception) {
        int status;
        String error;
        String message = exception.getMessage();

        if (exception instanceof IllegalArgumentException) {
            status = Response.Status.BAD_REQUEST.getStatusCode();
            error = "Bad Request";
            if (message == null || message.isBlank()) {
                message = "Requisição inválida.";
            }
        } else if (exception instanceof NullPointerException) {
            status = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
            error = "Internal Server Error";
            message = (message == null || message.isBlank())
                    ? "Valor nulo inesperado durante o processamento."
                    : message;
        } else if (exception instanceof WebApplicationException webEx) {
            status = webEx.getResponse().getStatus();
            Response.Status statusEnum = Response.Status.fromStatusCode(status);
            error = statusEnum != null ? statusEnum.getReasonPhrase() : "HTTP " + status;
            if (message == null || message.isBlank()) {
                message = error;
            }
        } else {
            status = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
            error = "Internal Server Error";
            if (message == null || message.isBlank()) {
                message = "Erro interno inesperado.";
            }
        }

        String path = uriInfo != null ? uriInfo.getPath() : null;
        ErrorResponse body = ErrorResponse.of(status, error, message, path);

        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(body)
                .build();
    }
}
