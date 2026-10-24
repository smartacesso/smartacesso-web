package br.com.startjob.acesso.dataprovider.gateway;

import br.com.startjob.acesso.core.enumeration.ParametroCatalogo;
import br.com.startjob.acesso.dataprovider.entity.ClienteEntity;
import br.com.startjob.acesso.dataprovider.entity.ParametroEntity;
import br.com.startjob.acesso.dataprovider.mapper.ParametroMapper;
import br.com.startjob.acesso.dataprovider.repository.ClienteRepository;
import br.com.startjob.acesso.dataprovider.repository.ParametroRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParametroGatewayImplTest {

    @Mock
    private ParametroRepository parametroRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ParametroMapper parametroMapper;

    @InjectMocks
    private ParametroGatewayImpl gateway;

    @Test
    @DisplayName("salvarParametrosCliente deve atualizar existentes (incluindo nome legado) e criar novos")
    @SuppressWarnings("unchecked")
    void deveSalvarParametrosAtualizandoExistentesECriandoNovos() {
        Long clienteId = 10L;
        ClienteEntity clienteProxy = new ClienteEntity();
        clienteProxy.setId(clienteId);

        ParametroEntity existenteLegado = new ParametroEntity();
        existenteLegado.setId(55L);
        existenteLegado.setCliente(clienteProxy);
        existenteLegado.setNome(" ");
        existenteLegado.setValor("false");

        when(clienteRepository.getReferenceById(clienteId)).thenReturn(clienteProxy);
        when(parametroRepository.findAllByCliente(clienteId)).thenReturn(List.of(existenteLegado));

        Map<String, String> valores = new LinkedHashMap<>();
        valores.put(ParametroCatalogo.VALIDAR_MATRICULAS_DUPLICADAS.getNomePersistido(), "true");
        valores.put(ParametroCatalogo.GERAR_MATRICULA_SEQUENCIAL.getNomePersistido(), "true");

        gateway.salvarParametrosCliente(clienteId, valores);

        ArgumentCaptor<List<ParametroEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(parametroRepository).saveAll(captor.capture());

        List<ParametroEntity> salvos = captor.getValue();
        assertEquals(2, salvos.size());

        ParametroEntity atualizado = salvos.stream()
                .filter(e -> e.getNome().equals(ParametroCatalogo.VALIDAR_MATRICULAS_DUPLICADAS.getNomePersistido()))
                .findFirst()
                .orElseThrow();
        assertEquals(55L, atualizado.getId());
        assertEquals("true", atualizado.getValor());
        assertFalse(atualizado.getRemovido());

        ParametroEntity criado = salvos.stream()
                .filter(e -> e.getNome().equals(ParametroCatalogo.GERAR_MATRICULA_SEQUENCIAL.getNomePersistido()))
                .findFirst()
                .orElseThrow();
        assertNull(criado.getId());
        assertEquals("true", criado.getValor());
        assertSame(clienteProxy, criado.getCliente());
    }
}
