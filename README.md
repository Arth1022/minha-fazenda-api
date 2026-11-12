# Minha Fazenda — API

API Spring Boot do Minha Fazenda, app mobile offline-first para pequenos produtores rurais.
O app grava sempre no SQLite local; as escritas chegam aqui pela fila do PowerSync
(`POST /sync/upload`) e as leituras descem do Postgres para o celular pelo PowerSync Service,
sem passar pela API.

A API cuida de quatro coisas:

- **Conta**: JWT próprio (RS256), sessões por aparelho e refresh token com rotação.
- **Perfil**: nome e avatar (regravado em JPEG 512 × 512, sem EXIF).
- **Fazendas**: membros, papéis (`owner` / `collaborator`), convites de uso único e auditoria.
- **Sync**: um único endpoint que valida e aplica as operações da fila do app.

## Stack

| Item | Versão / escolha |
| --- | --- |
| Java | 21 |
| Spring Boot | 4.1.2 (Spring Framework 7, Spring Security 7, Hibernate 7, Jackson 3) |
| Banco | PostgreSQL (Supabase em produção; Postgres 15 no Docker local e nos testes) |
| Migrations | Liquibase 5, geradas a partir das Entities com `liquibase-hibernate7` |
| Storage | Supabase Storage pela API compatível com S3 (AWS SDK v2) |
| Imagens | Thumbnailator + leitor WebP do TwelveMonkeys |
| Rate limit | Bucket4j |
| Testes | JUnit 6, MockMvc, Testcontainers (Postgres e S3Mock) |

## Como rodar

Pré-requisitos: JDK 21 e Docker.

```bash
docker compose up -d            # Postgres 15 na porta 54322, com wal_level=logical
./mvnw spring-boot:run -Pdev    # API em http://localhost:8080
```

No perfil `dev` nada precisa ser configurado: o banco aponta para o Docker local, o Liquibase
cria o schema na subida e, sem `JWT_PRIVATE_KEY`, a API gera um par RSA a cada subida (os tokens
deixam de valer ao reiniciar). O único endpoint que depende de serviço externo é o do avatar,
que precisa de um Storage real (variáveis `STORAGE_*`).

Para rodar local contra o banco e o Storage de teste do Supabase, crie um `.env` na raiz (fora do
repositório, modelo em `.env.example`) e use o perfil `supabase`:

```bash
./mvnw spring-boot:run -Psupabase   # perfis dev + supabase; credenciais lidas do .env
```

Primeiro acesso:

```bash
curl -X POST localhost:8080/auth/registro -H "Content-Type: application/json" -d '{
  "nome": "Arthur",
  "credencial": { "tipo": "senha", "email": "a@x.com", "senha": "uma senha boa" },
  "deviceId": "meu-aparelho",
  "nomeFazenda": "Sitio Boa Vista"
}'
```

## Configuração

Tudo que é sensível vem de variável de ambiente. No perfil `prod` todas são obrigatórias e a
API não sobe sem elas; no `dev`, as que têm padrão local aparecem na última coluna.

| Variável | Para quê | Padrão em `dev` |
| --- | --- | --- |
| `DB_URL`, `DB_USER`, `DB_PASS` | Postgres | Docker local (`localhost:54322`) |
| `JWT_PRIVATE_KEY` | chave RSA privada em PEM PKCS#8 (as quebras de linha podem vir como `\n`) | par efêmero |
| `JWT_KID` | identificador da chave publicado na JWKS | `dev` |
| `POWERSYNC_URL` | URL da instância PowerSync (endpoint devolvido ao app e `aud` do token) | `http://localhost:8081` |
| `STORAGE_ENDPOINT` | endpoint S3 do Supabase Storage (`https://<ref>.storage.supabase.co/storage/v1/s3`) | Supabase local |
| `STORAGE_REGION` | região do projeto Supabase | `local` |
| `STORAGE_KEY`, `STORAGE_SECRET` | access key S3 do Storage | `dev` |
| `STORAGE_BUCKET` | bucket dos avatares | `avatars` |
| `STORAGE_PUBLIC_URL` | base pública do bucket (`https://<ref>.supabase.co/storage/v1/object/public/avatars`) | Supabase local |
| `APP_LINK_BASE` | base do link de convite (`<base>/convite/<token>`) | `minhafazenda://app` |
| `SYNC_MIN_SCHEMA_VERSION` | menor `schemaVersion` do app ainda aceito | `1` |

Para gerar a chave de produção:

```bash
openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out jwt-private.pem
```

Outros ajustes ficam no `application.yaml`: validade dos tokens (`jwt.*`), validade e usos do
convite (`convite.*`), tabelas sincronizáveis (`sync.tabelas`), permissão de apagar
(`permissoes.collaborator-pode-apagar`) e rate limit (`rate-limit.requisicoes-por-minuto`).

