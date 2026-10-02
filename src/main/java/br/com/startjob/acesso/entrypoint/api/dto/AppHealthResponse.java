package br.com.startjob.acesso.entrypoint.api.dto;

import lombok.Builder;

@Builder
public record AppHealthResponse(
        String status,
        boolean jwtConfigured,
        boolean firebasePathConfigured,
        boolean firebaseFileExists,
        boolean firebaseReady) {
}
