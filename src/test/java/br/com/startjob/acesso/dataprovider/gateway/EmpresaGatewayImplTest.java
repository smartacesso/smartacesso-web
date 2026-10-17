package br.com.startjob.acesso.dataprovider.gateway;

import br.com.startjob.acesso.core.domain.empresa.Cargo;
import br.com.startjob.acesso.core.domain.empresa.CentroCusto;
import br.com.startjob.acesso.core.domain.empresa.Departamento;
import br.com.startjob.acesso.core.domain.empresa.Empresa;
import br.com.startjob.acesso.core.domain.endereco.Endereco;
import br.com.startjob.acesso.core.enumeration.Status;
import br.com.startjob.acesso.dataprovider.entity.CargoEntity;
import br.com.startjob.acesso.dataprovider.entity.CentroCustoEntity;
import br.com.startjob.acesso.dataprovider.entity.DepartamentoEntity;
import br.com.startjob.acesso.dataprovider.entity.EmpresaEntity;
import br.com.startjob.acesso.dataprovider.entity.EnderecoEntity;
import br.com.startjob.acesso.dataprovider.mapper.*;
import br.com.startjob.acesso.dataprovider.repository.EmpresaRepository;
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
class EmpresaGatewayImplTest {

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private EmpresaMapper empresaMapper;

    @Mock
    private DepartamentoMapper departamentoMapper;

    @Mock
    private CentroCustoMapper centroCustoMapper;

    @Mock
    private CargoMapper cargoMapper;

    @Mock
    private EnderecoMapper enderecoMapper;

    @InjectMocks
    private EmpresaGatewayImpl empresaGateway;

    @Test
    @DisplayName("save deve associar departamentos, centros e cargos à nova EmpresaEntity e salvar")
    void deveSalvarNovaEmpresaAssociandoFilhos() {
        Departamento dep = Departamento.builder().nome("TI").status(Status.ATIVO).build();
        CentroCusto centro = CentroCusto.builder().nome("Financeiro").status(Status.ATIVO).build();
        Cargo cargo = Cargo.builder().nome("Dev").status(Status.ATIVO).build();

        Empresa empresa = Empresa.builder()
                .nome("Nova Empresa")
                .cnpj("12.345.678/0001-90")
                .departamentos(List.of(dep))
                .centros(List.of(centro))
                .cargos(List.of(cargo))
                .build();

        DepartamentoEntity depEntity = new DepartamentoEntity();
        depEntity.setNome("TI");

        CentroCustoEntity centroEntity = new CentroCustoEntity();
        centroEntity.setNome("Financeiro");

        CargoEntity cargoEntity = new CargoEntity();
        cargoEntity.setNome("Dev");

        EmpresaEntity entity = new EmpresaEntity();
        entity.setNome("Nova Empresa");
        entity.setDepartamentos(new ArrayList<>(List.of(depEntity)));
        entity.setCentros(new ArrayList<>(List.of(centroEntity)));
        entity.setCargos(new ArrayList<>(List.of(cargoEntity)));

        when(empresaMapper.toEntity(eq(empresa), any())).thenReturn(entity);
        when(empresaRepository.save(entity)).thenReturn(entity);

        Empresa domainRetornado = Empresa.builder()
                .id(1L)
                .nome("Nova Empresa")
                .departamentos(List.of(dep))
                .centros(List.of(centro))
                .cargos(List.of(cargo))
                .build();
        when(empresaMapper.toDomain(eq(entity), any())).thenReturn(domainRetornado);

        Empresa resultado = empresaGateway.save(empresa);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertSame(entity, depEntity.getEmpresa());
        assertSame(entity, centroEntity.getEmpresa());
        assertSame(entity, cargoEntity.getEmpresa());
        assertEquals(Boolean.FALSE, depEntity.getRemovido());
        assertEquals(Boolean.FALSE, centroEntity.getRemovido());
        assertEquals(Boolean.FALSE, cargoEntity.getRemovido());
        verify(empresaRepository, times(1)).save(entity);
    }

