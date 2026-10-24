package br.com.startjob.acesso.core.domain.pedestre;

import br.com.startjob.acesso.core.domain.BaseDomain;
import br.com.startjob.acesso.core.domain.cliente.Cliente;
import br.com.startjob.acesso.core.domain.empresa.Cargo;
import br.com.startjob.acesso.core.domain.empresa.CentroCusto;
import br.com.startjob.acesso.core.domain.empresa.Departamento;
import br.com.startjob.acesso.core.domain.empresa.Empresa;
import br.com.startjob.acesso.core.domain.endereco.Endereco;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.Genero;
import br.com.startjob.acesso.core.enumeration.PerfilAcessoApp;
import br.com.startjob.acesso.core.enumeration.Status;
import br.com.startjob.acesso.core.enumeration.TipoPedestre;
import br.com.startjob.acesso.core.enumeration.TipoQRCode;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@ToString(exclude = {"foto", "responsaveis"})
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Setter
@Getter
public class Pedestre extends BaseDomain {

    private Long id;
    private String nome;
    private Status status;
    private String email;
    private String cpf;
    private String rg;
    private String telefone;
    private String celular;
    private String matricula;
    private String codigoCartaoAcesso;
    private String observacoes;
    private TipoQRCode tipoQRCode;
    private String qrCodeParaAcesso;
    private Boolean sempreLiberado;
    private Boolean acessoLivre;
    private Boolean habilitarTeclado;
    private Boolean cadastroFacialObrigatorio;
    private byte[] foto;
    private LocalDateTime dataAlteracaoFoto;
    private LocalDate dataNascimento;
    private TipoPedestre tipo;
    private Genero genero;
    private Boolean enviaSmsAoPassarNaCatraca;
    private String uuidLocal;
    private Endereco endereco;
    private Empresa empresa;
    private Departamento departamento;
    private CentroCusto centroCusto;
    private Cargo cargo;
    private String empresaVisitada;
    private Empresa empresaVisitadaRef;
    private String login;
    private String senha;
    private PerfilAcessoApp perfilApp;
    private Cliente cliente;
    private Usuario usuario;
    private String justificativa;
    private LocalDateTime dataInicioPeriodoAgendamento;
    private LocalDateTime dataFimPeriodoAgendamento;
    private Boolean agendamentoLiberado;
    private Integer qtdAcessoAntesSinc;
    private List<PedestreRegra> regras;
    private List<PedestreEquipamento> equipamentos;
    private List<Documento> documentos;
    private List<Biometria> biometrias;
    private List<MensagemEquipamento> mensagensPersonalizadas;
    private List<HistoricoCota> cotas;
    private List<Pedestre> responsaveis;
    private List<Responsavel> responsaveisEntidade;

    public boolean isVisitante() {
        return TipoPedestre.VISITANTE.equals(this.tipo);
    }

    public boolean isPedestre() {
        return TipoPedestre.PEDESTRE.equals(this.tipo);
    }
}
