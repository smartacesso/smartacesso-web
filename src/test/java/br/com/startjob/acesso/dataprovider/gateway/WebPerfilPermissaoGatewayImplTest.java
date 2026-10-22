package br.com.startjob.acesso.dataprovider.gateway;

import br.com.startjob.acesso.core.enumeration.PerfilAcesso;
import br.com.startjob.acesso.core.enumeration.WebPermissao;
import br.com.startjob.acesso.dataprovider.entity.ClienteEntity;
import br.com.startjob.acesso.dataprovider.entity.WebPerfilPermissaoEntity;
import br.com.startjob.acesso.dataprovider.mapper.WebPerfilPermissaoMapper;
import br.com.startjob.acesso.dataprovider.repository.ClienteRepository;
import br.com.startjob.acesso.dataprovider.repository.WebPerfilPermissaoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebPerfilPermissaoGatewayImplTest {

    @Mock
    private WebPerfilPermissaoRepository webPerfilPermissaoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private WebPerfilPermissaoMapper webPerfilPermissaoMapper;

    @InjectMocks
    private WebPerfilPermissaoGatewayImpl gateway;

    @Test
    @DisplayName("salvarMatrizCliente deve atualizar registros existentes e criar novos para todas as permissões do perfil")
    @SuppressWarnings("unchecked")
    void deveSalvarMatrizAtualizandoExistentesECriandoNovos() {
        Long clienteId = 10L;
        ClienteEntity clienteProxy = new ClienteEntity();
        clienteProxy.setId(clienteId);

        WebPerfilPermissaoEntity existente = new WebPerfilPermissaoEntity();
        existente.setId(99L);
        existente.setCliente(clienteProxy);
        existente.setPerfil(PerfilAcesso.ADMINISTRADOR);
        existente.setCodigoPermissao(WebPermissao.PEDESTRE_VER.getCodigo());
        existente.setHabilitado(false);
        existente.setRemovido(true);

        when(clienteRepository.getReferenceById(clienteId)).thenReturn(clienteProxy);
        when(webPerfilPermissaoRepository.findAllByCliente(clienteId)).thenReturn(List.of(existente));

        Map<PerfilAcesso, Set<String>> matriz = new EnumMap<>(PerfilAcesso.class);
        matriz.put(PerfilAcesso.ADMINISTRADOR, Set.of(WebPermissao.PEDESTRE_VER.getCodigo()));

        gateway.salvarMatrizCliente(clienteId, matriz);

        ArgumentCaptor<List<WebPerfilPermissaoEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(webPerfilPermissaoRepository).saveAll(captor.capture());

        List<WebPerfilPermissaoEntity> salvos = captor.getValue();
        assertEquals(WebPermissao.values().length, salvos.size());

        WebPerfilPermissaoEntity pedestreVerSalvo = salvos.stream()
                .filter(e -> e.getCodigoPermissao().equals(WebPermissao.PEDESTRE_VER.getCodigo()))
                .findFirst()
                .orElseThrow();
        assertEquals(99L, pedestreVerSalvo.getId());
        assertTrue(pedestreVerSalvo.getHabilitado());
        assertFalse(pedestreVerSalvo.getRemovido());

        WebPerfilPermissaoEntity pedestreExcluirSalvo = salvos.stream()
                .filter(e -> e.getCodigoPermissao().equals(WebPermissao.PEDESTRE_EXCLUIR.getCodigo()))
                .findFirst()
                .orElseThrow();
        assertNull(pedestreExcluirSalvo.getId());
        assertFalse(pedestreExcluirSalvo.getHabilitado());
        assertFalse(pedestreExcluirSalvo.getRemovido());
        assertSame(clienteProxy, pedestreExcluirSalvo.getCliente());
    }
}