    @Test
    @DisplayName("save deve atualizar existente, sincronizar novos, alterados e marcar ausentes como removidos")
    void deveAtualizarEmpresaExistenteSincronizandoFilhos() {
        EmpresaEntity existing = new EmpresaEntity();
        existing.setId(10L);
        existing.setNome("Nome Antigo");

        DepartamentoEntity depExistente1 = new DepartamentoEntity();
        depExistente1.setId(101L);
        depExistente1.setNome("Dep 1");
        depExistente1.setStatus(Status.ATIVO);
        depExistente1.setRemovido(false);

        DepartamentoEntity depExistente2 = new DepartamentoEntity();
        depExistente2.setId(102L);
        depExistente2.setNome("Dep 2 (Remover)");
        depExistente2.setStatus(Status.ATIVO);
        depExistente2.setRemovido(false);

        existing.setDepartamentos(new ArrayList<>(List.of(depExistente1, depExistente2)));
        existing.setCentros(new ArrayList<>());
        existing.setCargos(new ArrayList<>());

        when(empresaRepository.findById(10L)).thenReturn(Optional.of(existing));

        Departamento novoDep = Departamento.builder().nome("Dep 3 Novo").status(Status.ATIVO).build();
        Departamento dep1Atualizado = Departamento.builder().id(101L).nome("Dep 1 Atualizado").status(Status.ATIVO).build();

        Empresa empresaToSave = Empresa.builder()
                .id(10L)
                .nome("Nome Atualizado")
                .departamentos(List.of(dep1Atualizado, novoDep))
                .centros(List.of())
                .cargos(List.of())
                .build();

        DepartamentoEntity newDepEntity = new DepartamentoEntity();
        newDepEntity.setNome("Dep 3 Novo");
        newDepEntity.setStatus(Status.ATIVO);
        when(departamentoMapper.toEntity(eq(novoDep), any())).thenReturn(newDepEntity);

        when(empresaRepository.save(existing)).thenReturn(existing);

        Empresa domainMock = Empresa.builder()
                .id(10L)
                .nome("Nome Atualizado")
                .departamentos(List.of(
                        Departamento.builder().id(101L).nome("Dep 1 Atualizado").status(Status.ATIVO).build(),
                        Departamento.builder().id(103L).nome("Dep 3 Novo").status(Status.ATIVO).build()
                ))
                .build();
        when(empresaMapper.toDomain(eq(existing), any())).thenReturn(domainMock);

        Empresa resultado = empresaGateway.save(empresaToSave);

        assertNotNull(resultado);
        assertEquals("Nome Atualizado", existing.getNome());
        assertEquals("Dep 1 Atualizado", depExistente1.getNome());
        assertEquals(Boolean.FALSE, depExistente1.getRemovido());
        assertEquals(Boolean.TRUE, depExistente2.getRemovido());
        assertNotNull(depExistente2.getDataRemovido());
        assertTrue(existing.getDepartamentos().contains(newDepEntity));
        assertSame(existing, newDepEntity.getEmpresa());
        verify(empresaRepository, times(1)).save(existing);
    }

    @Test
    @DisplayName("findById deve buscar EmpresaEntity pelo id e mapear para domain filtrando filhos ativos")
    void deveBuscarPorIdComSucessoEFiltrarFilhosRemovidos() {
        EmpresaEntity entity = new EmpresaEntity();
        entity.setId(10L);
        entity.setNome("Empresa ABC");

        when(empresaRepository.findByIdWithCliente(10L)).thenReturn(Optional.of(entity));

        Departamento depAtivo = Departamento.builder().id(1L).nome("TI").removido(false).build();
        Departamento depRemovido = Departamento.builder().id(2L).nome("Antigo TI").removido(true).build();
        CentroCusto centroAtivo = CentroCusto.builder().id(3L).nome("Centro Ativo").removido(false).build();
        CentroCusto centroRemovido = CentroCusto.builder().id(4L).nome("Centro Removido").removido(true).build();
        Cargo cargoAtivo = Cargo.builder().id(5L).nome("Cargo Ativo").removido(false).build();
        Cargo cargoRemovido = Cargo.builder().id(6L).nome("Cargo Removido").removido(true).build();

        Empresa domain = Empresa.builder()
                .id(10L)
                .nome("Empresa ABC")
                .departamentos(List.of(depAtivo, depRemovido))
                .centros(List.of(centroAtivo, centroRemovido))
                .cargos(List.of(cargoAtivo, cargoRemovido))
                .build();

        when(empresaMapper.toDomain(eq(entity), any())).thenReturn(domain);

        Optional<Empresa> resultado = empresaGateway.findById(10L);

        assertTrue(resultado.isPresent());
        Empresa empresa = resultado.get();
        assertEquals(10L, empresa.getId());
        assertEquals(1, empresa.getDepartamentos().size());
        assertEquals("TI", empresa.getDepartamentos().get(0).getNome());
        assertEquals(1, empresa.getCentros().size());
        assertEquals("Centro Ativo", empresa.getCentros().get(0).getNome());
        assertEquals(1, empresa.getCargos().size());
        assertEquals("Cargo Ativo", empresa.getCargos().get(0).getNome());
        verify(empresaRepository, times(1)).findByIdWithCliente(10L);
    }

