package br.com.startjob.acesso.dataprovider.gateway;

import br.com.startjob.acesso.core.domain.cliente.Cliente;
import br.com.startjob.acesso.core.domain.endereco.Endereco;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.PerfilAcesso;
import br.com.startjob.acesso.core.enumeration.Status;
import br.com.startjob.acesso.dataprovider.entity.ClienteEntity;
import br.com.startjob.acesso.dataprovider.entity.EnderecoEntity;
import br.com.startjob.acesso.dataprovider.entity.UsuarioEntity;
import br.com.startjob.acesso.dataprovider.mapper.EnderecoMapper;
import br.com.startjob.acesso.dataprovider.mapper.UsuarioMapper;
import br.com.startjob.acesso.dataprovider.repository.ClienteRepository;
import br.com.startjob.acesso.dataprovider.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioGatewayImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private EnderecoMapper enderecoMapper;

    @InjectMocks
    private UsuarioGatewayImpl usuarioGateway;

    @Test
    @DisplayName("save deve criar novo usuário associando cliente por referência e salvando")
    void deveSalvarNovoUsuarioComSucesso() {
        Cliente cliente = Cliente.builder().id(10L).nome("Cliente").build();
        Usuario usuario = Usuario.builder()
                .nome("Novo Usuario")
                .login("novo.usuario")
                .status(Status.ATIVO)
                .perfil(PerfilAcesso.ADMINISTRADOR)
                .cliente(cliente)
                .build();

        UsuarioEntity entity = new UsuarioEntity();
        entity.setNome("Novo Usuario");

        ClienteEntity clienteEntity = new ClienteEntity();
        clienteEntity.setId(10L);

        when(usuarioMapper.toEntity(eq(usuario), any())).thenReturn(entity);
        when(clienteRepository.getReferenceById(10L)).thenReturn(clienteEntity);
        when(usuarioRepository.save(entity)).thenReturn(entity);

        Usuario domainRetornado = Usuario.builder().id(1L).nome("Novo Usuario").build();
        when(usuarioMapper.toDomain(eq(entity), any())).thenReturn(domainRetornado);

        Usuario resultado = usuarioGateway.save(usuario);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(clienteEntity, entity.getCliente());
        assertEquals(Boolean.FALSE, entity.getRemovido());
        verify(usuarioRepository, times(1)).save(entity);
    }

    @Test
    @DisplayName("save deve atualizar usuário existente adicionando novo endereço quando antes não possuía")
    void deveAtualizarUsuarioAdicionandoEndereco() {
        UsuarioEntity existing = new UsuarioEntity();
        existing.setId(52L);
        existing.setNome("Usuario Antigo");
        existing.setVersao(1);
        existing.setEndereco(null);

        when(usuarioRepository.findByIdWithCliente(52L)).thenReturn(Optional.of(existing));

        Endereco novoEndereco = Endereco.builder()
                .cep("38400-000")
                .logradouro("Rua das Flores")
                .numero("123")
                .bairro("Centro")
                .cidade("Uberlândia")
                .estado("MG")
                .build();

        Usuario usuarioUpdate = Usuario.builder()
                .id(52L)
                .nome("Usuario Atualizado")
                .endereco(novoEndereco)
                .build();

        EnderecoEntity newEndEntity = new EnderecoEntity();
        newEndEntity.setCep("38400-000");
        newEndEntity.setLogradouro("Rua das Flores");

        when(enderecoMapper.toEntity(eq(novoEndereco), any())).thenReturn(newEndEntity);
        when(usuarioRepository.save(existing)).thenReturn(existing);

        Usuario domainRetornado = Usuario.builder()
                .id(52L)
                .nome("Usuario Atualizado")
                .endereco(novoEndereco)
                .build();
        when(usuarioMapper.toDomain(eq(existing), any())).thenReturn(domainRetornado);

        Usuario resultado = usuarioGateway.save(usuarioUpdate);

        assertNotNull(resultado);
        assertEquals(52L, resultado.getId());
        assertEquals("Usuario Atualizado", existing.getNome());
        assertNotNull(existing.getEndereco());
        assertEquals("Rua das Flores", existing.getEndereco().getLogradouro());
        assertEquals(Boolean.FALSE, existing.getEndereco().getRemovido());
        verify(usuarioRepository, times(1)).save(existing);
    }

    @Test
    @DisplayName("save deve atualizar campos do endereço existente in-place quando usuário já possuía endereço")
    void deveAtualizarCamposDoEnderecoExistente() {
        UsuarioEntity existing = new UsuarioEntity();
        existing.setId(52L);
        existing.setNome("Usuario");
        existing.setVersao(1);

        EnderecoEntity existingEnd = new EnderecoEntity();
        existingEnd.setId(100L);
        existingEnd.setCep("38400-000");
        existingEnd.setLogradouro("Rua Antiga");
        existingEnd.setNumero("10");
        existing.setEndereco(existingEnd);

        when(usuarioRepository.findByIdWithCliente(52L)).thenReturn(Optional.of(existing));

        Endereco enderecoAtualizado = Endereco.builder()
                .cep("38400-111")
                .logradouro("Rua Nova")
                .numero("20")
                .bairro("Bairro Novo")
                .cidade("Uberlândia")
                .estado("MG")
                .build();

        Usuario usuarioUpdate = Usuario.builder()
                .id(52L)
                .nome("Usuario")
                .endereco(enderecoAtualizado)
                .build();

        when(usuarioRepository.save(existing)).thenReturn(existing);

        Usuario domainRetornado = Usuario.builder()
                .id(52L)
                .nome("Usuario")
                .endereco(enderecoAtualizado)
                .build();
        when(usuarioMapper.toDomain(eq(existing), any())).thenReturn(domainRetornado);

        Usuario resultado = usuarioGateway.save(usuarioUpdate);

        assertNotNull(resultado);
        assertEquals(52L, resultado.getId());
        assertSame(existingEnd, existing.getEndereco());
        assertEquals("Rua Nova", existingEnd.getLogradouro());
        assertEquals("20", existingEnd.getNumero());
        assertEquals("38400-111", existingEnd.getCep());
        verify(usuarioRepository, times(1)).save(existing);
        verifyNoInteractions(enderecoMapper);
    }

    @Test
    @DisplayName("save deve desvincular endereço se endereço recebido for vazio ou nulo")
    void deveRemoverEnderecoQuandoVazio() {
        UsuarioEntity existing = new UsuarioEntity();
        existing.setId(52L);
        existing.setNome("Usuario");
        existing.setVersao(1);

        EnderecoEntity existingEnd = new EnderecoEntity();
        existingEnd.setId(100L);
        existing.setEndereco(existingEnd);

        when(usuarioRepository.findByIdWithCliente(52L)).thenReturn(Optional.of(existing));

        Endereco enderecoVazio = Endereco.builder().cep("").logradouro("").build();
        Usuario usuarioUpdate = Usuario.builder()
                .id(52L)
                .nome("Usuario")
                .endereco(enderecoVazio)
                .build();

        when(usuarioRepository.save(existing)).thenReturn(existing);

        Usuario domainRetornado = Usuario.builder().id(52L).nome("Usuario").build();
        when(usuarioMapper.toDomain(eq(existing), any())).thenReturn(domainRetornado);

        Usuario resultado = usuarioGateway.save(usuarioUpdate);

        assertNotNull(resultado);
        assertNull(existing.getEndereco());
        verify(usuarioRepository, times(1)).save(existing);
    }

    @Test
    @DisplayName("findByLoginAndCliente deve delegar ao repository e converter para domínio")
    void deveBuscarPorLoginECliente() {
        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(5L);
        entity.setLogin("joao");

        when(usuarioRepository.findByLoginAndCliente("joao", 10L)).thenReturn(Optional.of(entity));

        Usuario domain = Usuario.builder().id(5L).login("joao").build();
        when(usuarioMapper.toDomain(eq(entity), any())).thenReturn(domain);

        Optional<Usuario> resultado = usuarioGateway.findByLoginAndCliente("joao", 10L);

        assertTrue(resultado.isPresent());
        assertEquals("joao", resultado.get().getLogin());
        verify(usuarioRepository, times(1)).findByLoginAndCliente("joao", 10L);
    }
}
