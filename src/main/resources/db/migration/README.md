# Flyway

Em `prod`, `spring.flyway.enabled=true` e `baseline-on-migrate=true`.

O schema completo já existe no banco legado (Hibernate `hbm2ddl.auto=update`). As migrations deste módulo começam a partir de alterações incrementais (índices, colunas de algoritmo de senha, etc.), não recriam as 44 tabelas.

Nenhuma migration é aplicada em `dev`/`test` (DDL Hibernate).
