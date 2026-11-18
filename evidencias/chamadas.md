# Evidências — chamadas reais à API

Geradas com `curl` contra a API rodando localmente (`./mvnw spring-boot:run`, MySQL 8.4) em 16/11/2026 23:17.
Prints do Swagger UI e do app mobile: adicionar nesta pasta (`swagger.png`, `app-18h00.png`, `app-18h48.png`, `testes.png`).

### Lista de cruzamentos (app mobile)

`GET /cruzamentos` → **200**

```json
[
    {
        "id": "nu-jaime",
        "nome": "Av. das Nações Unidas × R. Jaime de Oliveira Souza",
        "nome_curto": "Nações Unidas × Jaime de Oliveira Souza",
        "via": "Av. das Nações Unidas",
        "bairro": "Vila Géa",
        "latitude": -23.6814075,
        "longitude": -46.694591,
        "placeholder": false
    },
    {
        "id": "cruzamento-2",
        "nome": "[cruzamento 2]",
        "nome_curto": "[cruzamento 2]",
        "via": null,
        "bairro": null,
        "latitude": null,
        "longitude": null,
        "placeholder": true
    },
    {
        "id": "cruzamento-3",
        "nome": "[cruzamento 3]",
        "nome_curto": "[cruzamento 3]",
        "via": null,
        "bairro": null,
        "latitude": null,
        "longitude": null,
        "placeholder": true
    },
    {
        "id": "cruzamento-4",
        "nome": "[cruzamento 4]",
        "nome_curto": "[cruzamento 4]",
        "via": null,
        "bairro": null,
        "latitude": null,
        "longitude": null,
        "placeholder": true
    }
]
```

### Replay 18:00 — aviso, sem confirmação

`GET /risco?local=nu-jaime&em=2025-01-16T18%3A00%3A00-03%3A00` → **200**

```json
{
    "local_id": "nu-jaime",
    "cruzamento": "Av. das Nações Unidas × R. Jaime de Oliveira Souza",
    "previsao_hora": "2025-01-16T18:00:00-03:00",
    "valida_ate": "2025-01-16T19:00:00-03:00",
    "estimativa": null,
    "limiar": 0.01083,
    "status": "elevado",
    "confirmado_cge": false,
    "confirmado_em": null
}
```

### Replay 18:48 — alagamento confirmado pelo CGE

`GET /risco?local=nu-jaime&em=2025-01-16T18%3A48%3A00-03%3A00` → **200**

```json
{
    "local_id": "nu-jaime",
    "cruzamento": "Av. das Nações Unidas × R. Jaime de Oliveira Souza",
    "previsao_hora": "2025-01-16T18:00:00-03:00",
    "valida_ate": "2025-01-16T19:00:00-03:00",
    "estimativa": null,
    "limiar": 0.01083,
    "status": "elevado",
    "confirmado_cge": true,
    "confirmado_em": "2025-01-16T18:48:00-03:00"
}
```

### Lote com cruzamento inexistente

`GET /risco?locais=nu-jaime,cruzamento-2,xyz&em=2025-01-16T18%3A30%3A00-03%3A00` → **200**

```json
{
    "riscos": [
        {
            "local_id": "nu-jaime",
            "cruzamento": "Av. das Nações Unidas × R. Jaime de Oliveira Souza",
            "previsao_hora": "2025-01-16T18:00:00-03:00",
            "valida_ate": "2025-01-16T19:00:00-03:00",
            "estimativa": null,
            "limiar": 0.01083,
            "status": "elevado",
            "confirmado_cge": false,
            "confirmado_em": null
        },
        {
            "local_id": "cruzamento-2",
            "cruzamento": "[cruzamento 2]",
            "previsao_hora": "2025-01-16T18:00:00-03:00",
            "valida_ate": "2025-01-16T19:00:00-03:00",
            "estimativa": null,
            "limiar": 0.01083,
            "status": "sem_estimativa",
            "confirmado_cge": false,
            "confirmado_em": null
        }
    ],
    "nao_encontrados": [
        "xyz"
    ]
}
```

