package br.com.startjob.acesso.core.domain.correspondencia;

import br.com.startjob.acesso.core.domain.BaseDomain;
import br.com.startjob.acesso.core.domain.cliente.Cliente;
import br.com.startjob.acesso.core.domain.pedestre.Pedestre;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@ToString(exclude = {"destinatario", "usuarioRecebeu", "cliente", "fotoVolume"})
@EqualsAndHashCode(callSuper = true, exclude = {"destinatario", "usuarioRecebeu", "cliente", "fotoVolume"})
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Setter
@Getter
public class Correspondencia extends BaseDomain {

    private Long id;
    private Pedestre destinatario;
    private Usuario usuarioRecebeu;
    private String tipo;
    private String codigoRastreio;
    private Long idArquivoFoto;
    private LocalDateTime dataRecebimento;

    @Builder.Default
    private String confirmaRetirada = "N";

    private LocalDateTime dataRetirada;
    private String observacao;
    private byte[] fotoVolume;
    private String extensaoFoto;
    private Cliente cliente;
    private String nomeQuemRetirou;
    private String documentoQuemRetirou;
}
