package br.com.startjob.acesso.core.usecase.regra;

import br.com.startjob.acesso.core.domain.empresa.Empresa;
import br.com.startjob.acesso.core.domain.regra.Horario;
import br.com.startjob.acesso.core.domain.regra.Regra;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.TipoRegra;
import br.com.startjob.acesso.core.enumeration.WebPermissao;
import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.gateway.EmpresaGateway;
import br.com.startjob.acesso.core.gateway.RegraGateway;
import br.com.startjob.acesso.core.gateway.UsuarioGateway;
import br.com.startjob.acesso.core.usecase.login.WebPermissaoUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class SalvarRegraUseCase {

    private final RegraGateway regraGateway;
    private final EmpresaGateway empresaGateway;
    private final UsuarioGateway usuarioGateway;
    private final WebPermissaoUseCase webPermissaoUseCase;

    @Transactional
    public Regra execute(Regra regra) {
        if (regra == null) {
            throw new BusinessException("Dados da regra não informados.");
        }

        Usuario usuarioLogado = usuarioGateway.findLoggedUser()
                .orElseThrow(() -> new BusinessException("Usuário não está logado."));

        Set<String> permissoes = webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado);
        if (!permissoes.contains(WebPermissao.CONFIG_REGRAS_EDITAR.getCodigo())) {
            throw new BusinessException("Você não possui permissão para salvar regras de acesso.");
        }

        if (usuarioLogado.getCliente() == null || usuarioLogado.getCliente().getId() == null) {
            throw new BusinessException("Usuário logado não possui cliente associado.");
        }
        Long idClienteLogado = usuarioLogado.getCliente().getId();

        validarCamposBasicos(regra);
        tratarEmpresa(regra, idClienteLogado);

        boolean novaRegra = (regra.getId() == null);
        if (novaRegra) {
            regra.setCliente(usuarioLogado.getCliente());
            regra.setRemovido(false);
            validarNovosHorarios(regra);
        } else {
            Regra regraExistente = regraGateway.findById(regra.getId())
                    .orElseThrow(() -> new BusinessException("Regra não encontrada."));

            if (Boolean.TRUE.equals(regraExistente.getRemovido())) {
                throw new BusinessException("Regra não encontrada.");
            }

            if (regraExistente.getCliente() == null || !regraExistente.getCliente().getId().equals(idClienteLogado)) {
                throw new BusinessException("A regra não pertence ao seu cliente.");
            }

            regra.setCliente(regraExistente.getCliente());
            regra.setDataCriacao(regraExistente.getDataCriacao());
            regra.setRemovido(false);

            validarHorariosParaEdicao(regra, regraExistente);
        }

        limparCamposIncompativeisComTipo(regra);
        validarHorarios(regra);
        validarCamposPorTipo(regra);

        Regra salva = regraGateway.save(regra);
        log.info("Regra id={} salva com sucesso.", salva.getId());
        return salva;
    }

    private void validarCamposBasicos(Regra regra) {
        if (regra.getNome() == null || regra.getNome().isBlank()) {
            throw new BusinessException("Nome da regra é obrigatório.");
        }
        if (regra.getStatus() == null) {
            throw new BusinessException("Status é obrigatório.");
        }
        if (regra.getTipo() == null) {
            throw new BusinessException("Tipo de regra é obrigatório.");
        }
        if (regra.getTipoPedestre() == null) {
            throw new BusinessException("Tipo de pedestre é obrigatório.");
        }
    }

    private void tratarEmpresa(Regra regra, Long idClienteLogado) {
        if (regra.getEmpresa() != null && regra.getEmpresa().getId() != null) {
            Empresa empresa = empresaGateway.findById(regra.getEmpresa().getId())
                    .orElseThrow(() -> new BusinessException("Empresa não encontrada."));

            if (Boolean.TRUE.equals(empresa.getRemovido())) {
                throw new BusinessException("Empresa não encontrada.");
            }

            if (empresa.getCliente() == null || !empresa.getCliente().getId().equals(idClienteLogado)) {
                throw new BusinessException("A empresa não pertence ao seu cliente.");
            }

            regra.setEmpresa(empresa);
        } else {
            regra.setEmpresa(null);
        }
    }

    private void validarNovosHorarios(Regra regra) {
        if (regra.getHorarios() != null) {
            for (Horario horario : regra.getHorarios()) {
                if (horario.getId() != null) {
                    throw new BusinessException("Não é permitido informar identificador para novos horários.");
                }
            }
        }
    }

    private void validarHorariosParaEdicao(Regra regra, Regra regraExistente) {
        if (regra.getHorarios() != null) {
            Set<Long> existingHorarioIds = regraExistente.getHorarios() == null
                    ? Set.of()
                    : regraExistente.getHorarios().stream()
                            .filter(h -> h.getRemovido() == null || !h.getRemovido())
                            .map(Horario::getId)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet());

            for (Horario horario : regra.getHorarios()) {
                if (horario.getId() != null && !existingHorarioIds.contains(horario.getId())) {
                    throw new BusinessException("Horário informado não pertence a esta regra.");
                }
            }
        }
    }

    private void limparCamposIncompativeisComTipo(Regra regra) {
        TipoRegra tipo = regra.getTipo();
        if (tipo == null || !tipo.usaHorarios()) {
            regra.setHorarios(new ArrayList<>());
        }
        if (tipo == null || !tipo.usaCreditos()) {
            regra.setQtdeDeCreditos(null);
            regra.setDiasValidadeCredito(null);
        }
        if (tipo == null || (!tipo.usaPeriodo() && !tipo.usaEscala33())) {
            regra.setDataInicioPeriodo(null);
            regra.setDataFimPeriodo(null);
        }
        if (tipo == null || !tipo.usaEscala()) {
            regra.setTipoEscala(null);
            if (tipo == null || !tipo.usaEscala33()) {
                regra.setHorarioInicioTurno(null);
            }
        }
        if (TipoRegra.ACESSO_UNICO.equals(tipo)) {
            regra.setQtdeDeCreditos(1L);
            regra.setDiasValidadeCredito(null);
        }
        if (TipoRegra.ACESSO_HORARIO.equals(tipo)) {
            regra.setDataInicioPeriodo(null);
            regra.setDataFimPeriodo(null);
        }
        if (TipoRegra.ACESSO_PERIODO.equals(tipo) && regra.getHorarios() != null) {
            regra.getHorarios().forEach(horario -> horario.setQtdeDeCreditos(null));
        }
    }

    private void validarHorarios(Regra regra) {
        if (regra.getHorarios() != null && !regra.getHorarios().isEmpty()) {
            for (Horario horario : regra.getHorarios()) {
                if (horario.getNome() == null || horario.getNome().isBlank()) {
                    throw new BusinessException("Nome do horário é obrigatório.");
                }
                if (horario.getStatus() == null) {
                    throw new BusinessException("Status do horário é obrigatório.");
                }
                if (horario.getDiasSemana() == null || horario.getDiasSemana().isBlank()) {
                    throw new BusinessException("Adicione pelo menos um dia da semana.");
                }
                if (horario.getHorarioInicio() == null) {
                    throw new BusinessException("O horário de início é obrigatório.");
                }
                if (horario.getHorarioFim() == null) {
                    throw new BusinessException("O horário final é obrigatório.");
                }
            }
        }
    }

    private void validarCamposPorTipo(Regra regra) {
        TipoRegra tipo = regra.getTipo();
        if (TipoRegra.ACESSO_HORARIO.equals(tipo)) {
            if (regra.getHorarios() == null || regra.getHorarios().isEmpty()) {
                throw new BusinessException("Adicione pelo menos um horário.");
            }
        } else if (TipoRegra.ACESSO_PERIODO.equals(tipo)) {
            if (regra.getDataInicioPeriodo() == null) {
                throw new BusinessException("A data inicial do período é obrigatória.");
            } else if (regra.getDataFimPeriodo() == null) {
                throw new BusinessException("A data final do período é obrigatória.");
            }
        } else if (TipoRegra.ACESSO_ESCALA.equals(tipo)) {
            if (regra.getTipoEscala() == null) {
                throw new BusinessException("O tipo de escala é obrigatória.");
            } else if (regra.getHorarioInicioTurno() == null) {
                throw new BusinessException("O horário do início do turno é obrigatório.");
            }
        } else if (TipoRegra.ACESSO_CREDITO.equals(tipo)) {
            if (regra.getQtdeDeCreditos() == null) {
                throw new BusinessException("A quantidade de creditos deve ser informada.");
            }
        } else if (TipoRegra.ACESSO_UNICO.equals(tipo)) {
            regra.setQtdeDeCreditos(1L);
        } else if (TipoRegra.ACESSO_ESCALA_3_3.equals(tipo)) {
            if (regra.getDataInicioPeriodo() == null) {
                throw new BusinessException("A data inicial do período é obrigatória.");
            } else if (regra.getHorarioInicioTurno() == null) {
                throw new BusinessException("O horário do início do turno é obrigatório.");
            }
        }
    }
}
