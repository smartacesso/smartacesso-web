package br.com.startjob.acesso.entrypoint.api.dto;

import br.com.startjob.acesso.core.domain.parametro.SecaoParametroGrupo;

import java.util.List;

public record SecaoParametroResponse(
        String secao,
        String titulo,
        List<ParametroItemResponse> parametros
) {
    public static SecaoParametroResponse fromDomain(SecaoParametroGrupo domain) {
        List<ParametroItemResponse> itens = domain.getParametros() == null
                ? List.of()
                : domain.getParametros().stream()
                        .map(ParametroItemResponse::fromDomain)
                        .toList();

        return new SecaoParametroResponse(
                domain.getSecao() != null ? domain.getSecao().name() : null,
                domain.getTitulo(),
                itens
        );
    }
}
