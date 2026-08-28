# ADR 0001 — Arquitetura alvo

## Contexto

O legado é um EAR Jakarta EE (JSF + EJB + RESTEasy) com um EJB genérico de ~2400 linhas e um `PedestreEJB` de ~5000 linhas. Há clientes acoplados a URLs e JSON específicos.

## Decisão

Monólito modular Spring Boot 3, um único deployável, pacotes por capacidade:

- `api.legacy` — contratos HTTP existentes (Swing, pedestre, facial)
- `api.app` — API JWT do aplicativo
- `application.*` — casos de uso
- `domain` — entidades, enums, repositórios
- `security` — authn/authz, hashing, JWT
- `config` — infraestrutura

Não adotamos microsserviços agora: o domínio é um único produto de controle de acesso, o time é pequeno e as transações cruzam cadastro, regra e log.

UI JSF não será reescrita nesta etapa. A API é o contrato crítico com o desktop. A UI web entra depois (Thymeleaf ou SPA), atrás da mesma API.

## Consequências

- Deploy simples, observabilidade única, transações JPA locais.
- Extração futura de módulos (facial, integrações RH) é possível pelos pacotes, sem ser obrigatória.
- `hbm2ddl.auto=update` do legado é substituído por `none` + Flyway em produção.
