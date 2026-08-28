# ADR 0002 — Segurança e compatibilidade

## Contexto

O desktop Swing autentica com `GET /login/do?passwd=` e persiste `object.senha` (hash SHA-256) e um token `{timestamp}-{id}-{hash}`. Falhas de login voltam HTTP 500 com chaves `msgs.account.*`.

Endurecer isso agora quebraria todos os clientes em campo.

## Decisão

1. Preservar `/login/do`, `/login/interno` e o envelope `ResponseServiceTO`.
2. Preservar HTTP 500 + message key no login desktop.
3. Preservar o campo `senha` no JSON de `/login/do` atrás de `smartacesso.security.include-password-hash-in-desktop-login` (default `true`).
4. Não rehash para BCrypt no login enquanto o token desktop embute o hash (`password-upgrade-on-login=false`).
5. App `/app/login` usa JWT HS256, não devolve senha, HTTP 401 em credencial inválida.
6. Rate limit nos endpoints de login.
7. Headers de segurança Spring; CSRF off (API stateless); CORS restrito a localhost na etapa 1.
8. Erros genéricos fora do contrato desktop — sem stacktrace na resposta.
9. `/access/*` de sync só será publicado em modo `COMPAT` até o desktop enviar token.

## Consequências

A superfície de ataque do login GET permanece até o desktop ser atualizado. A nova API já nasce com JWT e hashing moderno. A troca do token desktop é um passo coordenado com `controleaccesso-swing`.
