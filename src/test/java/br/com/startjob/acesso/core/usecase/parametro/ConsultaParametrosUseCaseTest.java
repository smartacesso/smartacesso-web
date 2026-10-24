package br.com.startjob.acesso.core.usecase.parametro;

import br.com.startjob.acesso.core.domain.cliente.Cliente;
import br.com.startjob.acesso.core.domain.parametro.Parametro;
import br.com.startjob.acesso.core.domain.parametro.ParametroItem;
import br.com.startjob.acesso.core.domain.parametro.SecaoParametroGrupo;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.ParametroCatalogo;
import br.com.startjob.acesso.core.enumeration.PerfilAcesso;
import br.com.startjob.acesso.core.enumeration.SecaoParametro;
import br.com.startjob.acesso.core.enumeration.WebPermissao;
import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.gateway.ParametroGateway;
import br.com.startjob.acesso.core.gateway.UsuarioGateway;
import br.com.startjob.acesso.core.usecase.login.WebPermissaoUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultaParametrosUseCaseTest {

    @Mock
    private UsuarioGateway usuarioGateway;

    @Mock
    private ParametroGateway parametroGateway;

    @Mock
    private WebPermissaoUseCase webPermissaoUseCase;

    private ConsultaParametrosUseCase consultaParametrosUseCase;
    private Usuario usuario;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        consultaParametrosUseCase = new ConsultaParametrosUseCase(
                usuarioGateway, parametroGateway, webPermissaoUseCase);

        cliente = Cliente.builder().id(10L).nome("Unidade Matriz").build();
        usuario = Usuario.builder()
                .id(1L)
                .nome("Operador")
                .perfil(PerfilAcesso.OPERADOR)
                .cliente(cliente)
                .build();
    }

    @Test
    @DisplayName("Deve retornar parâmetros agrupados por seção mesclando valores salvos com padrões do catálogo")
    void deveConsultarParametrosComSucesso() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuario));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuario))
                .thenReturn(Set.of(WebPermissao.CONFIG_PARAMETROS_VER.getCodigo()));

        Parametro paramSalvo1 = Parametro.builder()
                .id(1L)
                .nome(ParametroCatalogo.GERAR_MATRICULA_SEQUENCIAL.getNomePersistido())
                .valor("true")
                .cliente(cliente)
                .build();
        Parametro paramSalvo2 = Parametro.builder()
                .id(2L)
                .nome(ParametroCatalogo.CAMPOS_OBRIGATORIOS_CADASTRO_PEDESTRE.getNomePersistido())
                .valor("cpf,foto,")
                .cliente(cliente)
                .build();

        when(parametroGateway.findAllByCliente(10L)).thenReturn(List.of(paramSalvo1, paramSalvo2));

        List<SecaoParametroGrupo> secoes = consultaParametrosUseCase.execute();

        assertFalse(secoes.isEmpty());

        SecaoParametroGrupo secaoCadastro = secoes.stream()
                .filter(s -> s.getSecao() == SecaoParametro.CADASTRO)
                .findFirst()
                .orElseThrow();

        ParametroItem itemMatricula = secaoCadastro.getParametros().stream()
                .filter(p -> p.getCodigo().equals("GERAR_MATRICULA_SEQUENCIAL"))
                .findFirst()
                .orElseThrow();
        assertEquals(Boolean.TRUE, itemMatricula.getValor());
        assertEquals(Boolean.FALSE, itemMatricula.getValorPadrao());

        ParametroItem itemCampos = secaoCadastro.getParametros().stream()
                .filter(p -> p.getCodigo().equals("CAMPOS_OBRIGATORIOS_CADASTRO_PEDESTRE"))
                .findFirst()
                .orElseThrow();
        assertEquals(List.of("cpf", "foto"), itemCampos.getValor());
        assertNotNull(itemCampos.getOpcoes());
        assertFalse(itemCampos.getOpcoes().isEmpty());
    }

    @Test
    @DisplayName("Deve falhar quando usuário não tiver permissão CONFIG_PARAMETROS_VER nem CONFIG_PARAMETROS_EDITAR")
    void deveFalharQuandoSemPermissao() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuario));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuario)).thenReturn(Set.of());

        BusinessException ex = assertThrows(BusinessException.class, () -> consultaParametrosUseCase.execute());
        assertEquals("Você não possui permissão para visualizar os parâmetros do sistema.", ex.getMessage());
    }
}
