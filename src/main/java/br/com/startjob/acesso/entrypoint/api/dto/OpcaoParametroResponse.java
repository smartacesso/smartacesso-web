package br.com.startjob.acesso.entrypoint.api.dto;

import br.com.startjob.acesso.core.domain.parametro.OpcaoParametro;

public record OpcaoParametroResponse(
        String valor,
        String rotulo
) {
    public static OpcaoParametroResponse fromDomain(OpcaoParametro domain) {
        if (domain == null) {
            return null;
        }
        return new OpcaoParametroResponse(domain.valor(), domain.rotulo());
    }
}
