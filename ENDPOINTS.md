# Minha Fazenda — Endpoints da API

Referência para o app. Base de produção (teste):

```
https://minha-fazenda-api.onrender.com
```

- JSON em todas as rotas, exceto o upload de avatar (multipart).
- Rotas autenticadas: header `Authorization: Bearer <accessToken>`.
- Datas em ISO 8601 UTC (`2026-11-11T12:34:56.789Z`). IDs são UUID.

## Sumário

| Método | Rota | Auth | Para quê |
|---|---|---|---|
| `POST` | [`/auth/registro`](#post-authregistro) | — | Cria a conta e entra (com fazenda nova ou por convite) |
| `POST` | [`/auth/login`](#post-authlogin) | — | Entra com e-mail e senha |
| `POST` | [`/auth/refresh`](#post-authrefresh) | — | Troca o refresh token por tokens novos |
| `POST` | [`/auth/logout`](#post-authlogout) | sim | Encerra a sessão deste aparelho |
| `GET` | [`/auth/powersync-token`](#get-authpowersync-token) | sim | Token para conectar no PowerSync |
| `GET` | [`/usuarios/me`](#get-usuariosme) | sim | Perfil do usuário logado |
| `PATCH` | [`/usuarios/me`](#patch-usuariosme) | sim | Troca o nome |
| `PUT` | [`/usuarios/me/avatar`](#put-usuariosmeavatar) | sim | Envia ou troca a foto |
| `DELETE` | [`/usuarios/me/avatar`](#delete-usuariosmeavatar) | sim | Remove a foto |
| `POST` | [`/fazendas`](#post-fazendas) | sim | Cria uma fazenda (quem cria vira owner) |
| `PATCH` | [`/fazendas/{id}`](#patch-fazendasid) | owner | Renomeia a fazenda |
| `PATCH` | [`/fazendas/{fazendaId}/membros/{usuarioId}`](#patch-fazendasfazendaidmembrosusuarioid) | owner | Muda o papel de um membro |
| `DELETE` | [`/fazendas/{fazendaId}/membros/{usuarioId}`](#delete-fazendasfazendaidmembrosusuarioid) | owner | Remove um membro |
| `DELETE` | [`/fazendas/{fazendaId}/membros/me`](#delete-fazendasfazendaidmembrosme) | membro | Sai da fazenda |
| `POST` | [`/fazendas/{fazendaId}/convites`](#post-fazendasfazendaidconvites) | owner | Gera um link de convite |
| `DELETE` | [`/convites/{id}`](#delete-convitesid) | owner | Revoga um convite |
| `POST` | [`/convites/aceitar`](#post-convitesaceitar) | sim | Entra na fazenda do convite |
| `POST` | [`/sync/upload`](#post-syncupload) | membro | Envia a fila de escritas do PowerSync |
| `GET` | [`/ping`](#get-ping) | — | Saúde da API |
| `GET` | [`/.well-known/jwks.json`](#get-well-knownjwksjson) | — | Chave pública (usada pelo PowerSync) |

"owner" e "membro" são conferidos na tabela `fazenda_membros` a cada requisição; o papel não vai
no token. Remover alguém da fazenda vale na hora.

## Erros

Toda resposta de erro tem o mesmo formato:

```json
{ "erro": "VALIDACAO", "mensagem": "credencial.senha: deve ter no mínimo 8 caracteres e no máximo 72 bytes" }
```

A `mensagem` é em português e pode ser mostrada ao usuário. O app deve decidir o que fazer pelo
`erro`.

| HTTP | `erro` | Quando |
|---|---|---|
| 400 | `VALIDACAO` | corpo inválido, campo obrigatório faltando, e-mail já cadastrado |
| 401 | `TOKEN_INVALIDO` | access token ausente, vencido ou inválido → chamar `/auth/refresh` |
| 401 | `SESSAO_ENCERRADA` | refresh revogado, vencido, reutilizado ou de outro aparelho → mandar para o login |
| 401 | `CREDENCIAL_INVALIDA` | e-mail ou senha incorretos (mesma resposta nos dois casos) |
| 403 | `SEM_PERMISSAO` | não é membro da fazenda ou o papel não permite |
| 404 | `NAO_ENCONTRADO` | fazenda, membro ou convite não existe |
| 409 | `ULTIMO_OWNER` | a ação deixaria a fazenda sem nenhum owner |
| 410 | `CONVITE_INVALIDO` | convite vencido, revogado ou já usado |
| 413 | `ARQUIVO_GRANDE` | foto acima de 5 MB |
| 415 | `FORMATO_INVALIDO` | foto que não é JPEG, PNG ou WebP |
| 426 | `APP_DESATUALIZADO` | `schemaVersion` do app abaixo do mínimo aceito → pedir atualização |
| 429 | `MUITAS_TENTATIVAS` | mais de 10 requisições por minuto no mesmo IP em `/auth/*` |
| 500 | `ERRO_INTERNO` | erro inesperado |

## Sessão e tokens

| Token | Validade | Observação |
|---|---|---|
| `accessToken` | 30 min (`expiresIn` = 1800 s) | JWT RS256; vai no header `Authorization` |
| `refreshToken` | 90 dias | Opaco. Muda a cada `/auth/refresh`: guarde sempre o último |
| PowerSync | 60 min | Só serve para o PowerSync, nunca para a API |

Fluxo recomendado no app:

1. Guardar `accessToken` e `refreshToken` em armazenamento seguro do aparelho.
2. Recebeu `401 TOKEN_INVALIDO` → chamar `/auth/refresh` uma vez e repetir a requisição.
3. Recebeu `401 SESSAO_ENCERRADA` no refresh → apagar os tokens e mandar para o login.
4. O `deviceId` precisa ser o mesmo no login e em todo refresh daquele aparelho. Um refresh com
   outro `deviceId`, ou um refresh token antigo reaproveitado, encerra a sessão.

---

## Conta

### `POST /auth/registro`

Cria a conta e já devolve a sessão. Mande **ou** `nomeFazenda` (cria uma fazenda e vira owner)
**ou** `conviteToken` (entra como collaborator na fazenda do convite), nunca os dois.

```json
{
  "nome": "Arthur",
  "credencial": { "tipo": "senha", "email": "arthur@exemplo.com", "senha": "uma senha boa" },
  "deviceId": "id-estavel-do-aparelho",
  "nomeFazenda": "Sítio Boa Vista"
}
```

| Campo | Regra |
|---|---|
| `nome` | 2 a 60 caracteres |
| `credencial.tipo` | `"senha"` |
| `credencial.email` | e-mail válido; maiúsculas são ignoradas |
| `credencial.senha` | mínimo 8 caracteres, máximo 72 bytes |
| `deviceId` | obrigatório; identificador estável do aparelho |
| `nomeFazenda` | até 100 caracteres |
| `conviteToken` | o token do link de convite |

`201 Created`

```json
{
  "accessToken": "eyJ...",
  "refreshToken": "q8Zk...",
  "expiresIn": 1800,
  "usuario": { "id": "uuid", "nome": "Arthur", "avatarUrl": null },
  "fazendaId": "uuid"
}
```

Erros: `400 VALIDACAO` (inclusive e-mail já cadastrado), `410 CONVITE_INVALIDO`.

### `POST /auth/login`

```json
{
  "credencial": { "tipo": "senha", "email": "arthur@exemplo.com", "senha": "uma senha boa" },
  "deviceId": "id-estavel-do-aparelho"
}
```

`200 OK`

```json
{
  "accessToken": "eyJ...",
  "refreshToken": "q8Zk...",
  "expiresIn": 1800,
  "usuario": { "id": "uuid", "nome": "Arthur", "avatarUrl": "https://..." }
}
```

Erros: `401 CREDENCIAL_INVALIDA`, `400 VALIDACAO`, `429 MUITAS_TENTATIVAS`.

### `POST /auth/refresh`

```json
{ "refreshToken": "q8Zk...", "deviceId": "id-estavel-do-aparelho" }
```

`200 OK`

```json
{ "accessToken": "eyJ...", "refreshToken": "novo-refresh", "expiresIn": 1800 }
```

O refresh token antigo deixa de valer. Erros: `401 SESSAO_ENCERRADA`.

### `POST /auth/logout`

```json
{ "refreshToken": "q8Zk..." }
```

`204 No Content`. A sessão do aparelho é encerrada.

### `GET /auth/powersync-token`

`200 OK`

```json
{ "endpoint": "https://<instancia>.powersync.com", "token": "eyJ...", "expiresAt": "2026-11-11T13:34:56Z" }
```

O app passa `endpoint` e `token` para o conector do PowerSync (`fetchCredentials`). Enquanto a
instância do PowerSync não existir, o `endpoint` é um valor provisório.

## Perfil

### `GET /usuarios/me`

`200 OK`

```json
{ "id": "uuid", "nome": "Arthur", "avatarUrl": "https://..." }
```

### `PATCH /usuarios/me`

```json
{ "nome": "Arthur Alexandre" }
```

`200 OK` com o perfil atualizado. Nome de 2 a 60 caracteres.

### `PUT /usuarios/me/avatar`

`multipart/form-data` com o arquivo no campo **`arquivo`**.

- JPEG, PNG ou WebP, até 5 MB.
- A API recorta e regrava em JPEG 512 × 512 e remove os metadados (inclusive GPS).
- A foto anterior é apagada.

`200 OK`

```json
{ "avatarUrl": "https://tbxmjakviucmhvkgmhpy.supabase.co/storage/v1/object/public/avatars/<usuario>/<arquivo>.jpg" }
```

Erros: `413 ARQUIVO_GRANDE`, `415 FORMATO_INVALIDO`.

### `DELETE /usuarios/me/avatar`

`204 No Content`. `avatarUrl` volta a ser `null`.

## Fazendas e membros

### `POST /fazendas`

```json
{ "id": "uuid-opcional-gerado-no-app", "nome": "Fazenda Nova" }
```

`201 Created`

```json
{ "id": "uuid", "nome": "Fazenda Nova" }
```

Quem cria vira owner. Nome até 100 caracteres.

### `PATCH /fazendas/{id}`

Só owner.

```json
{ "nome": "Fazenda Renomeada" }
```

`200 OK` com `{ "id", "nome" }`.

### `PATCH /fazendas/{fazendaId}/membros/{usuarioId}`

Só owner.

```json
{ "papel": "owner" }
```

`papel`: `"owner"` ou `"collaborator"`. `204 No Content`. Erros: `409 ULTIMO_OWNER` ao rebaixar o
único owner, `404 NAO_ENCONTRADO`.

### `DELETE /fazendas/{fazendaId}/membros/{usuarioId}`

Só owner. `204 No Content`. No sync seguinte, os dados da fazenda somem do celular da pessoa
removida. Erros: `409 ULTIMO_OWNER`, `404 NAO_ENCONTRADO`.

### `DELETE /fazendas/{fazendaId}/membros/me`

Sai da fazenda. `204 No Content`. Erro: `409 ULTIMO_OWNER` se for o único owner (promova outra
pessoa antes).

## Convites

### `POST /fazendas/{fazendaId}/convites`

Só owner. Sem corpo.

`201 Created`

```json
{
  "id": "uuid",
  "url": "minhafazenda://app/convite/<token>",
  "token": "<token>",
  "expiraEm": "2026-11-13T12:00:00Z"
}
```

O convite vale 48 horas e só pode ser usado uma vez. Compartilhe a `url`.

### `DELETE /convites/{id}`

Só owner da fazenda do convite. `204 No Content`.

### `POST /convites/aceitar`

Para quem já tem conta. Quem ainda não tem conta usa `conviteToken` no `/auth/registro`.

```json
{ "token": "<token>" }
```

`200 OK`

```json
{ "fazendaId": "uuid" }
```

Entra como collaborator. Erro: `410 CONVITE_INVALIDO`.

## Sync

### `POST /sync/upload`

O `uploadData` do conector do PowerSync manda a fila de escritas locais.

```json
{
  "schemaVersion": 1,
  "ops": [
    { "op": "PUT", "table": "animais", "id": "uuid", "data": {
        "fazenda_id": "uuid", "brinco": "1023", "sexo": "F", "nascimento_aproximado": false,
        "ja_pariu": false, "crias_anteriores": 0, "status": "ativo", "parto_assistido": false,
        "created_at": "2026-11-11T12:00:00.000Z", "updated_at": "2026-11-11T12:00:00.000Z" } },
    { "op": "PATCH", "table": "animais", "id": "uuid", "data": {
        "nome": "Mimosa", "updated_at": "2026-11-11T12:05:00.000Z" } },
    { "op": "PATCH", "table": "pesagens", "id": "uuid", "data": {
        "deleted_at": "2026-11-11T12:06:00.000Z", "updated_at": "2026-11-11T12:06:00.000Z" } }
  ]
}
```

`200 OK`

```json
{
  "aplicadas": 2,
  "rejeitadas": [
    { "table": "pesagens", "id": "uuid", "codigo": "SEM_PERMISSAO", "mensagem": "Seu papel não permite apagar registros." }
  ]
}
```

Como a API trata cada operação:

- **Cada operação é independente.** Uma rejeitada não derruba as outras; o lote responde `200` e
  as rejeitadas voltam em `rejeitadas`. O app deve concluir a transação do PowerSync mesmo com
  rejeições (a API guarda cada rejeição em `sync_rejeicoes`).
- **`PUT`** cria o registro, ou sobrescreve as colunas enviadas se ele já existir. Registro novo
  precisa de `fazenda_id` e de todas as colunas obrigatórias da tabela.
- **`PATCH`** altera só as colunas enviadas. Registro que não existe → `NAO_ENCONTRADO`.
- **Apagar** é `PATCH` com `deleted_at` (soft delete). Um `DELETE` também marca `deleted_at`.
- **O servidor controla** `id`, `fazenda_id`, `created_by` e `server_updated_at`: esses valores
  no `data` são ignorados, e a fazenda de um registro existente nunca muda. Colunas que a tabela
  não tem também são ignoradas.
- **Tipos**: booleanos como `true`/`false`, números como número JSON, JSON como texto (string),
  datas como texto ISO 8601.
- **Enviar o mesmo lote de novo** deixa o banco igual (é seguro repetir).

Códigos em `rejeitadas[].codigo`: `VALIDACAO` (valor incompatível, coluna obrigatória faltando,
tabela que não sincroniza), `SEM_PERMISSAO` (não é membro, ou collaborator apagando quando não
pode), `NAO_ENCONTRADO`.

Erros do lote inteiro: `401 TOKEN_INVALIDO`, `426 APP_DESATUALIZADO` (`schemaVersion` abaixo do
mínimo, hoje `1`), `400 VALIDACAO` (corpo malformado).

Tabelas aceitas (schema v1 do app):

```
fazenda_config, lotes, animais, pesagens, reproducoes, tratamentos, doses_aplicadas,
avaliacoes_corporais, controles_leiteiros, secagens, mastites, cios, exames, anotacoes_animal,
ordenhas, maquinas, usos_maquina, paradas_uso, abastecimentos, leituras_horimetro,
ocorrencias_maquina, anotacoes_maquina, talhoes, marcadores_mapa, movimentacoes, calculos_cerca,
modelos_cultura, ciclos_plantio, etapas_ciclo, usos_area, tarefas, tarefa_eventos
```

`fazendas`, `fazenda_membros` e `usuarios` não são aceitas no sync: mudam só pelos endpoints de
fazenda, membros e perfil.

## Infra

### `GET /ping`

`204 No Content`. Sem token.

### `GET /.well-known/jwks.json`

Chave pública que assina os tokens. Configurada no PowerSync; o app não precisa chamar.

## Observações do ambiente de teste

- Plano gratuito do Render: depois de um deploy, a API leva cerca de 3 minutos para responder.
  Fora isso ela fica acordada: um ping interno roda a cada 10 minutos.
- O banco e o Storage são o projeto de teste do Supabase.
