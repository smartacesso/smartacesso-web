package br.com.startjob.acesso.core.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Dedo {

    RIGHT_INDEX("Indicador direito"),
    RIGHT_THUMB("Polegar direito"),
    RIGHT_MIDDLE("Médio direito"),
    RIGHT_RING("Anelar direito"),
    RIGHT_LITTLE("Mínimo direito"),
    LEFT_INDEX("Indicador esquerdo"),
    LEFT_THUMB("Polegar esquerdo"),
    LEFT_MIDDLE("Médio esquerdo"),
    LEFT_RING("Anelar esquerdo"),
    LEFT_LITTLE("Mínimo esquerdo");

    private final String descricao;
}
