package br.com.startjob.acesso.dataprovider.gateway;

import br.com.startjob.acesso.core.domain.regra.Regra;
import br.com.startjob.acesso.core.enumeration.TipoPedestre;
import br.com.startjob.acesso.core.enumeration.TipoRegra;
import br.com.startjob.acesso.dataprovider.entity.RegraEntity;
import br.com.startjob.acesso.dataprovider.mapper.CycleAvoidingMappingContext;
import br.com.startjob.acesso.dataprovider.mapper.RegraMapper;
import br.com.startjob.acesso.dataprovider.repository.RegraRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegraGatewayImplTest {

    @Mock
    private RegraRepository regraRepository;

    @Mock
    private RegraMapper regraMapper;

    @InjectMocks
    private RegraGatewayImpl regraGateway;

    @Test
    @DisplayName("buscarRegras deve consultar repository e mapear entidades para domain")
    void deveBuscarRegrasEMapearParaDomain() {
        Long idCliente = 10L;
        Pageable pageable = PageRequest.of(0, 10);
        RegraEntity entity = new RegraEntity();
        entity.setId(1L);
        entity.setNome("Regra Teste");

        Regra domain = Regra.builder().id(1L).nome("Regra Teste").build();

        Page<RegraEntity> entityPage = new PageImpl<>(List.of(entity), pageable, 1);

        when(regraRepository.buscarRegras(eq(idCliente), eq("Regra"), eq(5L), eq("Empresa"), eq(TipoPedestre.PEDESTRE), eq(TipoRegra.ACESSO_HORARIO), eq(pageable)))
                .thenReturn(entityPage);
        when(regraMapper.toDomain(eq(entity), any(CycleAvoidingMappingContext.class)))
                .thenReturn(domain);

        Page<Regra> result = regraGateway.buscarRegras(idCliente, "Regra", 5L, "Empresa", TipoPedestre.PEDESTRE, TipoRegra.ACESSO_HORARIO, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).getId());
        assertEquals("Regra Teste", result.getContent().get(0).getNome());
        verify(regraRepository).buscarRegras(idCliente, "Regra", 5L, "Empresa", TipoPedestre.PEDESTRE, TipoRegra.ACESSO_HORARIO, pageable);
    }

    @Test
    @DisplayName("findById deve buscar regra completa e mapear para domain")
    void deveBuscarRegraPorId() {
        RegraEntity entity = new RegraEntity();
        entity.setId(10L);
        Regra domain = Regra.builder().id(10L).nome("Regra 10").build();

        when(regraRepository.findByIdComplete(10L)).thenReturn(Optional.of(entity));
        when(regraMapper.toDomain(eq(entity), any(CycleAvoidingMappingContext.class))).thenReturn(domain);

        Optional<Regra> result = regraGateway.findById(10L);

        assertTrue(result.isPresent());
        assertEquals(10L, result.get().getId());
        verify(regraRepository).findByIdComplete(10L);
    }
}
