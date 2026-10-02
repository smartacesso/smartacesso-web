package br.com.startjob.acesso.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
public class BaseDomain {

    private Boolean removido;
    private LocalDateTime dataRemovido;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAlteracao;
    private Integer versao;
    private UUID uniqueId;

}
