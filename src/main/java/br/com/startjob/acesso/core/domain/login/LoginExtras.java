package br.com.startjob.acesso.core.domain.login;

import java.util.Set;

public record LoginExtras(Integer qtdePadraoDigitosCartao, String chaveIntegracaoComtele, Set<String> permissoes) {
}
