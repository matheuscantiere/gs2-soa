# Prompts usados (claude code)

## 1. Aprender

> Explique a arquitetura hexagonal deste projeto: o que fica em `port/in`, `port/out` e `adapter`, e por quê.
> Explique passo a passo o que acontece numa chamada a `GET /risco?local=nu-jaime&em=...`, do controller até o banco.
> Como o `RiscoService` decide entre `elevado`, `normal` e `sem_estimativa`?
> Explique o fluxo de JWT neste projeto: login, geração do token, `SecurityFilter` e expiração.
> Para que servem as migrações Flyway V1–V11 e por que existem os `alter table` em vez de recriar a tabela?
> Qual a diferença entre `@SpringBootTest` com MockMvc e `@DataJpaTest`, e onde cada um é usado aqui?
> Como o `OpenMeteoService` consome a API externa com fluent-hc e gson?

## 2. Revisar

> Revise o `TokenService`: a expiração do token está correta em servidor com fuso UTC?
> Revise o `SecurityConfig`: algum endpoint que deveria ser protegido está público?
> Revise o `GlobalExceptionHandler`: algum erro de cliente pode virar 500?
> Os testes de `RiscoControllerTest` cobrem o replay de 16/01/2025 (18:00, 18:30 e 18:48)?
> Revise o `POST /reportes` quanto a abuso e dados pessoais.

> Crie um prompts.md com os prompts que eu usei nesta conversa, em ordem.
Agrupe em seções com cada prompt em citação (>) resumido em uma frase.