## Estrutura

Pacotes por camada, em `com.minhafazenda`, sem subpastas por módulo:

| Pacote | Conteúdo |
| --- | --- |
| `controller` | endpoints REST (`AuthController`, `PerfilController`, `FazendaController`, `MembroController`, `ConviteController`, `SyncController`, `JwksController`) |
| `service` | regras de negócio: conta, sessão, JWT, perfil e avatar, fazenda, membros, auditoria, convite e sync (`SyncService`, `AplicadorDeOp`, `ValidadorSync`, `RegistroDeRejeicoes`) |
| `repository` | repositórios JPA e o `SyncRepository` (SQL do sync) |
| `entity` | Entities JPA, fonte da verdade do schema: as 7 tabelas da API e as 30 de domínio do app (`BaseFazendaEntity`, `Usuario`, `Animal`, `Maquina`, `Talhao`...) |
| `enums` | enums das colunas, gravados com o valor do app (`Sexo.FEMEA` ↔ `"F"`) por um conversor JPA |
| `dto` | requests e responses do contrato e os valores trocados entre services (`SessaoAberta`, `AccessToken`, `TokenPowerSync`) |
| `config` | `@Configuration` e `@ConfigurationProperties` (Spring Security, JWT, Storage, relógio, propriedades) |
| `security` | rate limit, usuário autenticado, validação de sessão, tokens opacos e os `CredencialVerifier` |
| `exception` | `ApiException`, códigos de erro e o `@RestControllerAdvice` no formato do contrato |
| `infra` | integrações técnicas: Storage S3, processamento de imagem, o `SchemaCache` lido do `information_schema` e o ping que mantém a API acordada no Render |

Dependências: `controller` → `service` → `repository` / `infra` → `entity`; `dto`, `exception` e
`config` são usados por todas as camadas.

## Contrato

JSON em `camelCase`, datas em ISO 8601 UTC. Toda rota exige `Authorization: Bearer <accessToken>`,
exceto registro, login, refresh e `/.well-known/*`.

| Rota | Quem pode |
| --- | --- |
| `POST /auth/registro`, `/auth/login`, `/auth/refresh` | público |
| `POST /auth/logout`, `GET /auth/powersync-token` | logado |
| `GET /.well-known/jwks.json` | público (o PowerSync lê a chave daqui) |
| `GET /ping` | público (health check e ping que mantém a API acordada) |
| `GET`/`PATCH /usuarios/me`, `PUT`/`DELETE /usuarios/me/avatar` | logado |
| `POST /fazendas` | logado (vira owner) |
| `PATCH /fazendas/{id}` | owner |
| `POST /fazendas/{id}/convites`, `DELETE /convites/{id}` | owner |
| `POST /convites/aceitar` | logado |
| `PATCH`/`DELETE /fazendas/{id}/membros/{usuarioId}` | owner |
| `DELETE /fazendas/{id}/membros/me` | membro |
| `POST /sync/upload` | logado + membro da fazenda de cada registro |

Todo erro tem o corpo `{ "erro": "<CODIGO>", "mensagem": "<texto para o usuário>" }`:

| HTTP | Código | Quando |
| --- | --- | --- |
| 400 | `VALIDACAO` | campo inválido |
| 401 | `TOKEN_INVALIDO` | access token ausente, vencido, inválido ou de sessão revogada |
| 401 | `SESSAO_ENCERRADA` | refresh revogado, vencido, reutilizado ou de outro aparelho |
| 401 | `CREDENCIAL_INVALIDA` | e-mail ou senha incorretos (fora do contrato original; o app precisa tratar) |
| 403 | `SEM_PERMISSAO` | não é membro ou o papel não permite |
| 404 | `NAO_ENCONTRADO` | recurso não existe ou não é visível |
| 409 | `ULTIMO_OWNER` | a ação deixaria a fazenda sem owner |
| 410 | `CONVITE_INVALIDO` | convite vencido, revogado, esgotado ou desconhecido |
| 413 | `ARQUIVO_GRANDE` | avatar acima de 5 MB |
| 415 | `FORMATO_INVALIDO` | avatar que não é JPEG, PNG ou WebP |
| 426 | `APP_DESATUALIZADO` | `schemaVersion` abaixo do mínimo |
| 429 | `MUITAS_TENTATIVAS` | rate limit |
| 500 | `ERRO_INTERNO` | erro inesperado; no sync, o app reenvia o lote |

## Como as partes funcionam

### Autenticação

- **Access token**: JWT RS256 de 30 min com `sub`, `nome`, `sid`, `iat` e `exp`. Fazenda e papel
  não vão no token: são lidos de `fazenda_membros` a cada requisição, então remover alguém vale
  na hora.
