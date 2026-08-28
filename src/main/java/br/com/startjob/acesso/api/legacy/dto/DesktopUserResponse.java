package br.com.startjob.acesso.api.legacy.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

public class DesktopUserResponse implements Serializable {

    private Long id;
    private String nome;
    private String login;
    private String senha;
    private String status;
    private String perfil;
    private String token;
    private String email;
    private Boolean acessaWeb;
    private Boolean expedidora;
    private Boolean cadastroSimples;
    private Date dataCriacao;
    private Integer qtdePadraoDigitosCartao;
    private String chaveIntegracaoComtele;
    private Set<String> permissoes;
    private DesktopClienteResponse cliente;

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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getAcessaWeb() {
        return acessaWeb;
    }

    public void setAcessaWeb(Boolean acessaWeb) {
        this.acessaWeb = acessaWeb;
    }

    public Boolean getExpedidora() {
        return expedidora;
    }

    public void setExpedidora(Boolean expedidora) {
        this.expedidora = expedidora;
    }

    public Boolean getCadastroSimples() {
        return cadastroSimples;
    }

    public void setCadastroSimples(Boolean cadastroSimples) {
        this.cadastroSimples = cadastroSimples;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Integer getQtdePadraoDigitosCartao() {
        return qtdePadraoDigitosCartao;
    }

    public void setQtdePadraoDigitosCartao(Integer qtdePadraoDigitosCartao) {
        this.qtdePadraoDigitosCartao = qtdePadraoDigitosCartao;
    }

    public String getChaveIntegracaoComtele() {
        return chaveIntegracaoComtele;
    }

    public void setChaveIntegracaoComtele(String chaveIntegracaoComtele) {
        this.chaveIntegracaoComtele = chaveIntegracaoComtele;
    }

    public Set<String> getPermissoes() {
        return permissoes;
    }

    public void setPermissoes(Set<String> permissoes) {
        this.permissoes = permissoes;
    }

    public DesktopClienteResponse getCliente() {
        return cliente;
    }

    public void setCliente(DesktopClienteResponse cliente) {
        this.cliente = cliente;
    }
}
