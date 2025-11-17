package br.com.caixaverso.painel.dto;

import java.time.OffsetDateTime;

public record ErrorResponse(
        int status,
        String error,
        String message,
        String path,
        String timestamp
) {

    public static ErrorResponse of(int status, String error, String message, String path) {
        String ts = OffsetDateTime.now().toString();
        return new ErrorResponse(status, error, message, path, ts);
    }
}