- **Refresh token**: 32 bytes aleatórios em Base64URL, 90 dias. O banco guarda só o SHA-256, na
  tabela `sessoes`, junto com o `device_id`.
- **Rotação**: cada refresh troca o hash. A sessão guarda também o hash anterior
  (`refresh_hash_anterior`); se ele reaparecer, o token foi copiado e a sessão é revogada.
  Refresh vindo de outro `deviceId` também revoga.
- **Revogação**: o `sid` de uma sessão revogada é recusado por um validador do JWT, com cache de
  30 s (`jwt.cache-sessao-segundos`). Na instância que revogou, o efeito é imediato.
- **PowerSync**: `GET /auth/powersync-token` emite um JWT de 60 min com `aud` = `POWERSYNC_URL`,
  assinado pela mesma chave. Ele não leva `sid`, por isso nunca é aceito como access token.
- **Método de login**: a interface `CredencialVerifier` isola o método. Em todos os perfis vale o
  `SenhaCredencialVerifier` (`tipo: "senha"`, com `email` e `senha`): a senha tem de 8 caracteres a
  72 bytes e o banco guarda só o hash bcrypt em `usuarios.credencial_hash`. Senha errada e e-mail
  inexistente recebem a mesma resposta, `401 CREDENCIAL_INVALIDA`. No perfil `dev` existe também o
  `DevCredencialVerifier` (`tipo: "dev"`), que aceita o e-mail sem verificação.

### Avatar

O tipo é reconhecido pelos primeiros bytes, não pelo `Content-Type`. A imagem é recortada no
centro, reduzida para 512 × 512 e regravada em JPEG com qualidade 0,85, o que descarta o EXIF.
O arquivo vai para `avatars/{usuarioId}/{uuid}.jpg`; o anterior só é apagado depois do commit.

### Permissões

`MembroService.exigirMembro` e `exigirOwner` são os únicos pontos de checagem, usados pelos
controllers e pelo sync. Remover um membro preenche `removed_at`, nunca apaga a linha. Toda ação
que deixaria a fazenda sem owner ativo responde `409 ULTIMO_OWNER`. Convite criado, revogado e
aceito, papel alterado e membro removido ficam em `auditoria_fazenda`.

### Sync

`POST /sync/upload` recebe `{ schemaVersion, ops: [{ op, table, id, data }] }` e aplica tudo, em
ordem, numa transação:

1. A tabela precisa estar em `sync.tabelas`. As tabelas de conta, fazenda e convite nunca entram,
   e a API não sobe se alguma for colocada lá.
2. A fazenda da operação é a do registro existente, ou o `fazenda_id` do `data` num PUT novo.
3. O usuário precisa ser membro ativo dessa fazenda, e o papel precisa permitir a operação.
4. Roda o `ValidadorSync` da tabela, se houver um bean para ela.
5. PUT é upsert, PATCH altera só as colunas enviadas, DELETE preenche `deleted_at`.

Nomes de tabela e coluna saem sempre do `SchemaCache` (lido do `information_schema` na subida);
valores entram só como parâmetros, com cast para o tipo real da coluna. Colunas desconhecidas são
ignoradas, assim como `id`, `created_by`, `server_updated_at` e `fazenda_id` de registro
existente.

Operação recusada (permissão, validação, valor que o Postgres não aceita) é gravada em
`sync_rejeicoes`, entra em `rejeitadas` e a requisição continua com 200, para a fila do app não
travar. Cada operação roda num savepoint, então a recusa de uma não desfaz as outras. Erro
inesperado desfaz o lote inteiro e responde 500.

## Mudanças de schema

As Entities são a fonte da verdade. Nunca escreva SQL de migration à mão.

1. Registre a mudança no [SCHEMA_CHANGES.md](SCHEMA_CHANGES.md) (ID `SC-xxx`).
2. Altere a Entity. Coluna nova é sempre nullable; renomear ou remover segue expand → contract.
3. Com o Postgres local no ar e atualizado (`./mvnw liquibase:update`), gere o changeset:

   ```bash
   ./mvnw compile liquibase:diff
   ```

4. Revise o arquivo criado em `src/main/resources/db/changelog/changes/` e coloque o `SC-xxx` no
   `comment` de cada changeset.
5. Tabela de domínio nova: estenda `BaseFazendaEntity`, declare o índice em `fazenda_id`,
   acrescente a tabela em `sync.tabelas` e um bloco em `powersync/sync-streams.yaml`.

Quando Entities e banco estão iguais, o `liquibase:diff` não gera changeset; o CI falha se gerar.

Há duas migrations escritas à mão, e só elas:

- `…-powersync-publication.yaml`: `CREATE PUBLICATION powersync FOR ALL TABLES`, a exceção
  prevista no projeto, mais o `REPLICA IDENTITY FULL` da tabela de controle do Liquibase (sem
  chave primária, ela não aceitaria `UPDATE` depois da publication).
