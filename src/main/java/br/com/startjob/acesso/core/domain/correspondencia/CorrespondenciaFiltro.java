package br.com.startjob.acesso.core.domain.correspondencia;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class CorrespondenciaFiltro {

    private String nomeDestinatario;
    private String codigoRastreio;
    private String tipo;
    private String confirmaRetirada;
    private LocalDate dataRecebimento;
}
