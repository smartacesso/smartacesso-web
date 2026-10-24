package br.com.startjob.acesso.core.usecase.parametro;

import br.com.startjob.acesso.core.domain.cliente.Cliente;
import br.com.startjob.acesso.core.domain.parametro.AtualizarParametroInput;
import br.com.startjob.acesso.core.domain.parametro.SecaoParametroGrupo;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.ParametroCatalogo;
import br.com.startjob.acesso.core.enumeration.PerfilAcesso;
import br.com.startjob.acesso.core.enumeration.WebPermissao;
import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.gateway.ParametroGateway;
import br.com.startjob.acesso.core.gateway.UsuarioGateway;
import br.com.startjob.acesso.core.usecase.login.WebPermissaoUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalvarParametrosUseCaseTest {

    @Mock
    private UsuarioGateway usuarioGateway;

    @Mock
    private ParametroGateway parametroGateway;

    @Mock
    private WebPermissaoUseCase webPermissaoUseCase;

    private ConsultaParametrosUseCase consultaParametrosUseCase;
    private SalvarParametrosUseCase salvarParametrosUseCase;

    private Usuario usuarioAdmin;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        consultaParametrosUseCase = new ConsultaParametrosUseCase(
                usuarioGateway, parametroGateway, webPermissaoUseCase);
        salvarParametrosUseCase = new SalvarParametrosUseCase(
                usuarioGateway, parametroGateway, webPermissaoUseCase, consultaParametrosUseCase);

        cliente = Cliente.builder().id(10L).nome("Cliente Teste").build();
        usuarioAdmin = Usuario.builder()
                .id(1L)
                .nome("Admin")
                .perfil(PerfilAcesso.ADMINISTRADOR)
                .cliente(cliente)
                .build();
    }

    @Test
    @DisplayName("Deve validar, formatar e persistir parâmetros de múltiplos tipos com sucesso")
    @SuppressWarnings("unchecked")
    void deveSalvarParametrosComSucesso() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioAdmin));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioAdmin))
                .thenReturn(Set.of(WebPermissao.CONFIG_PARAMETROS_EDITAR.getCodigo()));
        when(parametroGateway.findAllByCliente(10L)).thenReturn(List.of());

        List<AtualizarParametroInput> entradas = List.of(
                new AtualizarParametroInput("GERAR_MATRICULA_SEQUENCIAL", true),
                new AtualizarParametroInput("ESCOLHER_QTDE_DIGITOS_CARTAO", 12),
                new AtualizarParametroInput("FORMATO_DATA_PARA_IMPORTACAO", "yyyy-MM-dd"),
                new AtualizarParametroInput("CAMPOS_OBRIGATORIOS_CADASTRO_PEDESTRE", List.of("cpf", "email", "foto"))
        );

        List<SecaoParametroGrupo> resultado = salvarParametrosUseCase.execute(entradas);
        assertFalse(resultado.isEmpty());

        ArgumentCaptor<Map<String, String>> captor = ArgumentCaptor.forClass(Map.class);
        verify(parametroGateway).salvarParametrosCliente(eq(10L), captor.capture());

        Map<String, String> salvos = captor.getValue();
        assertEquals("true", salvos.get(ParametroCatalogo.GERAR_MATRICULA_SEQUENCIAL.getNomePersistido()));
        assertEquals("12", salvos.get(ParametroCatalogo.ESCOLHER_QTDE_DIGITOS_CARTAO.getNomePersistido()));
        assertEquals("yyyy-MM-dd", salvos.get(ParametroCatalogo.FORMATO_DATA_PARA_IMPORTACAO.getNomePersistido()));
        assertEquals("cpf,email,foto,", salvos.get(ParametroCatalogo.CAMPOS_OBRIGATORIOS_CADASTRO_PEDESTRE.getNomePersistido()));
    }

    @Test
    @DisplayName("Deve falhar quando usuário não possuir permissão CONFIG_PARAMETROS_EDITAR")
    void deveFalharQuandoSemPermissaoEditar() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioAdmin));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioAdmin))
                .thenReturn(Set.of(WebPermissao.CONFIG_PARAMETROS_VER.getCodigo()));

        List<AtualizarParametroInput> entradas = List.of(
                new AtualizarParametroInput("GERAR_MATRICULA_SEQUENCIAL", true)
        );

        BusinessException ex = assertThrows(BusinessException.class,
                () -> salvarParametrosUseCase.execute(entradas));
        assertEquals("Você não possui permissão para editar os parâmetros do sistema.", ex.getMessage());
        verify(parametroGateway, never()).salvarParametrosCliente(anyLong(), any());
    }

    @Test
    @DisplayName("Deve falhar quando código do parâmetro for desconhecido")
    void deveFalharQuandoCodigoDesconhecido() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioAdmin));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioAdmin))
                .thenReturn(Set.of(WebPermissao.CONFIG_PARAMETROS_EDITAR.getCodigo()));
        when(parametroGateway.findAllByCliente(10L)).thenReturn(List.of());

        List<AtualizarParametroInput> entradas = List.of(
                new AtualizarParametroInput("PARAMETRO_INEXISTENTE", "valor")
        );

        BusinessException ex = assertThrows(BusinessException.class,
                () -> salvarParametrosUseCase.execute(entradas));
        assertEquals("Parâmetro inválido ou desconhecido: PARAMETRO_INEXISTENTE", ex.getMessage());
        verify(parametroGateway, never()).salvarParametrosCliente(anyLong(), any());
    }
}
