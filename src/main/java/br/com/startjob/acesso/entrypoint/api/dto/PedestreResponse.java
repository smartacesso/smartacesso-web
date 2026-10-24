package br.com.startjob.acesso.entrypoint.api.dto;

import br.com.startjob.acesso.core.domain.pedestre.Pedestre;
import br.com.startjob.acesso.core.enumeration.Genero;
import br.com.startjob.acesso.core.enumeration.PerfilAcessoApp;
import br.com.startjob.acesso.core.enumeration.Status;
import br.com.startjob.acesso.core.enumeration.TipoPedestre;
import br.com.startjob.acesso.core.enumeration.TipoQRCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Builder
public record PedestreResponse(
        Long id,
        String nome,
        Status status,
        TipoPedestre tipo,
        String email,
        String cpf,
        String rg,
        String telefone,
        String celular,
        String matricula,
        String codigoCartaoAcesso,
        String observacoes,
        TipoQRCode tipoQRCode,
        String qrCodeParaAcesso,
        LocalDate dataNascimento,
        Genero genero,
        String generoDescricao,
        Boolean sempreLiberado,
        Boolean habilitarTeclado,
        Boolean cadastroFacialObrigatorio,
        Boolean enviaSmsAoPassarNaCatraca,
        String uuidLocal,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String fotoBase64,
        Long idEmpresa,
        String nomeEmpresa,
        Long idDepartamento,
        String nomeDepartamento,
        Long idCentroCusto,
        String nomeCentroCusto,
        Long idCargo,
        String nomeCargo,
        String empresaVisitada,
        Long idEmpresaVisitada,
        String nomeEmpresaVisitada,
        String login,
        PerfilAcessoApp perfilApp,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        EnderecoResponse endereco,
        List<PedestreRegraResponse> regras,
        List<PedestreEquipamentoResponse> equipamentos,
        List<DocumentoResponse> documentos,
        List<BiometriaResponse> biometrias,
        List<MensagemEquipamentoResponse> mensagensPersonalizadas,
        List<HistoricoCotaResponse> cotas,
        List<ResponsavelResumoResponse> responsaveis,
        List<ResponsavelResumoResponse> responsaveisEntidade
) {
    public static PedestreResponse fromDomain(Pedestre domain) {
        if (domain == null) {
            return null;
        }

        List<PedestreRegraResponse> regras = domain.getRegras() == null
                ? Collections.emptyList()
                : domain.getRegras().stream()
                        .filter(r -> r.getRemovido() == null || !r.getRemovido())
                        .map(PedestreRegraResponse::fromDomain)
                        .toList();

        List<PedestreEquipamentoResponse> equipamentos = domain.getEquipamentos() == null
                ? Collections.emptyList()
                : domain.getEquipamentos().stream()
                        .filter(e -> e.getRemovido() == null || !e.getRemovido())
                        .map(PedestreEquipamentoResponse::fromDomain)
                        .toList();

        List<DocumentoResponse> documentos = domain.getDocumentos() == null
                ? Collections.emptyList()
                : domain.getDocumentos().stream()
                        .filter(d -> d.getRemovido() == null || !d.getRemovido())
                        .map(DocumentoResponse::fromDomain)
                        .toList();

        List<BiometriaResponse> biometrias = domain.getBiometrias() == null
                ? Collections.emptyList()
                : domain.getBiometrias().stream()
                        .filter(b -> b.getRemovido() == null || !b.getRemovido())
                        .map(BiometriaResponse::fromDomain)
                        .toList();

        List<MensagemEquipamentoResponse> mensagensPersonalizadas = domain.getMensagensPersonalizadas() == null
                ? Collections.emptyList()
                : domain.getMensagensPersonalizadas().stream()
                        .filter(m -> m.getRemovido() == null || !m.getRemovido())
                        .map(MensagemEquipamentoResponse::fromDomain)
                        .toList();

        List<HistoricoCotaResponse> cotas = domain.getCotas() == null
                ? Collections.emptyList()
                : domain.getCotas().stream()
                        .filter(c -> c.getRemovido() == null || !c.getRemovido())
                        .map(HistoricoCotaResponse::fromDomain)
                        .toList();

        List<ResponsavelResumoResponse> responsaveis = domain.getResponsaveis() == null
                ? Collections.emptyList()
                : domain.getResponsaveis().stream()
                        .map(ResponsavelResumoResponse::fromPedestre)
                        .toList();

        List<ResponsavelResumoResponse> responsaveisEntidade = domain.getResponsaveisEntidade() == null
                ? Collections.emptyList()
                : domain.getResponsaveisEntidade().stream()
                        .map(ResponsavelResumoResponse::fromResponsavel)
                        .toList();

        String fotoBase64 = (domain.getFoto() != null && domain.getFoto().length > 0)
                ? Base64.getEncoder().encodeToString(domain.getFoto())
                : null;

        return PedestreResponse.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .status(domain.getStatus())
                .tipo(domain.getTipo())
                .email(domain.getEmail())
                .cpf(domain.getCpf())
                .rg(domain.getRg())
                .telefone(domain.getTelefone())
                .celular(domain.getCelular())
                .matricula(domain.getMatricula())
                .codigoCartaoAcesso(domain.getCodigoCartaoAcesso())
                .observacoes(domain.getObservacoes())
                .tipoQRCode(domain.getTipoQRCode())
                .qrCodeParaAcesso(domain.getQrCodeParaAcesso())
                .dataNascimento(domain.getDataNascimento())
                .genero(domain.getGenero())
                .generoDescricao(domain.getGenero() != null ? domain.getGenero().getDescricao() : null)
                .sempreLiberado(domain.getSempreLiberado())
                .habilitarTeclado(domain.getHabilitarTeclado())
                .cadastroFacialObrigatorio(domain.getCadastroFacialObrigatorio())
                .enviaSmsAoPassarNaCatraca(domain.getEnviaSmsAoPassarNaCatraca())
                .uuidLocal(domain.getUuidLocal())
                .fotoBase64(fotoBase64)
                .idEmpresa(domain.getEmpresa() != null ? domain.getEmpresa().getId() : null)
                .nomeEmpresa(domain.getEmpresa() != null ? domain.getEmpresa().getNome() : null)
                .idDepartamento(domain.getDepartamento() != null ? domain.getDepartamento().getId() : null)
                .nomeDepartamento(domain.getDepartamento() != null ? domain.getDepartamento().getNome() : null)
                .idCentroCusto(domain.getCentroCusto() != null ? domain.getCentroCusto().getId() : null)
                .nomeCentroCusto(domain.getCentroCusto() != null ? domain.getCentroCusto().getNome() : null)
                .idCargo(domain.getCargo() != null ? domain.getCargo().getId() : null)
                .nomeCargo(domain.getCargo() != null ? domain.getCargo().getNome() : null)
                .empresaVisitada(domain.getEmpresaVisitada())
                .idEmpresaVisitada(domain.getEmpresaVisitadaRef() != null ? domain.getEmpresaVisitadaRef().getId() : null)
                .nomeEmpresaVisitada(domain.getEmpresaVisitadaRef() != null ? domain.getEmpresaVisitadaRef().getNome() : null)
                .login(domain.getLogin())
                .perfilApp(domain.getPerfilApp())
                .endereco(EnderecoResponse.fromDomain(domain.getEndereco()))
                .regras(regras)
                .equipamentos(equipamentos)
                .documentos(documentos)
                .biometrias(biometrias)
                .mensagensPersonalizadas(mensagensPersonalizadas)
                .cotas(cotas)
                .responsaveis(responsaveis)
                .responsaveisEntidade(responsaveisEntidade)
                .build();
    }
}
