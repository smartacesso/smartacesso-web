package br.com.startjob.acesso.entrypoint.api.dto;

import br.com.startjob.acesso.core.domain.permissao.WebPermissaoLinha;

public record WebPermissaoLinhaResponse(
        String codigo,
        String descricao,
        boolean administrador,
        boolean gerente,
        boolean operador,
        boolean porteiro,
        boolean cuidador,
        boolean responsavel
) {
    public static WebPermissaoLinhaResponse fromDomain(WebPermissaoLinha domain) {
        return new WebPermissaoLinhaResponse(
                domain.getCodigo(),
                domain.getDescricao(),
                domain.isAdministrador(),
                domain.isGerente(),
                domain.isOperador(),
                domain.isPorteiro(),
                domain.isCuidador(),
                domain.isResponsavel()
        );
    }
}
