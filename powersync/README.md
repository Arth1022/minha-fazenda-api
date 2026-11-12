# PowerSync

O PowerSync replica o Postgres para o SQLite de cada celular. A API não fica no caminho das
leituras: ela só emite o token que o app usa para conectar e publica a chave que valida esse
token.

## Configuração da instância

Vale para PowerSync Cloud e para self-host.

| Item | Valor |
| --- | --- |
| JWKS URI | `<URL da API>/.well-known/jwks.json` |
| Audience | o mesmo valor da variável `POWERSYNC_URL` da API |
| Sync Streams | o conteúdo de [`sync-streams.yaml`](sync-streams.yaml) |
| Publication | `powersync` (criada pelo Liquibase como `FOR ALL TABLES`) |

O token vem de `GET /auth/powersync-token`: JWT RS256 com `kid`, `sub` = id do usuário,
`aud` = `POWERSYNC_URL` e validade de 60 minutos. Nos streams, `auth.user_id()` é esse `sub`.

## Conexão com o Postgres

O PowerSync conecta com um usuário próprio, com permissão de replicação. As tabelas têm row
level security ligado e nenhuma policy, então esse usuário também precisa de `BYPASSRLS`:

```sql
CREATE ROLE powersync_role WITH REPLICATION BYPASSRLS LOGIN PASSWORD '<senha forte>';
GRANT SELECT ON ALL TABLES IN SCHEMA public TO powersync_role;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON TABLES TO powersync_role;
```

Use a conexão direta do banco (não o pooler): replicação lógica não passa pelo pooler.

O Postgres local do `docker-compose.yml` já sobe com `wal_level=logical`, então serve para rodar
o PowerSync localmente apontando para ele.

## Streams

Cada usuário recebe só os dados das fazendas em que é membro ativo. Todo stream usa o mesmo
subquery em `fazenda_membros`, com `removed_at IS NULL`: remover alguém tira os dados da fazenda
do celular dele no sync seguinte.

- `fazendas` e `membros`: só leitura no app.
- `colegas`: `id`, `nome` e `avatar_url` dos colegas de fazenda. E-mail e telefone nunca saem do
  servidor.
- Um stream por tabela de domínio (`animais`).

Tabela de domínio nova = um bloco novo no fim do `sync-streams.yaml`, igual ao de `animais`. O
teste `SyncStreamsTest` falha se uma tabela de `sync.tabelas` ficar sem stream ou se algum stream
não filtrar por membro ativo.

Antes do deploy, valide os streams no Sync Diagnostics do PowerSync; os testes do repositório
conferem o texto das queries, não a execução delas na instância.
