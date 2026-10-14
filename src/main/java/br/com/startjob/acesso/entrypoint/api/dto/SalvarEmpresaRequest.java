package br.com.startjob.acesso.entrypoint.api.dto;

import br.com.startjob.acesso.core.domain.empresa.Empresa;
import br.com.startjob.acesso.core.enumeration.Status;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record SalvarEmpresaRequest(
        Long id,
        @NotBlank(message = "Nome da empresa é obrigatório")
        String nome,
        @NotBlank(message = "CNPJ é obrigatório")
        String cnpj,
        @Email(message = "E-mail inválido")
        String email,
        String telefone,
        String celular,
        @NotNull(message = "Status é obrigatório")
        Status status,
        String codigoExterno,
        String codEmpresaSenior,
        Boolean autoAtendimentoLiberado,
        List<@Valid SalvarDepartamentoRequest> departamentos,
        List<@Valid SalvarCentroCustoRequest> centros,
        List<@Valid SalvarCargoRequest> cargos
) {
    public SalvarEmpresaRequest withId(Long novoId) {
        return new SalvarEmpresaRequest(
                novoId,
                this.nome,
                this.cnpj,
                this.email,
                this.telefone,
                this.celular,
                this.status,
                this.codigoExterno,
                this.codEmpresaSenior,
                this.autoAtendimentoLiberado,
                this.departamentos,
                this.centros,
                this.cargos
        );
    }

    public Empresa toDomain() {
        return Empresa.builder()
                .id(id)
                .nome(nome)
                .cnpj(cnpj)
                .email(email)
                .telefone(telefone)
                .celular(celular)
                .status(status)
                .codigoExterno(codigoExterno)
                .codEmpresaSenior(codEmpresaSenior)
                .autoAtendimentoLiberado(autoAtendimentoLiberado != null && autoAtendimentoLiberado)
                .departamentos(departamentos == null ? null : departamentos.stream().map(SalvarDepartamentoRequest::toDomain).toList())
                .centros(centros == null ? null : centros.stream().map(SalvarCentroCustoRequest::toDomain).toList())
                .cargos(cargos == null ? null : cargos.stream().map(SalvarCargoRequest::toDomain).toList())
                .build();
    }
}
