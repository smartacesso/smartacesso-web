package br.com.startjob.acesso.entrypoint.api.dto;

import br.com.startjob.acesso.core.domain.empresa.Cargo;
import br.com.startjob.acesso.core.domain.empresa.CentroCusto;
import br.com.startjob.acesso.core.domain.empresa.Departamento;
import br.com.startjob.acesso.core.domain.empresa.Empresa;
import br.com.startjob.acesso.core.domain.pedestre.Pedestre;
import br.com.startjob.acesso.core.enumeration.Genero;
import br.com.startjob.acesso.core.enumeration.PerfilAcessoApp;
import br.com.startjob.acesso.core.enumeration.Status;
import br.com.startjob.acesso.core.enumeration.TipoPedestre;
import br.com.startjob.acesso.core.enumeration.TipoQRCode;
import br.com.startjob.acesso.core.exception.BusinessException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Builder
public record SalvarPedestreRequest(
        Long id,
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
        String nome,
        Status status,
        TipoPedestre tipo,
        @Email(message = "E-mail inválido")
        @Size(max = 100, message = "E-mail deve ter no máximo 100 caracteres")
        String email,
        @Size(max = 20, message = "CPF deve ter no máximo 20 caracteres")
        String cpf,
        @Size(max = 20, message = "RG deve ter no máximo 20 caracteres")
        String rg,
        @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
        String telefone,
        @Size(max = 20, message = "Celular deve ter no máximo 20 caracteres")
        String celular,
        @Size(max = 200, message = "Matrícula deve ter no máximo 200 caracteres")
        String matricula,
        @Size(max = 50, message = "Código do cartão de acesso deve ter no máximo 50 caracteres")
        String codigoCartaoAcesso,
        @Size(max = 300, message = "Observações devem ter no máximo 300 caracteres")
        String observacoes,
        TipoQRCode tipoQRCode,
        LocalDate dataNascimento,
        Genero genero,
        Boolean sempreLiberado,
        Boolean acessoLivre,
        Boolean habilitarTeclado,
        Boolean cadastroFacialObrigatorio,
        Boolean enviaSmsAoPassarNaCatraca,
        String uuidLocal,
        String fotoBase64,
        Boolean removerFoto,
        Long idEmpresa,
        Long idDepartamento,
        Long idCentroCusto,
        Long idCargo,
        @Size(max = 200, message = "Empresa visitada deve ter no máximo 200 caracteres")
        String empresaVisitada,
        Long idEmpresaVisitada,
        @Size(max = 100, message = "Login deve ter no máximo 100 caracteres")
        String login,
        String senha,
        PerfilAcessoApp perfilApp,
        String justificativa,
        LocalDateTime dataInicioPeriodoAgendamento,
        LocalDateTime dataFimPeriodoAgendamento,
        Boolean agendamentoLiberado,
        @Valid
        SalvarEnderecoRequest endereco,
        List<@Valid SalvarPedestreRegraRequest> regras
) {
    public Pedestre toDomain() {
        return Pedestre.builder()
                .id(id)
                .nome(nome)
                .status(status != null ? status : Status.ATIVO)
                .tipo(tipo != null ? tipo : TipoPedestre.PEDESTRE)
                .email(email)
                .cpf(cpf)
                .rg(rg)
                .telefone(telefone)
                .celular(celular)
                .matricula(matricula)
                .codigoCartaoAcesso(codigoCartaoAcesso)
                .observacoes(observacoes)
                .tipoQRCode(tipoQRCode)
                .dataNascimento(dataNascimento)
                .genero(genero)
                .sempreLiberado(sempreLiberado)
                .acessoLivre(acessoLivre)
                .habilitarTeclado(habilitarTeclado)
                .cadastroFacialObrigatorio(cadastroFacialObrigatorio)
                .enviaSmsAoPassarNaCatraca(enviaSmsAoPassarNaCatraca)
                .uuidLocal(uuidLocal)
                .foto(decodeFotoBase64(fotoBase64))
                .empresa(idEmpresa != null ? Empresa.builder().id(idEmpresa).build() : null)
                .departamento(idDepartamento != null ? Departamento.builder().id(idDepartamento).build() : null)
                .centroCusto(idCentroCusto != null ? CentroCusto.builder().id(idCentroCusto).build() : null)
                .cargo(idCargo != null ? Cargo.builder().id(idCargo).build() : null)
                .empresaVisitada(empresaVisitada)
                .empresaVisitadaRef(idEmpresaVisitada != null ? Empresa.builder().id(idEmpresaVisitada).build() : null)
                .login(login)
                .senha(senha)
                .perfilApp(perfilApp)
                .justificativa(justificativa)
                .dataInicioPeriodoAgendamento(dataInicioPeriodoAgendamento)
                .dataFimPeriodoAgendamento(dataFimPeriodoAgendamento)
                .agendamentoLiberado(agendamentoLiberado)
                .endereco(endereco != null ? endereco.toDomain() : null)
                .regras(regras == null
                        ? Collections.emptyList()
                        : regras.stream().map(SalvarPedestreRegraRequest::toDomain).toList())
                .build();
    }

    private static byte[] decodeFotoBase64(String rawBase64) {
        if (rawBase64 == null || rawBase64.isBlank()) {
            return null;
        }
        String payload = rawBase64.trim();
        int commaIdx = payload.indexOf(',');
        if (commaIdx >= 0) {
            payload = payload.substring(commaIdx + 1).trim();
        }
        if (payload.isEmpty()) {
            return null;
        }
        try {
            return Base64.getDecoder().decode(payload);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Formato de foto em Base64 inválido.");
        }
    }
}