    @Test
    @DisplayName("save deve associar endereco preenchido à nova EmpresaEntity e salvar")
    void deveSalvarNovaEmpresaComEndereco() {
        Endereco endereco = Endereco.builder()
                .cep("38400-000")
                .logradouro("Av. Central")
                .numero("100")
                .bairro("Centro")
                .cidade("Uberlândia")
                .estado("MG")
                .build();

        Empresa empresa = Empresa.builder()
                .nome("Nova Empresa com Endereço")
                .cnpj("12.345.678/0001-90")
                .endereco(endereco)
                .build();

        EnderecoEntity enderecoEntity = new EnderecoEntity();
        enderecoEntity.setLogradouro("Av. Central");

        EmpresaEntity entity = new EmpresaEntity();
        entity.setNome("Nova Empresa com Endereço");
        entity.setEndereco(enderecoEntity);

        when(empresaMapper.toEntity(eq(empresa), any())).thenReturn(entity);
        when(empresaRepository.save(entity)).thenReturn(entity);

        Empresa domainRetornado = Empresa.builder()
                .id(1L)
                .nome("Nova Empresa com Endereço")
                .endereco(endereco)
                .build();
        when(empresaMapper.toDomain(eq(entity), any())).thenReturn(domainRetornado);

        Empresa resultado = empresaGateway.save(empresa);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(Boolean.FALSE, enderecoEntity.getRemovido());
        verify(empresaRepository, times(1)).save(entity);
    }

    @Test
    @DisplayName("save deve atualizar campos do endereco existente ao editar empresa")
    void deveAtualizarEnderecoExistenteAoEditarEmpresa() {
        EmpresaEntity existing = new EmpresaEntity();
        existing.setId(10L);
        existing.setNome("Empresa");

        EnderecoEntity existingEnd = new EnderecoEntity();
        existingEnd.setId(50L);
        existingEnd.setLogradouro("Rua Antiga");
        existingEnd.setCidade("Uberaba");
        existing.setEndereco(existingEnd);
        existing.setDepartamentos(new ArrayList<>());
        existing.setCentros(new ArrayList<>());
        existing.setCargos(new ArrayList<>());

        when(empresaRepository.findById(10L)).thenReturn(Optional.of(existing));

        Endereco novoEndereco = Endereco.builder()
                .logradouro("Rua Nova")
                .numero("200")
                .bairro("Bairro Novo")
                .cidade("Uberlândia")
                .estado("MG")
                .cep("38400-111")
                .build();

        Empresa empresaToSave = Empresa.builder()
                .id(10L)
                .nome("Empresa")
                .endereco(novoEndereco)
                .build();

        when(empresaRepository.save(existing)).thenReturn(existing);

        Empresa domainRetornado = Empresa.builder()
                .id(10L)
                .nome("Empresa")
                .endereco(novoEndereco)
                .build();
        when(empresaMapper.toDomain(eq(existing), any())).thenReturn(domainRetornado);

        Empresa resultado = empresaGateway.save(empresaToSave);

        assertNotNull(resultado);
        assertEquals("Rua Nova", existingEnd.getLogradouro());
        assertEquals("Uberlândia", existingEnd.getCidade());
        assertEquals("200", existingEnd.getNumero());
        verify(empresaRepository, times(1)).save(existing);
    }
}
