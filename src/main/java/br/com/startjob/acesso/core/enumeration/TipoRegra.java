package br.com.startjob.acesso.core.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TipoRegra {

    ACESSO_HORARIO("Acesso por horário"),
    ACESSO_PERIODO("Acesso por período"),
    ACESSO_ESCALA("Acesso por turno/escala"),
    ACESSO_CREDITO("Acesso via crédito"),
    ACESSO_UNICO("Acesso único"),
    ACESSO_ESCALA_3_3("Acesso escala 3/3");

    private final String descricao;

    public boolean usaHorarios() {
        return this == ACESSO_HORARIO || this == ACESSO_PERIODO;
    }

    public boolean usaCreditos() {
        return this == ACESSO_CREDITO || this == ACESSO_UNICO;
    }

    public boolean usaPeriodo() {
        return this == ACESSO_PERIODO;
    }

    public boolean usaEscala() {
        return this == ACESSO_ESCALA;
    }

    public boolean usaEscala33() {
        return this == ACESSO_ESCALA_3_3;
    }
}