### Erro: cruzamento inexistente

`GET /risco?local=xyz` → **404**

```json
{
    "erro": {
        "codigo": "local_nao_encontrado",
        "mensagem": "Cruzamento 'xyz' não existe."
    }
}
```

### Erro: em inválido

`GET /risco?local=nu-jaime&em=ontem` → **400**

```json
{
    "erro": {
        "codigo": "parametro_invalido",
        "mensagem": "em fora do formato ISO 8601."
    }
}
```

### Alertas do cruzamento

`GET /alertas?local=nu-jaime&desde=2025-01-01T00%3A00%3A00-03%3A00` → **200**

```json
[
    {
        "local_id": "nu-jaime",
        "cruzamento": "Av. das Nações Unidas × R. Jaime de Oliveira Souza",
        "previsao_hora": "2026-11-17T18:00:00-03:00",
        "valida_ate": "2026-11-17T19:00:00-03:00",
        "estimativa": 0.0234,
        "limiar": 0.01083,
        "status": "elevado",
        "confirmado_cge": false,
        "confirmado_em": null
    },
    {
        "local_id": "nu-jaime",
        "cruzamento": "Av. das Nações Unidas × R. Jaime de Oliveira Souza",
        "previsao_hora": "2025-01-16T18:00:00-03:00",
        "valida_ate": "2025-01-16T19:00:00-03:00",
        "estimativa": null,
        "limiar": 0.01083,
        "status": "elevado",
        "confirmado_cge": true,
        "confirmado_em": "2025-01-16T18:48:00-03:00"
    }
]
```

### Alagamentos na área (bbox)

`GET /alagamentos?bbox=-46.75,-23.75,-46.60,-23.60&desde=2025-01-16T00%3A00%3A00-03%3A00` → **200**

```json
[
    {
        "id": 1,
        "local_id": "nu-jaime",
        "cruzamento": "Av. das Nações Unidas × R. Jaime de Oliveira Souza",
        "latitude": -23.6814075,
        "longitude": -46.694591,
        "registrado_em": "2025-01-16T18:48:00-03:00",
        "fonte": "CGE",
        "intransitavel": true
    }
]
```

### Chuva atual e prevista (Open-Meteo, dado real)

`GET /chuva?lat=-23.6814075&lon=-46.694591` → **200**

```json
{
    "latitude": -23.6814075,
    "longitude": -46.694591,
    "medido_em": "2026-11-16T23:15:00-03:00",
    "intensidade_atual_mm_h": 0.0,
    "prevista": [
        {
            "hora": "2026-11-16T23:15:00-03:00",
            "intensidade_mm_h": 0.0
        },
        {
            "hora": "2026-11-16T23:30:00-03:00",
            "intensidade_mm_h": 0.0
        },
        {
            "hora": "2026-11-16T23:45:00-03:00",
            "intensidade_mm_h": 0.0
        },
        {
            "hora": "2026-11-17T00:00:00-03:00",
            "intensidade_mm_h": 0.0
        },
        {
            "hora": "2026-11-17T00:15:00-03:00",
            "intensidade_mm_h": 0.0
        },
        {
            "hora": "2026-11-17T00:30:00-03:00",
            "intensidade_mm_h": 0.0
        },
        {
            "hora": "2026-11-17T00:45:00-03:00",
            "intensidade_mm_h": 0.0
        },
        {
            "hora": "2026-11-17T01:00:00-03:00",
            "intensidade_mm_h": 0.0
        }
    ],
    "fonte": "Open-Meteo"
}
```

### Reporte do sensor ESP32 (sem login)

`POST /reportes` → **201**

```json
{
    "id": 2,
    "origem": "SENSOR",
    "local_id": "nu-jaime",
    "latitude": -23.6814075,
    "longitude": -46.694591,
    "nivel_cm": 18,
    "faixa": "intransitavel_a_pe",
    "descricao": null,
    "recebido_em": "2026-11-16T23:17:30-03:00"
}
```

