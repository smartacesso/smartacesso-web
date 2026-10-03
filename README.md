# Smart Acesso Web — Spring Boot

Reescrita incremental do monólito Jakarta EE / WildFly (`smart-acesso-web`) para Spring Boot 3.5 / Java 21.

Este repositório **não substitui o legado de imediato**. Ele preserva os contratos HTTP usados pelo desktop Swing, pelos apps móveis e pelo servidor facial, e migra funcionalidade por funcionalidade.

## Estado atual (etapa 1)

- Fundação Spring Boot: segurança, JPA, Actuator, OpenAPI, testes.
- Login desktop: `GET /sistema/restful-services/login/action|do|interno`
- Health da API de sync: `GET /sistema/restful-services/access/action`
- Login do app: `POST /sistema/restful-services/app/login` e `GET /app/health`

Ainda **não** migrados: sync de pedestres, fotos, biometria, JSF/PrimeFaces, WebSockets, integrações RHID/Senior/TOTVS/SOC.

## Como rodar

Requisito: JDK 25 e Maven 3.9+ (ou o wrapper `./mvnw`). O perfil `dev` conecta ao MySQL local; inicie o serviço antes da aplicação.

```bash
# perfil dev usa MySQL e cria usuário admin/123456 na unidade "desenvolvimento"
./mvnw spring-boot:run
```

- API base: http://localhost:8080/sistema
- Swagger: http://localhost:8080/sistema/swagger-ui.html
- Health: http://localhost:8080/sistema/actuator/health

Login desktop de exemplo:

```
GET /sistema/restful-services/login/do?unidadeName=desenvolvimento&loginName=admin&passwd=123456
```

## Perfis

| Perfil | Banco | DDL | Observação |
|--------|--------|-----|------------|
| `dev` (padrão) | MySQL local | `none` + Liquibase | Bootstrap de dados de desenvolvimento |
| `test` | H2 | `create-drop` | Liquibase desabilitado; usado pelos testes |
| `prod` | MySQL | `validate` + Liquibase | Exige `JWT_SECRET` e `DB_*` |

Copie `.env.example` para `.env` e preencha os secrets. Nunca commite `.env`.

Para apontar para um MySQL existente do legado (quando a etapa de sync estiver pronta):

```
SPRING_PROFILES_ACTIVE=prod
DB_URL=jdbc:mysql://host:3306/controle_acesso
DB_USERNAME=...
DB_PASSWORD=...
JWT_SECRET=...
```

## Testes

```bash
./mvnw test
```

## Documentação

- [Inventário do legado e plano de migração](docs/INVENTARIO.md)
- [ADR 0001 — Arquitetura alvo](docs/adr/0001-arquitetura-alvo.md)
- [ADR 0002 — Segurança e compatibilidade](docs/adr/0002-seguranca-e-compatibilidade.md)
