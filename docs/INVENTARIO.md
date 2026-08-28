# Inventário do legado e plano de migração

Fonte: `C:\ambiente\workspaces\smart-acesso-web` (EAR WildFly / Jakarta EE 10).

## Módulos do legado

| Módulo | Papel | Destino nesta reescrita |
|--------|--------|-------------------------|
| `ControleAcesso` | WAR JSF 4 + PrimeFaces 14 + RESTEasy + WebSocket | API REST neste repo; UI web em etapa posterior |
| `ControleAcesso-ejb` | 10 EJBs, 44 entidades, integrações | Services + JPA |
| `ControleAcesso-ear` | Empacote, context root `/sistema` | `server.servlet.context-path=/sistema` |
| `controle-acesso-pedestre` | App Android legado | Consome `/pedestre/*` (ainda não migrado) |
| `ControleAcesso-luxand-*` | Facial local | Fora do escopo desta etapa |
| `controleaccesso-swing` | Desktop catracas | Consome `/login/*`, `/access/*`, `/photo/*`, WS |

## Regras de negócio já portadas

1. **Multi-tenant por unidade** (`ClienteEntity.nomeUnidadeOrganizacional`).
2. **Login desktop**: unidade existe → exatamente 1 usuário com o login → senha confere → usuário ATIVO → plano ATIVO com `fim >= agora` → registra `TB_ACESSO_SISTEMA`. Acesso WEB exige `acessaWeb != false`.
3. **HTTP 500 com chave i18n** em falha de `/login/do` e `/login/interno` (contrato do Swing).
4. **Token desktop** `{timestamp}-{id}-{hashSenha}` sem expiração (expiração comentada no legado).
5. **Matriz de permissões web** por perfil, com override por cliente; `ANALISTA` sem permissões padrão.
6. **Login app**: `login` e `cliente` em minúsculas, `removido is null` (não `false`), JWT 8h, exige `perfilApp`.

## Regras de negócio ainda não portadas (próximas etapas)

- Sync incremental de pedestres/empresas/regras/locais/parâmetros/planos (`/access/request*`).
- Upload de visitantes, locais, regras, logs, biometria, fotos, backups.
- Dedup de logs de acesso e merge INDEFINIDO (`config.tempo.dispositivo`).
- Visitante, créditos, escalas, QR, totem, facial por link.
- Correspondência, avisos, responsáveis.
- Relatórios.
- Integrações RHID, Senior, TOTVS, SOC, AD, Sponte, Teknisa.
- WebSockets `/ws/local|liberacao|comando|pendente/{clienteId}`.
- Telas JSF.

## Riscos críticos de segurança (legado)

| Risco | Tratamento nesta etapa | Pendente |
|-------|------------------------|----------|
| `/access/*`, `/photo/*`, `/facial/*` sem autenticação | Flag `desktop-api-mode=COMPAT`; endpoints ainda não expostos | Autenticar sem quebrar Swing |
| Token desktop contém hash da senha | Preservado por contrato Swing | Trocar por JWT quando o desktop for atualizado |
| Login GET com senha na query | Preservado em `/login/do` | Adicionar POST paralelo |
| SHA-256 sem salt | Dual hasher SHA-256 + BCrypt; upgrade desligado | Ligar upgrade após novo token |
| Secrets hardcoded (Teknisa, TOTVS, AES) | Não copiados | Externalizar na migração de cada integração |
| WebSockets sem auth | Não migrados | Token na conexão |
| `/app/health` vazava paths | Health sem caminho de arquivo | — |
| Session web 7 dias | API stateless | UI futura com sessão curta |

## Estratégia

Strangler fig: o Spring Boot assume o mesmo context root `/sistema` e os mesmos paths REST. Clientes apontam para o novo host quando o contrato daquele grupo de endpoints estiver coberto por testes.
