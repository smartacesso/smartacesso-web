package br.com.startjob.acesso.dataprovider.gateway;

import br.com.startjob.acesso.core.domain.cliente.Cliente;
import br.com.startjob.acesso.core.domain.empresa.Empresa;
import br.com.startjob.acesso.core.domain.regra.Horario;
import br.com.startjob.acesso.core.domain.regra.Regra;
import br.com.startjob.acesso.core.enumeration.Status;
import br.com.startjob.acesso.core.enumeration.TipoPedestre;
import br.com.startjob.acesso.core.enumeration.TipoRegra;
import br.com.startjob.acesso.dataprovider.entity.ClienteEntity;
import br.com.startjob.acesso.dataprovider.entity.EmpresaEntity;
import br.com.startjob.acesso.dataprovider.entity.HorarioEntity;
import br.com.startjob.acesso.dataprovider.entity.RegraEntity;
import br.com.startjob.acesso.dataprovider.mapper.CycleAvoidingMappingContext;
import br.com.startjob.acesso.dataprovider.mapper.HorarioMapper;
import br.com.startjob.acesso.dataprovider.mapper.RegraMapper;
import br.com.startjob.acesso.dataprovider.repository.ClienteRepository;
import br.com.startjob.acesso.dataprovider.repository.EmpresaRepository;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private EmpresaRepository empresaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private RegraMapper regraMapper;

    @Mock
    private HorarioMapper horarioMapper;

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

    @Test
    @DisplayName("save deve associar empresa, cliente e horarios à nova RegraEntity e salvar")
    void deveSalvarNovaRegraAssociandoEntidades() {
        Empresa empresa = Empresa.builder().id(20L).build();
        Cliente cliente = Cliente.builder().id(10L).build();
        Horario horario = Horario.builder().nome("Turno 1").status(Status.ATIVO).build();

        Regra regra = Regra.builder()
                .nome("Nova Regra")
                .empresa(empresa)
                .cliente(cliente)
                .horarios(List.of(horario))
                .build();

        RegraEntity entity = new RegraEntity();
        entity.setNome("Nova Regra");
        HorarioEntity horarioEntity = new HorarioEntity();
        horarioEntity.setNome("Turno 1");
        entity.setHorarios(new ArrayList<>(List.of(horarioEntity)));

        EmpresaEntity empresaProxy = new EmpresaEntity();
        empresaProxy.setId(20L);
        ClienteEntity clienteProxy = new ClienteEntity();
        clienteProxy.setId(10L);

        when(empresaRepository.getReferenceById(20L)).thenReturn(empresaProxy);
        when(clienteRepository.getReferenceById(10L)).thenReturn(clienteProxy);
        when(regraMapper.toEntity(eq(regra), any(CycleAvoidingMappingContext.class))).thenReturn(entity);
        when(regraRepository.save(entity)).thenReturn(entity);

        Regra domainRetornado = Regra.builder()
                .id(1L)
                .nome("Nova Regra")
                .empresa(empresa)
                .cliente(cliente)
                .horarios(List.of(horario))
                .build();
        when(regraMapper.toDomain(eq(entity), any(CycleAvoidingMappingContext.class))).thenReturn(domainRetornado);

        Regra resultado = regraGateway.save(regra);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertSame(empresaProxy, entity.getEmpresa());
        assertSame(clienteProxy, entity.getCliente());
        assertSame(entity, horarioEntity.getRegra());
        assertEquals(Boolean.FALSE, horarioEntity.getRemovido());
        assertEquals(Boolean.FALSE, entity.getRemovido());
        verify(regraRepository).save(entity);
    }

    @Test
    @DisplayName("save deve atualizar existente, sincronizar novos, alterados e marcar ausentes como removidos")
    void deveAtualizarRegraExistenteSincronizandoHorarios() {
        RegraEntity existing = new RegraEntity();
        existing.setId(50L);
        existing.setNome("Nome Antigo");

        HorarioEntity hExistente1 = new HorarioEntity();
        hExistente1.setId(101L);
        hExistente1.setNome("H1");
        hExistente1.setStatus(Status.ATIVO);
        hExistente1.setRemovido(false);

        HorarioEntity hExistente2 = new HorarioEntity();
        hExistente2.setId(102L);
        hExistente2.setNome("H2 Removido");
        hExistente2.setStatus(Status.ATIVO);
        hExistente2.setRemovido(false);

        existing.setHorarios(new ArrayList<>(List.of(hExistente1, hExistente2)));

        when(regraRepository.findByIdComplete(50L)).thenReturn(Optional.of(existing));

        Horario h1Atualizado = Horario.builder().id(101L).nome("H1 Atualizado").status(Status.ATIVO).diasSemana("123").build();
        Horario novoH = Horario.builder().nome("H3 Novo").status(Status.ATIVO).diasSemana("456").build();

        Regra regraToSave = Regra.builder()
                .id(50L)
                .nome("Nome Atualizado")
                .status(Status.ATIVO)
                .horarios(List.of(h1Atualizado, novoH))
                .build();

        HorarioEntity newHEntity = new HorarioEntity();
        newHEntity.setNome("H3 Novo");
        newHEntity.setStatus(Status.ATIVO);
        when(horarioMapper.toEntity(eq(novoH), any(CycleAvoidingMappingContext.class))).thenReturn(newHEntity);

        when(regraRepository.save(existing)).thenReturn(existing);

        Regra domainMock = Regra.builder()
                .id(50L)
                .nome("Nome Atualizado")
                .horarios(List.of(h1Atualizado, novoH))
                .build();
        when(regraMapper.toDomain(eq(existing), any(CycleAvoidingMappingContext.class))).thenReturn(domainMock);

        Regra resultado = regraGateway.save(regraToSave);

        assertNotNull(resultado);
        assertEquals("Nome Atualizado", existing.getNome());
        assertEquals("H1 Atualizado", hExistente1.getNome());
        assertEquals(Boolean.FALSE, hExistente1.getRemovido());
        assertEquals(Boolean.TRUE, hExistente2.getRemovido());
        assertNotNull(hExistente2.getDataRemovido());
        assertTrue(existing.getHorarios().contains(newHEntity));
        assertSame(existing, newHEntity.getRegra());
        verify(regraRepository).save(existing);
    }
}
