package br.com.startjob.acesso.core.usecase.correspondencia;

import br.com.startjob.acesso.core.domain.correspondencia.Correspondencia;
import br.com.startjob.acesso.core.domain.pedestre.Pedestre;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.WebPermissao;
import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.gateway.CorrespondenciaGateway;
import br.com.startjob.acesso.core.gateway.PedestreGateway;
import br.com.startjob.acesso.core.gateway.UsuarioGateway;
import br.com.startjob.acesso.core.usecase.login.WebPermissaoUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class SalvarCorrespondenciaUseCase {

    public static final Set<String> TIPOS_VOLUME_VALIDOS = Set.of("CARTA", "PACOTE", "DOCUMENTO", "OUTROS");
    public static final Set<String> CONFIRMA_RETIRADA_VALIDOS = Set.of("S", "N");

    private final CorrespondenciaGateway correspondenciaGateway;
    private final PedestreGateway pedestreGateway;
    private final UsuarioGateway usuarioGateway;
    private final WebPermissaoUseCase webPermissaoUseCase;

    @Transactional
    public Correspondencia execute(Correspondencia correspondencia) {
        return execute(correspondencia, false);
    }

    @Transactional
    public Correspondencia execute(Correspondencia correspondencia, boolean removerFoto) {
        if (correspondencia == null) {
            throw new BusinessException("Dados da correspondência não informados.");
        }

        Usuario usuarioLogado = usuarioGateway.findLoggedUser()
                .orElseThrow(() -> new BusinessException("Usuário não está logado."));

        Set<String> permissoes = webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado);
        if (!permissoes.contains(WebPermissao.CORRESPONDENCIA_EDITAR.getCodigo())) {
            throw new BusinessException("Você não possui permissão para salvar correspondências.");
        }

        if (usuarioLogado.getCliente() == null || usuarioLogado.getCliente().getId() == null) {
            throw new BusinessException("Usuário logado não possui cliente associado.");
        }
        Long idClienteLogado = usuarioLogado.getCliente().getId();

        validarCamposObrigatorios(correspondencia);
        normalizarCamposTexto(correspondencia);

        Pedestre destinatario = validarECarregarDestinatario(correspondencia.getDestinatario().getId(), idClienteLogado);
        correspondencia.setDestinatario(destinatario);

        boolean novoRegistro = (correspondencia.getId() == null);
        if (novoRegistro) {
            correspondencia.setCliente(usuarioLogado.getCliente());
            correspondencia.setUsuarioRecebeu(usuarioLogado);
            correspondencia.setRemovido(false);

            if (correspondencia.getDataRecebimento() == null) {
                correspondencia.setDataRecebimento(LocalDateTime.now());
            }

            if (correspondencia.getConfirmaRetirada() == null || correspondencia.getConfirmaRetirada().isBlank()) {
                correspondencia.setConfirmaRetirada("N");
            } else {
                correspondencia.setConfirmaRetirada(correspondencia.getConfirmaRetirada().trim().toUpperCase());
            }
            validarConfirmaRetirada(correspondencia.getConfirmaRetirada());

            if ("S".equals(correspondencia.getConfirmaRetirada())) {
                if (correspondencia.getDataRetirada() == null) {
                    correspondencia.setDataRetirada(LocalDateTime.now());
                }
            } else {
                correspondencia.setDataRetirada(null);
            }

            tratarFoto(correspondencia, null, removerFoto);
        } else {
            Correspondencia existente = correspondenciaGateway.findById(correspondencia.getId())
                    .orElseThrow(() -> new BusinessException("Correspondência não encontrada."));

            if (Boolean.TRUE.equals(existente.getRemovido())) {
                throw new BusinessException("Correspondência não encontrada.");
            }

            if (existente.getCliente() == null || !existente.getCliente().getId().equals(idClienteLogado)) {
                throw new BusinessException("A correspondência não pertence ao seu cliente.");
            }

            correspondencia.setCliente(existente.getCliente());
            correspondencia.setUsuarioRecebeu(
                    existente.getUsuarioRecebeu() != null ? existente.getUsuarioRecebeu() : usuarioLogado
            );
            correspondencia.setDataRecebimento(
                    correspondencia.getDataRecebimento() != null
                            ? correspondencia.getDataRecebimento()
                            : existente.getDataRecebimento()
            );
            correspondencia.setDataCriacao(existente.getDataCriacao());
            correspondencia.setIdArquivoFoto(
                    correspondencia.getIdArquivoFoto() != null
                            ? correspondencia.getIdArquivoFoto()
                            : existente.getIdArquivoFoto()
            );
            correspondencia.setRemovido(false);

            if (correspondencia.getConfirmaRetirada() == null || correspondencia.getConfirmaRetirada().isBlank()) {
                correspondencia.setConfirmaRetirada(
                        existente.getConfirmaRetirada() != null ? existente.getConfirmaRetirada() : "N"
                );
            } else {
                correspondencia.setConfirmaRetirada(correspondencia.getConfirmaRetirada().trim().toUpperCase());
            }
            validarConfirmaRetirada(correspondencia.getConfirmaRetirada());

            if ("S".equals(correspondencia.getConfirmaRetirada())) {
                if (correspondencia.getDataRetirada() == null) {
                    correspondencia.setDataRetirada(
                            existente.getDataRetirada() != null ? existente.getDataRetirada() : LocalDateTime.now()
                    );
                }
                if (correspondencia.getNomeQuemRetirou() == null) {
                    correspondencia.setNomeQuemRetirou(existente.getNomeQuemRetirou());
                }
                if (correspondencia.getDocumentoQuemRetirou() == null) {
                    correspondencia.setDocumentoQuemRetirou(existente.getDocumentoQuemRetirou());
                }
            } else {
                correspondencia.setDataRetirada(null);
                correspondencia.setNomeQuemRetirou(null);
                correspondencia.setDocumentoQuemRetirou(null);
            }

            tratarFoto(correspondencia, existente, removerFoto);
        }

        Correspondencia salva = correspondenciaGateway.save(correspondencia);
        log.info("Correspondência id={} salva com sucesso.", salva.getId());
        return salva;
    }

    private void validarCamposObrigatorios(Correspondencia correspondencia) {
        if (correspondencia.getDestinatario() == null || correspondencia.getDestinatario().getId() == null) {
            throw new BusinessException("Destinatário é obrigatório.");
        }
        if (correspondencia.getTipo() == null || correspondencia.getTipo().isBlank()) {
            throw new BusinessException("Tipo de volume é obrigatório.");
        }
        String tipoNormalizado = correspondencia.getTipo().trim().toUpperCase();
        if (!TIPOS_VOLUME_VALIDOS.contains(tipoNormalizado)) {
            throw new BusinessException("Tipo de volume inválido.");
        }
        correspondencia.setTipo(tipoNormalizado);
    }

    private void validarConfirmaRetirada(String confirmaRetirada) {
        if (!CONFIRMA_RETIRADA_VALIDOS.contains(confirmaRetirada)) {
            throw new BusinessException("Situação de retirada inválida.");
        }
    }

    private Pedestre validarECarregarDestinatario(Long idDestinatario, Long idClienteLogado) {
        Pedestre destinatario = pedestreGateway.findById(idDestinatario)
                .orElseThrow(() -> new BusinessException("Destinatário não encontrado."));

        if (Boolean.TRUE.equals(destinatario.getRemovido())) {
            throw new BusinessException("Destinatário não encontrado.");
        }

        if (destinatario.getCliente() == null || !destinatario.getCliente().getId().equals(idClienteLogado)) {
            throw new BusinessException("O destinatário não pertence ao seu cliente.");
        }

        return destinatario;
    }

    private void normalizarCamposTexto(Correspondencia correspondencia) {
        correspondencia.setCodigoRastreio(trimOrNull(correspondencia.getCodigoRastreio()));
        correspondencia.setObservacao(trimOrNull(correspondencia.getObservacao()));
        correspondencia.setNomeQuemRetirou(trimOrNull(correspondencia.getNomeQuemRetirou()));
        correspondencia.setDocumentoQuemRetirou(trimOrNull(correspondencia.getDocumentoQuemRetirou()));
        correspondencia.setExtensaoFoto(trimOrNull(correspondencia.getExtensaoFoto()));
    }

    private void tratarFoto(Correspondencia correspondencia, Correspondencia existente, boolean removerFoto) {
        if (removerFoto) {
            correspondencia.setFotoVolume(null);
            correspondencia.setExtensaoFoto(null);
            return;
        }

        if (correspondencia.getFotoVolume() != null && correspondencia.getFotoVolume().length > 0) {
            if (correspondencia.getExtensaoFoto() == null || correspondencia.getExtensaoFoto().isBlank()) {
                correspondencia.setExtensaoFoto("jpg");
            }
        } else if (existente != null) {
            correspondencia.setFotoVolume(existente.getFotoVolume());
            correspondencia.setExtensaoFoto(existente.getExtensaoFoto());
        } else {
            correspondencia.setFotoVolume(null);
            correspondencia.setExtensaoFoto(null);
        }
    }

    private String trimOrNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