### Erro: reporte fora da Grande SP

`POST /reportes` → **400**

```json
{
    "erro": {
        "codigo": "validacao",
        "mensagem": "Localização fora da região atendida (Grande São Paulo)!"
    }
}
```

### Erro: login com senha errada

`POST /login` → **401**

```json
{
    "erro": {
        "codigo": "credenciais_invalidas",
        "mensagem": "Login ou senha inválidos."
    }
}
```

### Erro: listagem sem token

`GET /reportes` → **401**

```json
{
    "erro": {
        "codigo": "nao_autenticado",
        "mensagem": "Informe um token JWT válido (POST /login)."
    }
}
```

### Reportes paginados (ADMIN)

`GET /reportes?size=3` → **200**

```json
{
    "content": [
        {
            "id": 2,
            "origem": "SENSOR",
            "local_id": "nu-jaime",
            "faixa": "intransitavel_a_pe",
            "recebido_em": "2026-11-16T23:17:30-03:00"
        },
        {
            "id": 1,
            "origem": "SENSOR",
            "local_id": "nu-jaime",
            "faixa": "intransitavel_a_pe",
            "recebido_em": "2026-11-16T23:12:13-03:00"
        }
    ],
    "empty": false,
    "first": true,
    "last": true,
    "number": 0,
    "numberOfElements": 2,
    "pageable": {
        "offset": 0,
        "pageNumber": 0,
        "pageSize": 3,
        "paged": true,
        "sort": {
            "empty": false,
            "sorted": true,
            "unsorted": false
        },
        "unpaged": false
    },
    "size": 3,
    "sort": {
        "empty": false,
        "sorted": true,
        "unsorted": false
    },
    "totalElements": 2,
    "totalPages": 1
}
```

### Previsões do modelo de ML (ADMIN)

`PUT /previsoes` → **200**

```json
{
    "gravadas": 1
}
```

### Risco depois da previsão do ML

`GET /risco?local=nu-jaime&em=2026-11-18T18%3A15%3A00-03%3A00` → **200**

```json
{
    "local_id": "nu-jaime",
    "cruzamento": "Av. das Nações Unidas × R. Jaime de Oliveira Souza",
    "previsao_hora": "2026-11-18T18:00:00-03:00",
    "valida_ate": "2026-11-18T19:00:00-03:00",
    "estimativa": 0.0412,
    "limiar": 0.01083,
    "status": "elevado",
    "confirmado_cge": false,
    "confirmado_em": null
}
```

### Histórico do Flyway

```
+----------------+---------+------------------------------------------+---------+
| installed_rank | version | description                              | success |
+----------------+---------+------------------------------------------+---------+
|              1 | 1       | create-table-cruzamentos                 |       1 |
|              2 | 2       | create-table-previsoes                   |       1 |
|              3 | 3       | create-table-alagamentos                 |       1 |
|              4 | 4       | create-table-reportes                    |       1 |
|              5 | 5       | create-table-usuarios                    |       1 |
|              6 | 6       | alter-table-usuarios-add-column-perfil   |       1 |
|              7 | 7       | alter-table-cruzamentos-add-column-ordem |       1 |
|              8 | 8       | insert-cruzamentos                       |       1 |
|              9 | 9       | insert-replay-16-01-2025                 |       1 |
|             10 | 10      | insert-usuarios                          |       1 |
|             11 | 11      | create-table-leituras-chuva              |       1 |
+----------------+---------+------------------------------------------+---------+
```

## Segurança — expiração do token (servidor real)

API rodando com o servidor em **UTC** (como numa VM Linux) e `JWT_EXPIRACAO_MINUTOS=1`:

