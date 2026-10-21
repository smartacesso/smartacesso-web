package br.com.startjob.acesso.entrypoint.api.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;

public record AlterarLoginSenhaRequest(
        @JsonAlias({"novoLogin", "login"})
        @NotBlank(message = "Login é obrigatório")
        String login,

        @NotBlank(message = "Senha é obrigatória")
        String senha,

        @JsonAlias({"confirmarSenha", "confirmacaoSenha"})
        @NotBlank(message = "Confirmação de senha é obrigatória")
        String confirmacaoSenha
) {
}
