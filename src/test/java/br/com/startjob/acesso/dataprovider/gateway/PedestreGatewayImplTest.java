package br.com.startjob.acesso.dataprovider.gateway;

import br.com.startjob.acesso.core.domain.cliente.Cliente;
import br.com.startjob.acesso.core.domain.empresa.Empresa;
import br.com.startjob.acesso.core.domain.endereco.Endereco;
import br.com.startjob.acesso.core.domain.equipamento.Equipamento;
import br.com.startjob.acesso.core.domain.pedestre.Documento;
import br.com.startjob.acesso.core.domain.pedestre.Pedestre;
import br.com.startjob.acesso.core.domain.pedestre.PedestreEquipamento;
import br.com.startjob.acesso.core.domain.pedestre.PedestreRegra;
import br.com.startjob.acesso.core.domain.regra.Regra;
import br.com.startjob.acesso.core.enumeration.Status;
import br.com.startjob.acesso.core.enumeration.TipoPedestre;
import br.com.startjob.acesso.dataprovider.entity.ClienteEntity;
import br.com.startjob.acesso.dataprovider.entity.DocumentoEntity;
import br.com.startjob.acesso.dataprovider.entity.EmpresaEntity;
import br.com.startjob.acesso.dataprovider.entity.EnderecoEntity;
import br.com.startjob.acesso.dataprovider.entity.EquipamentoEntity;
import br.com.startjob.acesso.dataprovider.entity.PedestreEntity;
import br.com.startjob.acesso.dataprovider.entity.PedestreEquipamentoEntity;
import br.com.startjob.acesso.dataprovider.entity.PedestreRegraEntity;
import br.com.startjob.acesso.dataprovider.entity.RegraEntity;
import br.com.startjob.acesso.dataprovider.mapper.*;
import br.com.startjob.acesso.dataprovider.repository.ClienteRepository;
import br.com.startjob.acesso.dataprovider.repository.EmpresaRepository;
import br.com.startjob.acesso.dataprovider.repository.EquipamentoRepository;
import br.com.startjob.acesso.dataprovider.repository.PedestreRepository;
import br.com.startjob.acesso.dataprovider.repository.RegraRepository;
import br.com.startjob.acesso.dataprovider.repository.ResponsavelRepository;
import br.com.startjob.acesso.dataprovider.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedestreGatewayImplTest {

    @Mock
    private PedestreRepository pedestreRepository;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private EmpresaRepository empresaRepository;
    @Mock
    private RegraRepository regraRepository;
    @Mock
    private EquipamentoRepository equipamentoRepository;
    @Mock
    private ResponsavelRepository responsavelRepository;
    @Mock
    private PedestreMapper pedestreMapper;
    @Mock
    private PedestreRegraMapper pedestreRegraMapper;
    @Mock
    private PedestreEquipamentoMapper pedestreEquipamentoMapper;
    @Mock
    private DocumentoMapper documentoMapper;
    @Mock
    private BiometriaMapper biometriaMapper;
    @Mock
    private MensagemEquipamentoMapper mensagemEquipamentoMapper;
    @Mock
    private HistoricoCotaMapper historicoCotaMapper;
    @Mock
    private EnderecoMapper enderecoMapper;
    @Mock
    private DepartamentoMapper departamentoMapper;
    @Mock
    private CentroCustoMapper centroCustoMapper;
    @Mock
    private CargoMapper cargoMapper;
    @Mock
    private HorarioMapper horarioMapper;

    @InjectMocks
    private PedestreGatewayImpl pedestreGateway;

    @Test
    @DisplayName("save deve associar cliente, empresa, regras, equipamentos e documentos ao salvar novo PedestreEntity")
    void deveSalvarNovoPedestre() {
        Cliente cliente = Cliente.builder().id(10L).build();
        Empresa empresa = Empresa.builder().id(20L).build();
        Regra regra = Regra.builder().id(30L).build();
        Equipamento equipamento = Equipamento.builder().id(40L).build();
        Documento docDomain = Documento.builder().nome("Doc 1").build();
        PedestreEquipamento peDomain = PedestreEquipamento.builder().equipamento(equipamento).build();

        Pedestre domainInput = Pedestre.builder()
                .nome("Carlos")
                .status(Status.ATIVO)
                .tipo(TipoPedestre.PEDESTRE)
                .cliente(cliente)
                .empresa(empresa)
                .endereco(Endereco.builder().cep("01001000").build())
                .regras(List.of(PedestreRegra.builder().regra(regra).build()))
                .equipamentos(List.of(peDomain))
                .documentos(List.of(docDomain))
                .build();

        PedestreEntity entityMapped = new PedestreEntity();
        entityMapped.setNome("Carlos");
        EnderecoEntity enderecoEntity = new EnderecoEntity();
        enderecoEntity.setCep("01001000");
        entityMapped.setEndereco(enderecoEntity);

        PedestreRegraEntity prEntity = new PedestreRegraEntity();
        RegraEntity regraStub = new RegraEntity();
        regraStub.setId(30L);
        prEntity.setRegra(regraStub);
        entityMapped.setRegras(new ArrayList<>(List.of(prEntity)));

        PedestreEquipamentoEntity peEntity = new PedestreEquipamentoEntity();
        EquipamentoEntity eqStub = new EquipamentoEntity();
        eqStub.setId(40L);
        peEntity.setEquipamento(eqStub);
        entityMapped.setEquipamentos(new ArrayList<>(List.of(peEntity)));

        EquipamentoEntity eqRef = new EquipamentoEntity();
        eqRef.setId(40L);

        DocumentoEntity docEntity = new DocumentoEntity();
        docEntity.setNome("Doc 1");
        entityMapped.setDocumentos(new ArrayList<>(List.of(docEntity)));

        ClienteEntity clienteRef = new ClienteEntity();
        clienteRef.setId(10L);
        EmpresaEntity empresaRef = new EmpresaEntity();
        empresaRef.setId(20L);
        RegraEntity regraRef = new RegraEntity();
        regraRef.setId(30L);

        when(pedestreMapper.toEntity(eq(domainInput), any(CycleAvoidingMappingContext.class))).thenReturn(entityMapped);
        when(clienteRepository.getReferenceById(10L)).thenReturn(clienteRef);
        when(empresaRepository.getReferenceById(20L)).thenReturn(empresaRef);
        when(regraRepository.getReferenceById(30L)).thenReturn(regraRef);
        when(equipamentoRepository.getReferenceById(40L)).thenReturn(eqRef);
        when(pedestreRepository.save(entityMapped)).thenReturn(entityMapped);

        Pedestre domainReturned = Pedestre.builder().id(100L).nome("Carlos").build();
        when(pedestreMapper.toDomain(eq(entityMapped), any(CycleAvoidingMappingContext.class))).thenReturn(domainReturned);

        Pedestre result = pedestreGateway.save(domainInput);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertSame(clienteRef, entityMapped.getCliente());
        assertSame(empresaRef, entityMapped.getEmpresa());
        assertSame(entityMapped, prEntity.getPedestre());
        assertSame(regraRef, prEntity.getRegra());
        assertEquals(Boolean.FALSE, prEntity.getRemovido());
        assertEquals(1, entityMapped.getEquipamentos().size());
        assertSame(eqRef, peEntity.getEquipamento());
        assertEquals(1, entityMapped.getDocumentos().size());
        assertSame(entityMapped, docEntity.getPedestre());
        verify(pedestreRepository).save(entityMapped);
    }

    @Test
    @DisplayName("save deve atualizar PedestreEntity existente e marcar regras ausentes como removidas")
    void deveAtualizarPedestreExistenteSincronizandoRegras() {
        PedestreEntity existing = new PedestreEntity();
        existing.setId(50L);
        existing.setNome("Antigo");

        PedestreRegraEntity prAntiga = new PedestreRegraEntity();
        prAntiga.setId(500L);
        prAntiga.setRemovido(false);
        existing.setRegras(new ArrayList<>(List.of(prAntiga)));

        when(pedestreRepository.findByIdComplete(50L)).thenReturn(Optional.of(existing));

        PedestreRegra novaRegraDomain = PedestreRegra.builder()
                .regra(Regra.builder().id(30L).build())
                .qtdeDeCreditos(1L)
                .build();

        Pedestre updateInput = Pedestre.builder()
                .id(50L)
                .nome("Novo Nome")
                .status(Status.ATIVO)
                .tipo(TipoPedestre.VISITANTE)
                .regras(List.of(novaRegraDomain))
                .build();

        PedestreRegraEntity novaPrEntity = new PedestreRegraEntity();
        when(pedestreRegraMapper.toEntity(eq(novaRegraDomain), any(CycleAvoidingMappingContext.class)))
                .thenReturn(novaPrEntity);
        RegraEntity regraRef = new RegraEntity();
        regraRef.setId(30L);
        when(regraRepository.getReferenceById(30L)).thenReturn(regraRef);
        when(pedestreRepository.save(existing)).thenReturn(existing);
        when(pedestreMapper.toDomain(eq(existing), any(CycleAvoidingMappingContext.class)))
                .thenReturn(Pedestre.builder().id(50L).nome("Novo Nome").build());

        Pedestre result = pedestreGateway.save(updateInput);

        assertNotNull(result);
        assertEquals("Novo Nome", existing.getNome());
        assertEquals(Boolean.TRUE, prAntiga.getRemovido());
        assertNotNull(prAntiga.getDataRemovido());
        assertTrue(existing.getRegras().contains(novaPrEntity));
        assertSame(existing, novaPrEntity.getPedestre());
        assertSame(regraRef, novaPrEntity.getRegra());
    }

    @Test
    @DisplayName("findById deve buscar com findByIdComplete e filtrar colecoes filhas removidas")
    void deveBuscarPorIdFiltrandoFilhosRemovidos() {
        PedestreEntity entity = new PedestreEntity();
        entity.setId(77L);
        when(pedestreRepository.findByIdComplete(77L)).thenReturn(Optional.of(entity));

        PedestreRegra regraAtiva = PedestreRegra.builder().id(1L).removido(false).build();
        PedestreRegra regraRemovida = PedestreRegra.builder().id(2L).removido(true).build();
        Documento docAtivo = Documento.builder().id(10L).removido(false).build();
        Documento docRemovido = Documento.builder().id(11L).removido(true).build();

        Pedestre mapped = Pedestre.builder()
                .id(77L)
                .nome("Maria")
                .regras(new ArrayList<>(List.of(regraAtiva, regraRemovida)))
                .documentos(new ArrayList<>(List.of(docAtivo, docRemovido)))
                .build();

        when(pedestreMapper.toDomain(eq(entity), any(CycleAvoidingMappingContext.class))).thenReturn(mapped);

        Optional<Pedestre> result = pedestreGateway.findById(77L);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getRegras().size());
        assertEquals(1L, result.get().getRegras().getFirst().getId());
        assertEquals(1, result.get().getDocumentos().size());
        assertEquals(10L, result.get().getDocumentos().getFirst().getId());
        verify(pedestreRepository).findByIdComplete(77L);
    }
}