```
exp em 60 s | claims {'iss': 'RotaSecaSOA', 'sub': 'admin', 'perfil': 'ADMIN'}
00:17:48 com token novo:  200
00:18:53 apos 65s:        {"erro":{"codigo":"token_invalido","mensagem":"Token JWT inválido ou expirado!"}} [401]
00:18:53 sem token:       {"erro":{"codigo":"nao_autenticado","mensagem":"Informe um token JWT válido (POST /login)."}} [401]
00:18:54 publico (app):   200
servidor em UTC -> confirmado_em: 2025-01-16T18:48:00-03:00 | previsao_hora: 2025-01-16T18:00:00-03:00
```

Bug corrigido: o cálculo de expiração herdado da aula (`LocalDateTime.now().plusMinutes(30).toInstant(ZoneOffset.of("-03:00"))`)
depende do fuso do servidor. Com o servidor em UTC, o teste `SegurancaTest.expiracaoTrintaMinutos` falhava com validade de
**3h29min59s**; com `Instant.now().plus(30, MINUTES)` passa em qualquer fuso.

## Segurança — requisições mal formadas

```
POST /reportes com Content-Type: text/plain -> 415 {"erro":{"codigo":"requisicao_invalida",...}}  (antes: 500)
GET /risco com Accept: application/xml      -> 406                                              (antes: 401)
```

## Segurança — proteção contra abuso (servidor real)

```
## Login: 5 senhas erradas e depois a certa
tentativa 1 (errada): 401
tentativa 2 (errada): 401
tentativa 3 (errada): 401
tentativa 4 (errada): 401
tentativa 5 (errada): 401
6a (senha CERTA): HTTP/1.1 429  Retry-After: 899 {"erro":{"codigo":"muitas_tentativas","mensagem":"Muitas tentativas de login. Tente novamente mais tarde."}} 
outro usuario (consulta) mesmo IP: 200

## POST /reportes: 31 requisicoes no mesmo minuto
31a: {"erro":{"codigo":"muitas_tentativas","mensagem":"Limite de 30 reportes por minuto atingido. Tente novamente em instantes."}} [429]

## Dado pessoal na descricao (API reiniciada para zerar o limite)
descricao gravada: Rua alagada, me liga [removido], placa [removido] | dados_removidos: True
mesmo ponto de novo: {"erro":{"codigo":"reporte_duplicado","mensagem":"Já existe reporte desta origem para este local nos últimos 5 minutos!"}} [409]

## Perfil prod com JWT_SECRET padrao
JWT_SECRET fraco ou padrão no perfil prod: defina JWT_SECRET com 32+ caracteres aleatórios (ex.: openssl rand -base64 48).
API subiu? 000

## Perfil prod com JWT_SECRET forte
API subiu? 200
```

## Rodando com Docker (`docker compose up -d --build`)

Mesma API em container (imagem `eclipse-temurin:25-jre`) com MySQL 8.4, gerado em 19/11/2026 22:23.

```
$ docker compose ps
NAME            IMAGE         STATUS                  PORTS
gs2-soa-app-1   gs2-soa-app   Up 29 hours             0.0.0.0:8080->8080/tcp
gs2-soa-db-1    mysql:8.4     Up 29 hours (healthy)   0.0.0.0:3306->3306/tcp

$ curl http://localhost:8080/health
Rota Seca - Serviço de Risco da Rua: ok

$ curl "http://localhost:8080/risco?local=nu-jaime&em=2025-01-16T18:48:00-03:00"
{"local_id":"nu-jaime","cruzamento":"Av. das Nações Unidas × R. Jaime de Oliveira Souza","previsao_hora":"2025-01-16T18:00:00-03:00","valida_ate":"2025-01-16T19:00:00-03:00","estimativa":null,"limiar":0.01083,"status":"elevado","confirmado_cge":true,"confirmado_em":"2025-01-16T18:48:00-03:00"}

$ curl -o /dev/null -w "%{http_code}" http://localhost:8080/swagger-ui/index.html
200

$ docker compose exec db mysql -uroot -pfiap rotaseca -e "select count(*) migracoes from flyway_schema_history where success = 1"
migracoes
11
```
