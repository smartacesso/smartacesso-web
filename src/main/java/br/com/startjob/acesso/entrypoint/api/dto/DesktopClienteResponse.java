package br.com.startjob.acesso.entrypoint.api.dto;

import java.io.Serializable;

public class DesktopClienteResponse implements Serializable {

    private Long id;
    private String nome;
    private String nomeUnidadeOrganizacional;
    private String status;
    private String cnpj;
    private String email;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNomeUnidadeOrganizacional() {
        return nomeUnidadeOrganizacional;
    }

    public void setNomeUnidadeOrganizacional(String nomeUnidadeOrganizacional) {
        this.nomeUnidadeOrganizacional = nomeUnidadeOrganizacional;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
