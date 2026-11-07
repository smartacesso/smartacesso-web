package br.com.startjob.acesso.dataprovider.gateway;

import br.com.startjob.acesso.core.domain.correspondencia.Correspondencia;
import br.com.startjob.acesso.core.gateway.CorrespondenciaGateway;
import br.com.startjob.acesso.dataprovider.entity.CorrespondenciaEntity;
import br.com.startjob.acesso.dataprovider.mapper.CorrespondenciaMapper;
import br.com.startjob.acesso.dataprovider.mapper.CycleAvoidingMappingContext;
import br.com.startjob.acesso.dataprovider.repository.ClienteRepository;
import br.com.startjob.acesso.dataprovider.repository.CorrespondenciaRepository;
import br.com.startjob.acesso.dataprovider.repository.PedestreRepository;
import br.com.startjob.acesso.dataprovider.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class CorrespondenciaGatewayImpl implements CorrespondenciaGateway {

    private final CorrespondenciaRepository correspondenciaRepository;
    private final PedestreRepository pedestreRepository;
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final CorrespondenciaMapper correspondenciaMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<Correspondencia> findById(Long id) {
        return correspondenciaRepository.findByIdComplete(id)
                .map(entity -> correspondenciaMapper.toDomain(entity, new CycleAvoidingMappingContext()));
    }

    @Override
    @Transactional
    public Correspondencia save(Correspondencia correspondencia) {
        if (correspondencia.getId() != null) {
            Optional<CorrespondenciaEntity> existingOpt = correspondenciaRepository.findByIdComplete(correspondencia.getId());
            if (existingOpt.isPresent()) {
                CorrespondenciaEntity existing = existingOpt.get();
                existing.setRemovido(correspondencia.getRemovido() != null ? correspondencia.getRemovido() : false);
                existing.setDataRemovido(correspondencia.getDataRemovido());
                existing.setTipo(correspondencia.getTipo());
                existing.setCodigoRastreio(correspondencia.getCodigoRastreio());
                existing.setIdArquivoFoto(correspondencia.getIdArquivoFoto());
                existing.setDataRecebimento(correspondencia.getDataRecebimento());
                existing.setConfirmaRetirada(correspondencia.getConfirmaRetirada());
                existing.setDataRetirada(correspondencia.getDataRetirada());
                existing.setObservacao(correspondencia.getObservacao());
                existing.setFotoVolume(correspondencia.getFotoVolume());
                existing.setExtensaoFoto(correspondencia.getExtensaoFoto());
                existing.setNomeQuemRetirou(correspondencia.getNomeQuemRetirou());
                existing.setDocumentoQuemRetirou(correspondencia.getDocumentoQuemRetirou());

                if (correspondencia.getDestinatario() != null && correspondencia.getDestinatario().getId() != null) {
                    if (existing.getDestinatario() == null
                            || !existing.getDestinatario().getId().equals(correspondencia.getDestinatario().getId())) {
                        existing.setDestinatario(pedestreRepository.getReferenceById(correspondencia.getDestinatario().getId()));
                    }
                }

                if (correspondencia.getUsuarioRecebeu() != null && correspondencia.getUsuarioRecebeu().getId() != null) {
                    if (existing.getUsuarioRecebeu() == null
                            || !existing.getUsuarioRecebeu().getId().equals(correspondencia.getUsuarioRecebeu().getId())) {
                        existing.setUsuarioRecebeu(usuarioRepository.getReferenceById(correspondencia.getUsuarioRecebeu().getId()));
                    }
                } else {
                    existing.setUsuarioRecebeu(null);
                }

                var saved = correspondenciaRepository.save(existing);
                var domain = correspondenciaMapper.toDomain(saved, new CycleAvoidingMappingContext());
                if (correspondencia.getDestinatario() != null && correspondencia.getDestinatario().getNome() != null) {
                    domain.setDestinatario(correspondencia.getDestinatario());
                }
                if (correspondencia.getUsuarioRecebeu() != null && correspondencia.getUsuarioRecebeu().getNome() != null) {
                    domain.setUsuarioRecebeu(correspondencia.getUsuarioRecebeu());
                }
                return domain;
            }
        }

        var entity = correspondenciaMapper.toEntity(correspondencia, new CycleAvoidingMappingContext());
        if (correspondencia.getDestinatario() != null && correspondencia.getDestinatario().getId() != null) {
            entity.setDestinatario(pedestreRepository.getReferenceById(correspondencia.getDestinatario().getId()));
        }
        if (correspondencia.getUsuarioRecebeu() != null && correspondencia.getUsuarioRecebeu().getId() != null) {
            entity.setUsuarioRecebeu(usuarioRepository.getReferenceById(correspondencia.getUsuarioRecebeu().getId()));
        } else {
            entity.setUsuarioRecebeu(null);
        }
        if (correspondencia.getCliente() != null && correspondencia.getCliente().getId() != null) {
            entity.setCliente(clienteRepository.getReferenceById(correspondencia.getCliente().getId()));
        }
        if (entity.getRemovido() == null) {
            entity.setRemovido(false);
        }

        var saved = correspondenciaRepository.save(entity);
        var domain = correspondenciaMapper.toDomain(saved, new CycleAvoidingMappingContext());
        if (correspondencia.getDestinatario() != null && correspondencia.getDestinatario().getNome() != null) {
            domain.setDestinatario(correspondencia.getDestinatario());
        }
        if (correspondencia.getUsuarioRecebeu() != null && correspondencia.getUsuarioRecebeu().getNome() != null) {
            domain.setUsuarioRecebeu(correspondencia.getUsuarioRecebeu());
        }
        return domain;
    }
}
