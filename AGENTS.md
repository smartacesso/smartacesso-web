# Instruções para IA — Backend Smart-Acesso

Este documento define as diretrizes que qualquer IA (assistente de código, agente, etc.) deve seguir ao trabalhar neste projeto. O objetivo é manter consistência arquitetural e de convenções em todo o código gerado.

## Visão geral

- Backend Java/Spring Boot que serve uma aplicação frontend em React (smart-acesso-web-app), consumido via API REST/JSON.
- Deploy em servidor próprio na Algar, **sem** containerizar a aplicação. Apenas o banco de dados roda em container.
- Banco principal: MySQL. O projeto deve ser mantido portável para outros bancos relacionais (ex.: SQL Server) — evitar acoplamento a features específicas do MySQL.

## Stack tecnológica

- Java e Spring Boot nas versões estáveis mais recentes.
- Persistência: Spring Data JPA / Hibernate.
- Validação: Bean Validation (`spring-boot-starter-validation`).
- Segurança: Spring Security com autenticação JWT.
- Observabilidade: Spring Boot Actuator.
- Documentação de API: springdoc-openapi (Swagger UI).
- Redução de boilerplate: Lombok.
- Mapeamento entre camadas: MapStruct.
- Migração de schema: Liquibase (preferível ao Flyway neste projeto por gerar SQL adequado a cada dialeto de banco a partir do mesmo changeset, o que ajuda na portabilidade MySQL → SQL Server).
- Testes de integração: Testcontainers (subir MySQL real em container durante os testes).

## Arquitetura: Clean Architecture + DDD

Estrutura de pacotes obrigatória:

```
core/
  usecase/       # regras de aplicação, orquestram o domínio
  domain/        # entidades e regras de negócio puras, sem dependência de frameworks
  gateway/       # interfaces (ports) que a camada dataprovider deve implementar
  enumeration/   # enums de domínio
  exception/     # exceções de negócio/domínio
  config/        # objetos de configuração agnósticos de framework (ex.: records de propriedades)

dataprovider/
  client/        # integrações com sistemas externos (HTTP clients, Feign, etc.)
  dto/           # DTOs usados na comunicação com sistemas externos (clients)
  entity/        # entidades JPA (@Entity), nunca expostas fora desta camada
  gateway/       # implementações das interfaces definidas em core/gateway (adapters)
  mapper/        # mapeamento entity <-> domain, dto <-> domain (MapStruct)
  repository/    # interfaces Spring Data JPA

entrypoint/
  api/
    config/      # configuração de beans, CORS, OpenAPI, etc.
    dto/         # DTOs de request/response da API REST
    controller/  # controllers REST, traduzem HTTP <-> chamada de usecase
    handler/     # tratamento centralizado de exceções (@RestControllerAdvice)
    security/    # configuração de segurança, filtros JWT, etc.
```

### Regra de dependência

- `core` **não** pode depender de `dataprovider` nem de `entrypoint`.
- `core` deve evitar ao máximo anotações e classes do Spring Framework (nada de `@Entity`, `@RestController`, `@Service`, etc. dentro de `core`). Exceções pontuais são aceitáveis quando facilitam a integração sem violar a arquitetura, como `@Component` (para o Spring gerenciar os usecases) e `@Slf4j` (Lombok, para logging) — novas exceções devem ser avaliadas caso a caso, sempre priorizando o mínimo acoplamento possível a frameworks.
- `dataprovider` e `entrypoint` dependem de `core`, nunca o contrário.
- Comunicação entre `core` e `dataprovider` acontece via interfaces (gateways) definidas em `core/gateway` e implementadas em `dataprovider/gateway`.
- DTOs de `entrypoint/api/dto` e `dataprovider/dto` são independentes entre si — não reutilizar um DTO de API como DTO de integração externa, e vice-versa.
- Considerar testes com **ArchUnit** para validar automaticamente essas regras de dependência entre camadas.

## Injeção de dependência

- Sempre via **construtor**. Nunca usar `@Autowired` em campo nem injeção via setter.
- Usar `@RequiredArgsConstructor` (Lombok) com campos `private final` para reduzir boilerplate.

## Estratégia de testes

- Priorizar **testes de integração**, usando Testcontainers para subir uma instância real de MySQL.
- Testes unitários apenas em casos específicos: regras de domínio complexas isoladas em `core/domain` ou lógica de `usecase` que valha a pena testar sem subir contexto Spring.
- Testes de integração devem cobrir o fluxo completo: controller → usecase → gateway → banco.

## Banco de dados e portabilidade

- Usar JPA/Hibernate para abstrair o dialeto SQL; evitar `@Query` nativo sempre que possível — preferir JPQL/Criteria API.
- Estratégia de geração de ID: `GenerationType.SEQUENCE` (compatível com MySQL 8+ e SQL Server), evitar `IDENTITY`.
- Não usar tipos de dados ou funções específicas do MySQL nas entidades.
- Migrations em Liquibase (changesets), não SQL nativo direto.

## Deploy (sem Docker na aplicação)

- Empacotar como jar executável (`spring-boot-maven-plugin`).
- Rodar via serviço systemd no servidor da Algar (restart automático, logs centralizados, start no boot).
- Usar Spring Profiles (`application-dev.yml`, `application-prod.yml`) para configuração por ambiente, incluindo troca futura de datasource.
- Habilitar `server.shutdown=graceful`.
- Segredos e credenciais via variáveis de ambiente, nunca versionados no `application.yml`.
- Se a API for exposta publicamente, considerar Nginx como reverse proxy (TLS, etc.).

## Outras convenções

- Versionar a API (`/api/v1/...`).
- DTOs da camada `entrypoint/api/dto` como `record` (imutáveis), quando a versão do Java usada suportar.
- Tratamento de exceções centralizado em `entrypoint/api/handler` via `@RestControllerAdvice`, convertendo exceções de domínio (`core/exception`) em respostas HTTP apropriadas.
- CORS configurado explicitamente em `entrypoint/api/config` para liberar a origem do frontend React.
- Paginação de listagens via `Pageable`/`Page` do Spring Data.
- Para comunicação assíncrona futura (SSE) entre backend e frontend: usar `SseEmitter` do Spring MVC clássico (Tomcat, thread-per-request). Não migrar para WebFlux/Netty a menos que haja necessidade real de alta concorrência de conexões simultâneas.
- Logs estruturados (SLF4J + Logback), evitar `System.out.println`.
- Endpoints do Actuator sensíveis (ex.: `/actuator/env`, `/actuator/heapdump`) devem ficar protegidos por segurança, não expostos publicamente.
