package br.com.startjob.acesso.entrypoint.api.dto;

import jakarta.validation.constraints.NotBlank;

public record WebLoginRequest(
        @NotBlank(message = "Unidade organizacional é obrigatória")
        String unidadeOrganizacional,

        @NotBlank(message = "Usuário é obrigatório")
        String usuario,

        @NotBlank(message = "Senha é obrigatória")
        String senha
) {}
