package br.com.startjob.acesso.core.gateway;

import br.com.startjob.acesso.core.domain.permissao.WebPerfilPermissao;
import br.com.startjob.acesso.core.enumeration.PerfilAcesso;

import java.util.List;

public interface WebPerfilPermissaoGateway {

    long countByClienteAtivo(Long clienteId);

    List<WebPerfilPermissao> findByClienteAndPerfil(Long clienteId, PerfilAcesso perfil);

}
