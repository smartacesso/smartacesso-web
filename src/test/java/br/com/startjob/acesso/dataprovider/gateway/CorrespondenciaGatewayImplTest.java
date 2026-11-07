package br.com.startjob.acesso.dataprovider.gateway;

import br.com.startjob.acesso.core.domain.cliente.Cliente;
import br.com.startjob.acesso.core.domain.correspondencia.Correspondencia;
import br.com.startjob.acesso.core.domain.pedestre.Pedestre;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.dataprovider.entity.ClienteEntity;
import br.com.startjob.acesso.dataprovider.entity.CorrespondenciaEntity;
import br.com.startjob.acesso.dataprovider.entity.PedestreEntity;
import br.com.startjob.acesso.dataprovider.entity.UsuarioEntity;
import br.com.startjob.acesso.dataprovider.mapper.CorrespondenciaMapper;
import br.com.startjob.acesso.dataprovider.repository.ClienteRepository;
import br.com.startjob.acesso.dataprovider.repository.CorrespondenciaRepository;
import br.com.startjob.acesso.dataprovider.repository.PedestreRepository;
import br.com.startjob.acesso.dataprovider.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CorrespondenciaGatewayImplTest {

    @Mock
    private CorrespondenciaRepository correspondenciaRepository;

    @Mock
    private PedestreRepository pedestreRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private CorrespondenciaMapper correspondenciaMapper;

    @InjectMocks
    private CorrespondenciaGatewayImpl correspondenciaGateway;

    @Test
    @DisplayName("save deve persistir nova CorrespondenciaEntity associando referências JPA")
    void deveSalvarNovaCorrespondencia() {
        Pedestre destinatario = Pedestre.builder().id(50L).nome("Morador Maria").build();
        Usuario usuarioRecebeu = Usuario.builder().id(10L).nome("Porteiro João").build();
        Cliente cliente = Cliente.builder().id(1L).nome("Condomínio").build();

        Correspondencia domain = Correspondencia.builder()
                .destinatario(destinatario)
                .usuarioRecebeu(usuarioRecebeu)
                .cliente(cliente)
                .tipo("PACOTE")
                .codigoRastreio("BR123")
                .confirmaRetirada("N")
                .dataRecebimento(LocalDateTime.now())
                .build();

        CorrespondenciaEntity entity = new CorrespondenciaEntity();
        entity.setTipo("PACOTE");

        PedestreEntity pedRef = new PedestreEntity();
        pedRef.setId(50L);
        UsuarioEntity usrRef = new UsuarioEntity();
        usrRef.setId(10L);
        ClienteEntity cliRef = new ClienteEntity();
        cliRef.setId(1L);

        when(correspondenciaMapper.toEntity(eq(domain), any())).thenReturn(entity);
        when(pedestreRepository.getReferenceById(50L)).thenReturn(pedRef);
        when(usuarioRepository.getReferenceById(10L)).thenReturn(usrRef);
        when(clienteRepository.getReferenceById(1L)).thenReturn(cliRef);
        when(correspondenciaRepository.save(entity)).thenReturn(entity);

        Correspondencia domainRetornado = Correspondencia.builder()
                .id(100L)
                .tipo("PACOTE")
                .codigoRastreio("BR123")
                .confirmaRetirada("N")
                .build();
        when(correspondenciaMapper.toDomain(eq(entity), any())).thenReturn(domainRetornado);

        Correspondencia resultado = correspondenciaGateway.save(domain);

        assertNotNull(resultado);
        assertEquals(100L, resultado.getId());
        assertEquals("Morador Maria", resultado.getDestinatario().getNome());
        assertEquals("Porteiro João", resultado.getUsuarioRecebeu().getNome());
        assertSame(pedRef, entity.getDestinatario());
        assertSame(usrRef, entity.getUsuarioRecebeu());
        assertSame(cliRef, entity.getCliente());
        assertEquals(Boolean.FALSE, entity.getRemovido());
        verify(correspondenciaRepository, times(1)).save(entity);
    }

    @Test
    @DisplayName("save deve atualizar CorrespondenciaEntity existente quando id informado")
    void deveAtualizarCorrespondenciaExistente() {
        PedestreEntity pedExistente = new PedestreEntity();
        pedExistente.setId(50L);

        UsuarioEntity usrExistente = new UsuarioEntity();
        usrExistente.setId(10L);

        CorrespondenciaEntity existing = new CorrespondenciaEntity();
        existing.setId(100L);
        existing.setTipo("CARTA");
        existing.setConfirmaRetirada("N");
        existing.setDestinatario(pedExistente);
        existing.setUsuarioRecebeu(usrExistente);

        when(correspondenciaRepository.findByIdComplete(100L)).thenReturn(Optional.of(existing));
        when(correspondenciaRepository.save(existing)).thenReturn(existing);

        Correspondencia domainParaAtualizar = Correspondencia.builder()
                .id(100L)
                .destinatario(Pedestre.builder().id(50L).nome("Morador Maria").build())
                .usuarioRecebeu(Usuario.builder().id(10L).nome("Porteiro João").build())
                .tipo("DOCUMENTO")
                .codigoRastreio("NOVO999")
                .confirmaRetirada("S")
                .observacao("Entregue em mãos")
                .build();

        Correspondencia domainMapeado = Correspondencia.builder()
                .id(100L)
                .tipo("DOCUMENTO")
                .codigoRastreio("NOVO999")
                .confirmaRetirada("S")
                .observacao("Entregue em mãos")
                .build();
        when(correspondenciaMapper.toDomain(eq(existing), any())).thenReturn(domainMapeado);

        Correspondencia resultado = correspondenciaGateway.save(domainParaAtualizar);

        assertNotNull(resultado);
        assertEquals("DOCUMENTO", existing.getTipo());
        assertEquals("NOVO999", existing.getCodigoRastreio());
        assertEquals("S", existing.getConfirmaRetirada());
        assertEquals("Entregue em mãos", existing.getObservacao());
        verify(correspondenciaRepository, times(1)).save(existing);
    }

    @Test
    @DisplayName("findById deve buscar com findByIdComplete e mapear para domínio")
    void deveBuscarPorIdComSucesso() {
        CorrespondenciaEntity entity = new CorrespondenciaEntity();
        entity.setId(100L);
        entity.setTipo("PACOTE");

        when(correspondenciaRepository.findByIdComplete(100L)).thenReturn(Optional.of(entity));

        Correspondencia domain = Correspondencia.builder().id(100L).tipo("PACOTE").build();
        when(correspondenciaMapper.toDomain(eq(entity), any())).thenReturn(domain);

        Optional<Correspondencia> resultado = correspondenciaGateway.findById(100L);

        assertTrue(resultado.isPresent());
        assertEquals(100L, resultado.get().getId());
        verify(correspondenciaRepository, times(1)).findByIdComplete(100L);
    }
}
