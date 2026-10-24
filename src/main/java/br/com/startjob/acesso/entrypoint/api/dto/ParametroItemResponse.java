package br.com.startjob.acesso.entrypoint.api.dto;

import br.com.startjob.acesso.core.domain.parametro.ParametroItem;
import br.com.startjob.acesso.core.enumeration.TipoDadoParametro;

import java.util.List;

public record ParametroItemResponse(
        String codigo,
        String nome,
        String rotulo,
        TipoDadoParametro tipo,
        Object valor,
        Object valorPadrao,
        Integer min,
        Integer max,
        List<OpcaoParametroResponse> opcoes
) {
    public static ParametroItemResponse fromDomain(ParametroItem domain) {
        List<OpcaoParametroResponse> opcoesResponse = domain.getOpcoes() == null
                ? null
                : domain.getOpcoes().stream()
                        .map(OpcaoParametroResponse::fromDomain)
                        .toList();

        return new ParametroItemResponse(
                domain.getCodigo(),
                domain.getNome(),
                domain.getRotulo(),
                domain.getTipo(),
                domain.getValor(),
                domain.getValorPadrao(),
                domain.getMin(),
                domain.getMax(),
                opcoesResponse
        );
    }
}
