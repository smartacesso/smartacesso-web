package br.com.startjob.acesso.core.usecase.regra;

import br.com.startjob.acesso.core.domain.cliente.Cliente;
import br.com.startjob.acesso.core.domain.empresa.Empresa;
import br.com.startjob.acesso.core.domain.regra.Horario;
import br.com.startjob.acesso.core.domain.regra.Regra;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.Status;
import br.com.startjob.acesso.core.enumeration.TipoPedestre;
import br.com.startjob.acesso.core.enumeration.TipoRegra;
import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.gateway.RegraGateway;
import br.com.startjob.acesso.core.gateway.UsuarioGateway;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaRegraUseCaseTest {

    @Mock
    private RegraGateway regraGateway;

    @Mock
    private UsuarioGateway usuarioGateway;

    @InjectMocks
    private ConsultaRegraUseCase consultaRegraUseCase;

    @Test
    @DisplayName("Deve consultar regras paginadas com sucesso para o cliente do usuário logado")
    void deveConsultarRegrasComSucesso() {
        Long idCliente = 10L;
        Cliente cliente = Cliente.builder().id(idCliente).nome("Cliente Teste").build();
        Usuario usuarioLogado = Usuario.builder().id(1L).nome("Admin").cliente(cliente).build();

        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));

        Empresa empresa = Empresa.builder().id(100L).nome("Empresa Matriz").build();
        Regra regra = Regra.builder()
                .id(50L)
                .nome("Regra Acesso Comercial")
                .status(Status.ATIVO)
                .empresa(empresa)
                .tipo(TipoRegra.ACESSO_HORARIO)
                .tipoPedestre(TipoPedestre.PEDESTRE)
                .validade(LocalDate.of(2026, 12, 31))
                .horarios(List.of(
                        Horario.builder().id(1L).nome("Horario Comercial").status(Status.ATIVO).build()
                ))
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Regra> page = new PageImpl<>(List.of(regra), pageable, 1);

        when(regraGateway.buscarRegras(idCliente, "Comercial", 100L, "Matriz", TipoPedestre.PEDESTRE, TipoRegra.ACESSO_HORARIO, pageable))
                .thenReturn(page);

        Page<Regra> resultado = consultaRegraUseCase.execute("Comercial", 100L, "Matriz", TipoPedestre.PEDESTRE, TipoRegra.ACESSO_HORARIO, pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        Regra regraResult = resultado.getContent().get(0);
        assertEquals(50L, regraResult.getId());
        assertEquals("Regra Acesso Comercial", regraResult.getNome());
        assertEquals(TipoRegra.ACESSO_HORARIO, regraResult.getTipo());
        assertEquals(TipoPedestre.PEDESTRE, regraResult.getTipoPedestre());
        assertEquals("Empresa Matriz", regraResult.getEmpresa().getNome());
        assertEquals(1, regraResult.getHorarios().size());

        verify(usuarioGateway, times(1)).findLoggedUser();
        verify(regraGateway, times(1)).buscarRegras(idCliente, "Comercial", 100L, "Matriz", TipoPedestre.PEDESTRE, TipoRegra.ACESSO_HORARIO, pageable);
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando usuário logado não for encontrado")
    void deveLancarExcecaoQuandoUsuarioNaoLogado() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.empty());

        Pageable pageable = PageRequest.of(0, 10);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> consultaRegraUseCase.execute(null, null, null, null, null, pageable));

        assertEquals("Usuário logado não encontrado", ex.getMessage());
        verify(regraGateway, never()).buscarRegras(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando usuário logado não possuir cliente associado")
    void deveLancarExcecaoQuandoUsuarioSemCliente() {
        Usuario usuarioLogado = Usuario.builder().id(1L).nome("Admin").cliente(null).build();
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));

        Pageable pageable = PageRequest.of(0, 10);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> consultaRegraUseCase.execute(null, null, null, null, null, pageable));

        assertEquals("Usuário logado não possui cliente associado", ex.getMessage());
        verify(regraGateway, never()).buscarRegras(any(), any(), any(), any(), any(), any(), any());
    }
}
