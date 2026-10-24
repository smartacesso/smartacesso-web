package br.com.startjob.acesso.core.usecase.pedestre;

import br.com.startjob.acesso.core.domain.cliente.Cliente;
import br.com.startjob.acesso.core.domain.empresa.Cargo;
import br.com.startjob.acesso.core.domain.empresa.CentroCusto;
import br.com.startjob.acesso.core.domain.empresa.Departamento;
import br.com.startjob.acesso.core.domain.empresa.Empresa;
import br.com.startjob.acesso.core.domain.endereco.Endereco;
import br.com.startjob.acesso.core.domain.parametro.Parametro;
import br.com.startjob.acesso.core.domain.pedestre.Pedestre;
import br.com.startjob.acesso.core.domain.pedestre.PedestreRegra;
import br.com.startjob.acesso.core.domain.regra.Horario;
import br.com.startjob.acesso.core.domain.regra.Regra;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.Genero;
import br.com.startjob.acesso.core.enumeration.ParametroCatalogo;
import br.com.startjob.acesso.core.enumeration.PerfilAcessoApp;
import br.com.startjob.acesso.core.enumeration.Status;
import br.com.startjob.acesso.core.enumeration.TipoPedestre;
import br.com.startjob.acesso.core.enumeration.TipoRegra;
import br.com.startjob.acesso.core.enumeration.WebPermissao;
import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.gateway.EmpresaGateway;
import br.com.startjob.acesso.core.gateway.ParametroGateway;
import br.com.startjob.acesso.core.gateway.PasswordGateway;
import br.com.startjob.acesso.core.gateway.PedestreGateway;
import br.com.startjob.acesso.core.gateway.RegraGateway;
import br.com.startjob.acesso.core.gateway.UsuarioGateway;
import br.com.startjob.acesso.core.usecase.login.WebPermissaoUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalvarPedestreUseCaseTest {

    @Mock
    private PedestreGateway pedestreGateway;

    @Mock
    private EmpresaGateway empresaGateway;

    @Mock
    private RegraGateway regraGateway;

    @Mock
    private ParametroGateway parametroGateway;

    @Mock
    private UsuarioGateway usuarioGateway;

    @Mock
    private WebPermissaoUseCase webPermissaoUseCase;

    @Mock
    private PasswordGateway passwordGateway;

    @InjectMocks
    private SalvarPedestreUseCase salvarPedestreUseCase;

    private Cliente cliente;
    private Usuario usuarioLogado;
    private Empresa empresa;
    private Departamento departamento;
    private CentroCusto centroCusto;
    private Cargo cargo;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder().id(10L).nome("Cliente Teste").build();
        usuarioLogado = Usuario.builder().id(1L).nome("Operador").cliente(cliente).build();
        departamento = Departamento.builder().id(11L).nome("TI").status(Status.ATIVO).removido(false).build();
        centroCusto = CentroCusto.builder().id(12L).nome("CC-01").status(Status.ATIVO).removido(false).build();
        cargo = Cargo.builder().id(13L).nome("Analista").status(Status.ATIVO).removido(false).build();
        empresa = Empresa.builder()
                .id(100L)
                .nome("Empresa Matriz")
                .cliente(cliente)
                .removido(false)
                .departamentos(List.of(departamento))
                .centros(List.of(centroCusto))
                .cargos(List.of(cargo))
                .build();
    }

    private void mockUsuarioAutorizadoPedestre() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.PEDESTRE_EDITAR.getCodigo()));
    }

    private void mockUsuarioAutorizadoVisitante() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.VISITANTE_EDITAR.getCodigo()));
    }

    @Test
    @DisplayName("Deve cadastrar novo pedestre com empresa, depto, cargo, centro de custo, endereço e regra de horário")
    void deveCadastrarNovoPedestreCompletoComSucesso() {
        mockUsuarioAutorizadoPedestre();
        when(parametroGateway.findAllByCliente(10L)).thenReturn(List.of(
                Parametro.builder().nome(ParametroCatalogo.GERAR_MATRICULA_SEQUENCIAL.getNomePersistido()).valor("true").build(),
                Parametro.builder().nome(ParametroCatalogo.LOCAL_PADRAO_PEDESTRE.getNomePersistido()).valor("uuid-portaria-1").build()
        ));
        when(pedestreGateway.findUltimaMatriculaByCliente(10L)).thenReturn(Optional.of("1049"));
        when(empresaGateway.findById(100L)).thenReturn(Optional.of(empresa));

        Horario horarioTemplate = Horario.builder()
                .id(1L)
                .nome("Comercial")
                .status(Status.ATIVO)
                .diasSemana("12345")
                .horarioInicio(LocalDateTime.of(2026, 1, 1, 8, 0))
                .horarioFim(LocalDateTime.of(2026, 1, 1, 18, 0))
                .build();

        Regra regraHorario = Regra.builder()
                .id(50L)
                .nome("Regra Horário Comercial")
                .tipo(TipoRegra.ACESSO_HORARIO)
                .tipoPedestre(TipoPedestre.PEDESTRE)
                .cliente(cliente)
                .removido(false)
                .horarios(List.of(horarioTemplate))
                .build();

        when(regraGateway.findById(50L)).thenReturn(Optional.of(regraHorario));
        when(passwordGateway.hash("Senha123")).thenReturn("HASH_SENHA_123");
        when(pedestreGateway.save(any(Pedestre.class))).thenAnswer(inv -> {
            Pedestre p = inv.getArgument(0);
            p.setId(777L);
            return p;
        });

        Pedestre novo = Pedestre.builder()
                .nome("  João da Silva  ")
                .tipo(TipoPedestre.PEDESTRE)
                .cpf("529.982.247-25")
                .codigoCartaoAcesso("00012345")
                .genero(Genero.MASCULINO)
                .empresa(Empresa.builder().id(100L).build())
                .departamento(Departamento.builder().id(11L).build())
                .centroCusto(CentroCusto.builder().id(12L).build())
                .cargo(Cargo.builder().id(13L).build())
                .empresaVisitada("Deve ser limpa para pedestre")
                .endereco(Endereco.builder().cep("01001-000").logradouro("Praça da Sé").build())
                .login("joao.silva")
                .senha("Senha123")
                .perfilApp(PerfilAcessoApp.COMUM)
                .regras(List.of(PedestreRegra.builder()
                        .regra(Regra.builder().id(50L).build())
                        .build()))
                .build();

        Pedestre salvo = salvarPedestreUseCase.execute(novo);

        assertNotNull(salvo);
        assertEquals(777L, salvo.getId());
        assertEquals("João da Silva", salvo.getNome());
        assertEquals("52998224725", salvo.getCpf());
        assertEquals("12345", salvo.getCodigoCartaoAcesso());
        assertEquals("1050", salvo.getMatricula());
        assertEquals("uuid-portaria-1", salvo.getUuidLocal());
        assertNull(salvo.getEmpresaVisitada());
        assertEquals("HASH_SENHA_123", salvo.getSenha());
        assertEquals(1, salvo.getRegras().size());
        assertEquals(1, salvo.getRegras().get(0).getHorarios().size());
        assertEquals("Comercial", salvo.getRegras().get(0).getHorarios().get(0).getNome());
    }

    @Test
    @DisplayName("Deve cadastrar visitante aplicando regra padrão ACESSO_UNICO_VISITANTE e limpando vínculos organizacionais")
    void deveCadastrarVisitanteComRegraPadraoAutomatica() {
        mockUsuarioAutorizadoVisitante();
        when(parametroGateway.findAllByCliente(10L)).thenReturn(List.of(
                Parametro.builder().nome(ParametroCatalogo.PREENCHER_CARTAO_AUTO.getNomePersistido()).valor("true").build(),
                Parametro.builder().nome(ParametroCatalogo.ESCOLHER_QTDE_DIGITOS_CARTAO.getNomePersistido()).valor("8").build(),
                Parametro.builder().nome(ParametroCatalogo.LOCAL_PADRAO_VISITANTE.getNomePersistido()).valor("uuid-recepcao").build()
        ));
        when(empresaGateway.findById(100L)).thenReturn(Optional.of(empresa));

        Regra regraPadraoSalva = Regra.builder()
                .id(900L)
                .nome(SalvarPedestreUseCase.NOME_REGRA_PADRAO_VISITANTE)
                .tipo(TipoRegra.ACESSO_UNICO)
                .tipoPedestre(TipoPedestre.VISITANTE)
                .status(Status.ATIVO)
                .qtdeDeCreditos(1L)
                .cliente(cliente)
                .build();

        when(regraGateway.findByNomeAndCliente(SalvarPedestreUseCase.NOME_REGRA_PADRAO_VISITANTE, 10L))
                .thenReturn(Optional.empty());
        when(regraGateway.save(any(Regra.class))).thenReturn(regraPadraoSalva);
        when(pedestreGateway.save(any(Pedestre.class))).thenAnswer(inv -> inv.getArgument(0));

        Pedestre visitante = Pedestre.builder()
                .nome("Maria Visitante")
                .tipo(TipoPedestre.VISITANTE)
                .empresa(Empresa.builder().id(100L).build()) // deve ser limpo
                .departamento(Departamento.builder().id(11L).build()) // deve ser limpo
                .empresaVisitadaRef(Empresa.builder().id(100L).build())
                .build();

        Pedestre salvo = salvarPedestreUseCase.execute(visitante);

        assertNull(salvo.getEmpresa());
        assertNull(salvo.getDepartamento());
        assertNotNull(salvo.getEmpresaVisitadaRef());
        assertEquals("Empresa Matriz", salvo.getEmpresaVisitada());
        assertEquals("uuid-recepcao", salvo.getUuidLocal());
        assertNotNull(salvo.getCodigoCartaoAcesso());
        assertEquals(1, salvo.getRegras().size());
        assertEquals(900L, salvo.getRegras().get(0).getRegra().getId());
        assertEquals(1L, salvo.getRegras().get(0).getQtdeTotalDeCreditos());
    }

    @Test
    @DisplayName("Deve falhar quando usuário não possui permissão específica para o tipo (PEDESTRE vs VISITANTE)")
    void deveFalharQuandoSemPermissaoPorTipo() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.VISITANTE_EDITAR.getCodigo()));

        Pedestre pedestre = Pedestre.builder().nome("Pedestre").tipo(TipoPedestre.PEDESTRE).build();

        BusinessException ex = assertThrows(BusinessException.class, () -> salvarPedestreUseCase.execute(pedestre));
        assertEquals("Você não possui permissão para salvar pedestres.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve validar campos obrigatórios dinâmicos configurados no cliente")
    void deveValidarCamposObrigatoriosDinamicos() {
        mockUsuarioAutorizadoPedestre();
        when(parametroGateway.findAllByCliente(10L)).thenReturn(List.of(
                Parametro.builder()
                        .nome(ParametroCatalogo.CAMPOS_OBRIGATORIOS_CADASTRO_PEDESTRE.getNomePersistido())
                        .valor("cpf,email,foto")
                        .build()
        ));

        Pedestre semEmail = Pedestre.builder().nome("João").tipo(TipoPedestre.PEDESTRE).build();
        assertEquals("E-mail é obrigatório.",
                assertThrows(BusinessException.class, () -> salvarPedestreUseCase.execute(semEmail)).getMessage());

        Pedestre semCpf = Pedestre.builder().nome("João").tipo(TipoPedestre.PEDESTRE).email("joao@teste.com").build();
        assertEquals("CPF é obrigatório.",
                assertThrows(BusinessException.class, () -> salvarPedestreUseCase.execute(semCpf)).getMessage());

        Pedestre semFoto = Pedestre.builder()
                .nome("João")
                .tipo(TipoPedestre.PEDESTRE)
                .cpf("52998224725")
                .email("joao@teste.com")
                .build();
        assertEquals("A foto é obrigatória.",
                assertThrows(BusinessException.class, () -> salvarPedestreUseCase.execute(semFoto)).getMessage());
    }

    @Test
    @DisplayName("Deve validar dígito verificador de CPF e duplicidades de CPF, RG, Matrícula e Cartão")
    void deveValidarCpfEUnicidades() {
        mockUsuarioAutorizadoPedestre();
        when(parametroGateway.findAllByCliente(10L)).thenReturn(List.of(
                Parametro.builder().nome(ParametroCatalogo.VALIDA_CPF_VALIDO.getNomePersistido()).valor("true").build(),
                Parametro.builder().nome(ParametroCatalogo.VALIDAR_CPF_DUPLICADO.getNomePersistido()).valor("true").build(),
                Parametro.builder().nome(ParametroCatalogo.VALIDAR_RG_DUPLICADO.getNomePersistido()).valor("true").build(),
                Parametro.builder().nome(ParametroCatalogo.VALIDAR_MATRICULAS_DUPLICADAS.getNomePersistido()).valor("true").build(),
                Parametro.builder().nome(ParametroCatalogo.VALIDAR_CARTAO_ACESSO_DUPLICADO.getNomePersistido()).valor("true").build()
        ));

        Pedestre cpfInvalido = Pedestre.builder()
                .nome("João")
                .cpf("111.111.111-11")
                .build();
        assertEquals("CPF inválido.",
                assertThrows(BusinessException.class, () -> salvarPedestreUseCase.execute(cpfInvalido)).getMessage());

        when(pedestreGateway.existsAtivoByCpfAndCliente("52998224725", 10L, null)).thenReturn(true);
        Pedestre cpfDuplicado = Pedestre.builder()
                .nome("João")
                .cpf("529.982.247-25")
                .build();
        assertEquals("CPF já cadastrado para outro pedestre.",
                assertThrows(BusinessException.class, () -> salvarPedestreUseCase.execute(cpfDuplicado)).getMessage());

        when(pedestreGateway.existsAtivoByCpfAndCliente("52998224725", 10L, null)).thenReturn(false);
        when(pedestreGateway.existsAtivoByCartaoNormalizadoAndCliente("9988", 10L, null)).thenReturn(true);
        Pedestre cartaoDuplicado = Pedestre.builder()
                .nome("João")
                .cpf("529.982.247-25")
                .codigoCartaoAcesso("0009988")
                .build();
        assertEquals("Cartão de acesso já cadastrado para outro pedestre ativo.",
                assertThrows(BusinessException.class, () -> salvarPedestreUseCase.execute(cartaoDuplicado)).getMessage());
    }

    @Test
    @DisplayName("Deve impedir mais de uma regra ativa para o mesmo pedestre")
    void deveImpedirMaisDeUmaRegraAtiva() {
        mockUsuarioAutorizadoPedestre();
        when(parametroGateway.findAllByCliente(10L)).thenReturn(List.of());

        Pedestre comDuasRegras = Pedestre.builder()
                .nome("João")
                .regras(List.of(
                        PedestreRegra.builder().regra(Regra.builder().id(1L).build()).build(),
                        PedestreRegra.builder().regra(Regra.builder().id(2L).build()).build()
                ))
                .build();

        BusinessException ex = assertThrows(BusinessException.class, () -> salvarPedestreUseCase.execute(comDuasRegras));
        assertEquals("Não é permitido cadastrar mais de uma regra ativa para o pedestre.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve editar pedestre existente preservando foto e senha quando não alteradas")
    void deveEditarPedestreExistentePreservandoFotoESenha() {
        mockUsuarioAutorizadoPedestre();
        when(parametroGateway.findAllByCliente(10L)).thenReturn(List.of());

        byte[] fotoOriginal = new byte[]{1, 2, 3};
        LocalDateTime dataFotoOriginal = LocalDateTime.of(2026, 1, 10, 12, 0);

        Pedestre existente = Pedestre.builder()
                .id(55L)
                .nome("João Antigo")
                .tipo(TipoPedestre.PEDESTRE)
                .cliente(cliente)
                .foto(fotoOriginal)
                .dataAlteracaoFoto(dataFotoOriginal)
                .login("joao")
                .senha("HASH_ANTIGO")
                .endereco(Endereco.builder().id(300L).cep("01001000").build())
                .removido(false)
                .build();

        when(pedestreGateway.findById(55L)).thenReturn(Optional.of(existente));
        when(pedestreGateway.save(any(Pedestre.class))).thenAnswer(inv -> inv.getArgument(0));

        Pedestre editado = Pedestre.builder()
                .id(55L)
                .nome("João Atualizado")
                .tipo(TipoPedestre.PEDESTRE)
                .login("joao")
                .endereco(Endereco.builder().cep("02002000").logradouro("Rua Nova").build())
                .dataNascimento(LocalDate.of(1990, 5, 20))
                .build();

        Pedestre salvo = salvarPedestreUseCase.execute(editado, false);

        assertEquals("João Atualizado", salvo.getNome());
        assertSame(fotoOriginal, salvo.getFoto());
        assertEquals(dataFotoOriginal, salvo.getDataAlteracaoFoto());
        assertEquals("HASH_ANTIGO", salvo.getSenha());
        assertEquals(300L, salvo.getEndereco().getId());
    }
}