- `zzz-rls-todas-as-tabelas.yaml`: liga row level security em toda tabela, a cada subida. No
  Supabase, tabela do schema `public` sem RLS fica aberta pela Data API a quem tiver a chave
  pública do projeto. Com RLS e nenhuma policy, só o dono das tabelas (o usuário que a API usa) e
  papéis com `BYPASSRLS` (o do PowerSync) leem e gravam.

## Testes

```bash
./mvnw verify
```

A suíte sobe um Postgres real com Testcontainers, aplica o Liquibase e exercita a API inteira
por HTTP (MockMvc), incluindo a cadeia de segurança e o rate limit. Precisa de Docker rodando.

| Onde | O que cobre |
| --- | --- |
| `controller` | pela API: registro com e sem convite, login, rotação e reuso de refresh, logout, claims dos tokens, JWKS, perfil e avatar (EXIF removido, formatos, limite de 5 MB), papéis, último owner, convites usados, vencidos e revogados, auditoria, sync (isolamento entre fazendas, SQL injection, idempotência, coluna nova, rejeições, 426) |
| `security` | rate limit por IP, geração e hash de tokens, perfil do `DevCredencialVerifier` |
| `service` | validação do nome |
| `infra` | formatos de imagem, adaptador S3 contra um servidor S3 real, streams do PowerSync, FK e índice de `fazenda_id`, RLS e publication |

O teste `ContratoDeSchemaIntegrationTest` também gera `contracts/schema-colunas.json`, que o front
compara com o schema do Drizzle.

O CI (`.github/workflows/ci.yml`) roda a suíte, confere que o `liquibase:diff` não gera
changeset e publica o `schema-colunas.json` como artefato.

## Produção (Render + Supabase)

O banco e o Storage são o projeto **Minha Fazenda** no Supabase (`tbxmjakviucmhvkgmhpy`, região
`sa-east-1`), com o schema aplicado, RLS ligado, a publication `powersync` e o bucket público
`avatars`. A API roda no Render como Web Service Docker (`Dockerfile` na raiz), com o perfil
`prod`, em **https://minha-fazenda-api.onrender.com** (serviço `minha-fazenda-api`, plano Free, região
Ohio). Cada push na `main` dispara um deploy novo.

Variáveis do serviço no Render: as da tabela de [Configuração](#configuração). O banco usa a string
do **Session pooler** do Supabase (porta 5432): o modo transaction (porta 6543) não serve para o
Hibernate, e a conexão direta só responde por IPv6. `PORT` e `RENDER_EXTERNAL_URL` o próprio
Render preenche.

- **Não dormir**: no plano gratuito o Render desliga a API depois de 15 minutos sem requisições.
  Com `RENDER_EXTERNAL_URL` preenchida, a classe `ManterAcordado` chama `GET /ping` pela URL
  pública a cada 10 minutos (`manter-acordado.intervalo`). Uma instância ligada o mês inteiro
  gasta cerca de 744 das 750 horas gratuitas do mês.
- **Saúde**: `GET /ping` responde `204` sem token; é o health check do Render.
- **PowerSync**: enquanto não houver instância, `POWERSYNC_URL` fica com um valor provisório e o
  `GET /auth/powersync-token` emite um token que nenhum serviço usa. O resto da API não depende dele.

Atrás de proxy, o perfil `prod` já confia no `X-Forwarded-For` para o rate limit por IP
(`server.forward-headers-strategy=native`); só exponha a API por um proxy que sobrescreva esse
header.

## Pendências e decisões em aberto

Marcadas no código com `TODO(decisao)`:

| Decisão | Como está hoje |
| --- | --- |
| Método de login (o "code") | e-mail e senha (`SenhaCredencialVerifier`); login por código ainda não existe |
| PowerSync Cloud ou self-host | tudo por variável de ambiente; serve aos dois |
| Convite com aprovação do owner | entrada direta ao aceitar |
| Collaborator pode apagar registros | pode, atrás de `permissoes.collaborator-pode-apagar=true` |
| Validade do refresh token | 90 dias, em `jwt.refresh-dias` |
| Numeração do `SCHEMA_CHANGES.md` | o back usa SC-011 a SC-041 para os pedidos que o app numerou SC-001 a SC-033 |
| `maquinas.codigo` único | sem UNIQUE no Postgres; dois celulares offline podem gerar o mesmo código |

Limites conhecidos:

- O reuso de refresh é detectado para o token imediatamente anterior. Um token mais antigo é
  recusado, mas não identifica a sessão para revogá-la.
- O rate limit e o cache de sessões revogadas ficam na memória de cada instância. Com mais de
  uma instância, o limite vale por instância e uma revogação leva até 30 s para valer nas outras.
