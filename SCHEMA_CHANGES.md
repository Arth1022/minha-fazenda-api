# Mudanças de schema

Status: proposto → entity+changeset → API em produção → app publicado → concluído

O ID de cada mudança aparece no `comment` dos changesets em
`src/main/resources/db/changelog/changes/`.

| ID | Data | Tabela | Mudança | Tipo (Postgres) | Nulo? | Pedido por | Status |
|---|---|---|---|---|---|---|---|
| SC-001 | 2026-11-11 | usuarios | + tabela | ver detalhe | ver detalhe | back | entity+changeset |
| SC-002 | 2026-11-11 | sessoes | + tabela | ver detalhe | ver detalhe | back | entity+changeset |
| SC-003 | 2026-11-11 | fazendas | + tabela | ver detalhe | ver detalhe | back | entity+changeset |
| SC-004 | 2026-11-11 | fazenda_membros | + tabela | ver detalhe | ver detalhe | back | entity+changeset |
| SC-005 | 2026-11-11 | convites | + tabela | ver detalhe | ver detalhe | back | entity+changeset |
| SC-006 | 2026-11-11 | sync_rejeicoes | + tabela | ver detalhe | ver detalhe | back | entity+changeset |
| SC-007 | 2026-11-11 | auditoria_fazenda | + tabela | ver detalhe | ver detalhe | back | entity+changeset |
| SC-008 | 2026-11-11 | animais | + tabela de domínio com as colunas de `BaseFazendaEntity` | ver detalhe | ver detalhe | back | entity+changeset |
| SC-009 | 2026-11-11 | (todas) | + publication `powersync` FOR ALL TABLES | — | — | back | entity+changeset |
| SC-010 | 2026-11-11 | (todas) | row level security ligado em toda tabela | — | — | back | entity+changeset |
| SC-011 | 2026-11-11 | fazenda_config | + tabela de domínio | ver detalhe | ver detalhe | app | entity+changeset |
| SC-012 | 2026-11-11 | lotes | + tabela de domínio | ver detalhe | ver detalhe | app | entity+changeset |
| SC-013 | 2026-11-11 | animais | + 26 colunas do app (a tabela já existia só com `nome`) | ver detalhe | ver detalhe | app | entity+changeset |
| SC-014 | 2026-11-11 | pesagens | + tabela de domínio | ver detalhe | ver detalhe | app | entity+changeset |
| SC-015 | 2026-11-11 | reproducoes | + tabela de domínio | ver detalhe | ver detalhe | app | entity+changeset |
| SC-016 | 2026-11-11 | tratamentos | + tabela de domínio | ver detalhe | ver detalhe | app | entity+changeset |
| SC-017 | 2026-11-11 | doses_aplicadas | + tabela de domínio | ver detalhe | ver detalhe | app | entity+changeset |
| SC-018 | 2026-11-11 | avaliacoes_corporais | + tabela de domínio | ver detalhe | ver detalhe | app | entity+changeset |
| SC-019 | 2026-11-11 | controles_leiteiros | + tabela de domínio | ver detalhe | ver detalhe | app | entity+changeset |
| SC-020 | 2026-11-11 | secagens | + tabela de domínio | ver detalhe | ver detalhe | app | entity+changeset |
| SC-021 | 2026-11-11 | mastites | + tabela de domínio | ver detalhe | ver detalhe | app | entity+changeset |
| SC-022 | 2026-11-11 | cios | + tabela de domínio | ver detalhe | ver detalhe | app | entity+changeset |
| SC-023 | 2026-11-11 | exames | + tabela de domínio | ver detalhe | ver detalhe | app | entity+changeset |
| SC-024 | 2026-11-11 | anotacoes_animal | + tabela de domínio | ver detalhe | ver detalhe | app | entity+changeset |
| SC-025 | 2026-11-11 | ordenhas | + tabela de domínio | ver detalhe | ver detalhe | app | entity+changeset |
| SC-026 | 2026-11-11 | maquinas | + tabela de domínio | ver detalhe | ver detalhe | app | entity+changeset |
| SC-027 | 2026-11-11 | usos_maquina | + tabela de domínio | ver detalhe | ver detalhe | app | entity+changeset |
| SC-028 | 2026-11-11 | paradas_uso | + tabela de domínio | ver detalhe | ver detalhe | app | entity+changeset |
| SC-029 | 2026-11-11 | abastecimentos | + tabela de domínio | ver detalhe | ver detalhe | app | entity+changeset |
| SC-030 | 2026-11-11 | leituras_horimetro | + tabela de domínio | ver detalhe | ver detalhe | app | entity+changeset |
| SC-031 | 2026-11-11 | ocorrencias_maquina | + tabela de domínio | ver detalhe | ver detalhe | app | entity+changeset |
| SC-032 | 2026-11-11 | anotacoes_maquina | + tabela de domínio | ver detalhe | ver detalhe | app | entity+changeset |
| SC-033 | 2026-11-11 | talhoes | + tabela de domínio | ver detalhe | ver detalhe | app | entity+changeset |
| SC-034 | 2026-11-11 | marcadores_mapa | + tabela de domínio | ver detalhe | ver detalhe | app | entity+changeset |
| SC-035 | 2026-11-11 | movimentacoes | + tabela de domínio | ver detalhe | ver detalhe | app | entity+changeset |
| SC-036 | 2026-11-11 | calculos_cerca | + tabela de domínio | ver detalhe | ver detalhe | app | entity+changeset |
| SC-037 | 2026-11-11 | modelos_cultura | + tabela de domínio | ver detalhe | ver detalhe | app | entity+changeset |
| SC-038 | 2026-11-11 | ciclos_plantio | + tabela de domínio | ver detalhe | ver detalhe | app | entity+changeset |
| SC-039 | 2026-11-11 | etapas_ciclo | + tabela de domínio | ver detalhe | ver detalhe | app | entity+changeset |
| SC-040 | 2026-11-11 | usos_area | + tabela de domínio | ver detalhe | ver detalhe | app | entity+changeset |
| SC-041 | 2026-11-11 | (as 7 tabelas da API e animais.nome) | colunas de texto passam de varchar(255) para text | text | — | app | entity+changeset |
| SC-042 | 2026-11-12 | tarefas | + tabela de domínio | ver detalhe | ver detalhe | app | entity+changeset |
| SC-043 | 2026-11-12 | tarefa_eventos | + tabela de domínio | ver detalhe | ver detalhe | app | entity+changeset |
| SC-044 | 2026-11-12 | calculos_cerca, talhoes, maquinas, abastecimentos, leituras_horimetro, animais | renomeia 8 colunas com sufixo de unidade para snake_case (`espacamentom` → `espacamento_m` etc.) | — | — | app | entity+changeset |
| SC-045 | 2026-11-12 | tarefa_eventos | `dados` de jsonb para text | text | sim | app | entity+changeset |
| SC-046 | 2026-11-12 | usos_maquina | reparo: uso descartado (apagado) sem `fim` recebe `fim = deleted_at` | — | — | app | entity+changeset |
| SC-047 | 2026-11-12 | storage.buckets | + bucket privado `midias` (fotos, áudios, documentos) | — | — | app | entity+changeset |
| SC-048 | 2026-11-12 | (todas) | SELECT do papel de replicação do PowerSync em toda tabela, também nas futuras | — | — | back | entity+changeset |

Todas as mudanças acima já estão aplicadas no projeto Supabase; passam a "API em produção" quando
a API subir lá.

SC-011 a SC-040 vêm do mapeamento do schema do app (`src/entities/`, `SCHEMA_VERSION = 1`). O app
numera os mesmos pedidos de SC-001 a SC-033 no `SCHEMA_CHANGES.md` dele.
<!-- TODO(decisao): unificar a numeração com o SCHEMA_CHANGES.md do app. -->

Regras desta leva:
- Nenhuma FK nem UNIQUE além de `fazenda_id`: o app não tem essas restrições, e um conflito entre dois
  celulares offline viraria rejeição no `/sync/upload`.
- Colunas "Nulo? = não" do app são `NOT NULL` no Postgres. Coluna nova depois desta leva é sempre nullable.

## SC-001 — usuarios
- Motivo: conta e perfil do usuário
- Colunas: `id uuid`, `nome varchar NOT NULL`, `email varchar` (único), `telefone varchar`,
  `credencial_hash varchar`, `avatar_url varchar`, `created_at timestamptz NOT NULL`,
  `updated_at timestamptz NOT NULL`
- Sync Stream: `colegas`, só `id`, `nome` e `avatar_url`
- Changeset: db/changelog/changes/20261111153447-auto.yaml

## SC-002 — sessoes
- Motivo: um aparelho logado por linha, com o hash do refresh token
- Colunas: `id uuid`, `usuario_id uuid NOT NULL` (FK), `device_id varchar NOT NULL`,
  `refresh_hash varchar NOT NULL` (único), `refresh_hash_anterior varchar`,
  `expires_at timestamptz NOT NULL`, `revoked_at timestamptz`, `created_at timestamptz NOT NULL`
- Além da arquitetura: `refresh_hash_anterior` (detecta o reuso de um refresh já trocado) e
  `created_at`
- Sync Stream: não sincroniza
- Changeset: db/changelog/changes/20261111153447-auto.yaml

## SC-003 — fazendas
- Motivo: a fazenda, unidade de permissão e de sync
- Colunas: `id uuid`, `nome varchar NOT NULL`, `created_by uuid`, `created_at timestamptz NOT NULL`,
  `updated_at timestamptz NOT NULL`, `deleted_at timestamptz`
- Sync Stream: `fazendas` (só leitura no app)
- Changeset: db/changelog/changes/20261111153447-auto.yaml

## SC-004 — fazenda_membros
- Motivo: quem pertence a cada fazenda e com qual papel
- Colunas: `id uuid`, `fazenda_id uuid NOT NULL` (FK), `usuario_id uuid NOT NULL` (FK),
  `papel varchar NOT NULL` (`owner` / `collaborator`), `created_at timestamptz NOT NULL`,
  `removed_at timestamptz`; único `(fazenda_id, usuario_id)`
- Sync Stream: `membros` (só leitura no app)
- Changeset: db/changelog/changes/20261111153447-auto.yaml

## SC-005 — convites
- Motivo: convite de uso único, com validade e revogação
- Colunas: `id uuid`, `fazenda_id uuid NOT NULL` (FK), `token_hash varchar NOT NULL` (único),
  `criado_por uuid NOT NULL`, `expira_em timestamptz NOT NULL`, `usos_max integer NOT NULL`,
  `usos integer NOT NULL`, `revogado_em timestamptz`, `created_at timestamptz NOT NULL`
- Sync Stream: não sincroniza
- Changeset: db/changelog/changes/20261111153447-auto.yaml

## SC-006 — sync_rejeicoes
- Motivo: guardar as operações do app que a API recusou
- Colunas: `id uuid`, `usuario_id uuid NOT NULL` (FK), `tabela varchar NOT NULL`,
  `registro_id varchar NOT NULL`, `op varchar NOT NULL`, `dados jsonb`, `motivo varchar NOT NULL`,
  `created_at timestamptz NOT NULL`
- Sync Stream: não sincroniza
- Changeset: db/changelog/changes/20261111153447-auto.yaml

## SC-007 — auditoria_fazenda
- Motivo: registrar quem convidou, quem entrou, quem mudou papel e quem removeu
- Colunas: `id uuid`, `fazenda_id uuid NOT NULL` (FK), `ator_id uuid NOT NULL`,
  `acao varchar NOT NULL`, `alvo_id uuid`, `created_at timestamptz NOT NULL`
- Sync Stream: não sincroniza
- Changeset: db/changelog/changes/20261111153447-auto.yaml

## SC-008 — animais
- Motivo: tabela de domínio de referência, tirada do exemplo do contrato do sync
- Colunas: `id uuid`, `fazenda_id uuid NOT NULL` (FK, com índice), `created_by uuid`,
  `created_at timestamptz`, `updated_at timestamptz`, `deleted_at timestamptz`,
  `server_updated_at timestamptz`, `nome varchar`
- Impacto em apps antigos: nenhum (tabela nova)
- Sync Stream: `animais`
- Changeset: db/changelog/changes/20261111153447-auto.yaml

## SC-009 — publication powersync
- Motivo: o PowerSync replica as tabelas a partir desta publication; `FOR ALL TABLES` inclui
  sozinha as tabelas novas
- Também define `REPLICA IDENTITY FULL` em `databasechangelog`: a tabela de controle do Liquibase
  não tem chave primária e, dentro da publication, não aceitaria `UPDATE`
- Changeset: db/changelog/changes/20261111160000-powersync-publication.yaml (escrito à mão)

## SC-010 — row level security
- Motivo: no Supabase, tabela do schema `public` sem RLS fica aberta pela Data API
- Sem policies: só o dono das tabelas (o usuário da API) e papéis com `BYPASSRLS` acessam
- Roda a cada subida (`runAlways`), então cobre as tabelas criadas depois
- Changeset: db/changelog/changes/zzz-rls-todas-as-tabelas.yaml (escrito à mão)

## SC-011 — fazenda_config
- Motivo: Configuração da fazenda: uma linha por fazenda, com id igual ao fazenda_id.
- Colunas, além das de `BaseFazendaEntity`: `tipo_producao text (enum) NOT NULL`, `divisa_json text`, `area_hectares numeric`, `min_lat numeric`, `max_lat numeric`, `min_lng numeric`, `max_lng numeric`
- Sync Stream: `fazenda_config`
- Changeset: db/changelog/changes/20261112012941-auto.yaml

## SC-012 — lotes
- Motivo: Lote de animais, com o pasto em que foi colocado inteiro.
- Colunas, além das de `BaseFazendaEntity`: `nome text NOT NULL`, `descricao text`, `area_id uuid`, `area_desde timestamptz`
- Sync Stream: `lotes`
- Changeset: db/changelog/changes/20261112012941-auto.yaml

## SC-013 — animais
- Motivo: Animal do rebanho.
- Colunas, além das de `BaseFazendaEntity`: `brinco text NOT NULL`, `nome text`, `raca text`, `sexo text (enum) NOT NULL`, `data_nascimento timestamptz`, `nascimento_aproximado boolean NOT NULL`, `ja_pariu boolean NOT NULL`, `crias_anteriores integer NOT NULL`, `mae_id uuid`, `pai_id uuid`, `pai_descricao text`, `origem text (enum)`, `data_entrada timestamptz`, `status text (enum) NOT NULL`, `data_saida timestamptz`, `motivo_saida text`, `lote_id uuid`, `producao_leite_l numeric`, `racao_kg_dia numeric`, `valor_compra numeric`, `valor_venda numeric`, `peso_saida_kg numeric`, `sisbov text`, `grau_sangue text`, `foto_uri text`, `data_desmame timestamptz`, `parto_assistido boolean NOT NULL`
- Sync Stream: `animais`
- Changeset: db/changelog/changes/20261112012941-auto.yaml

## SC-014 — pesagens
- Motivo: Pesagem do animal (balança ou fita).
- Colunas, além das de `BaseFazendaEntity`: `animal_id uuid NOT NULL`, `data timestamptz NOT NULL`, `peso_kg numeric NOT NULL`, `metodo text (enum) NOT NULL`
- Sync Stream: `pesagens`
- Changeset: db/changelog/changes/20261112012941-auto.yaml

## SC-015 — reproducoes
- Motivo: Cobertura ou inseminação, com diagnóstico, parto e aborto.
- Colunas, além das de `BaseFazendaEntity`: `animal_id uuid NOT NULL`, `tipo text (enum) NOT NULL`, `data timestamptz NOT NULL`, `prenhez_confirmada boolean`, `previsao_parto timestamptz`, `data_parto timestamptz`, `cria_id uuid`, `touro_id uuid`, `touro_descricao text`, `data_diagnostico timestamptz`, `metodo_diagnostico text (enum)`, `data_aborto timestamptz`
- Sync Stream: `reproducoes`
- Changeset: db/changelog/changes/20261112012941-auto.yaml

## SC-016 — tratamentos
- Motivo: Tratamento com agenda de doses e carências.
- Colunas, além das de `BaseFazendaEntity`: `animal_id uuid NOT NULL`, `medicamento text NOT NULL`, `dose text`, `data_inicio timestamptz NOT NULL`, `data_fim timestamptz`, `dias_semana text NOT NULL`, `horario text`, `intervalo_horas integer`, `aviso_minutos_antes integer NOT NULL`, `notificacao_ativa boolean NOT NULL`, `carencia_leite_dias integer`, `carencia_carne_dias integer`, `observacoes text`, `grupo_aplicacao_id uuid`, `comprovante_uri text`
- Sync Stream: `tratamentos`
- Changeset: db/changelog/changes/20261112012941-auto.yaml

## SC-017 — doses_aplicadas
- Motivo: Dose confirmada num dia do tratamento.
- Colunas, além das de `BaseFazendaEntity`: `tratamento_id uuid NOT NULL`, `dia timestamptz NOT NULL`
- Sync Stream: `doses_aplicadas`
- Changeset: db/changelog/changes/20261112012941-auto.yaml

## SC-018 — avaliacoes_corporais
- Motivo: Escore de condição corporal (1 a 5).
- Colunas, além das de `BaseFazendaEntity`: `animal_id uuid NOT NULL`, `data timestamptz NOT NULL`, `escore numeric NOT NULL`
- Sync Stream: `avaliacoes_corporais`
- Changeset: db/changelog/changes/20261112012941-auto.yaml

## SC-019 — controles_leiteiros
- Motivo: Litros da vaca num dia.
- Colunas, além das de `BaseFazendaEntity`: `animal_id uuid NOT NULL`, `data timestamptz NOT NULL`, `litros numeric NOT NULL`
- Sync Stream: `controles_leiteiros`
- Changeset: db/changelog/changes/20261112012941-auto.yaml

## SC-020 — secagens
- Motivo: Secagem da vaca.
- Colunas, além das de `BaseFazendaEntity`: `animal_id uuid NOT NULL`, `data timestamptz NOT NULL`
- Sync Stream: `secagens`
- Changeset: db/changelog/changes/20261112012941-auto.yaml

## SC-021 — mastites
- Motivo: Caso de mastite com os tetos afetados.
- Colunas, além das de `BaseFazendaEntity`: `animal_id uuid NOT NULL`, `data timestamptz NOT NULL`, `tetos text NOT NULL`, `tipo text (enum) NOT NULL`, `observacoes text`
- Sync Stream: `mastites`
- Changeset: db/changelog/changes/20261112012941-auto.yaml

## SC-022 — cios
- Motivo: Cio observado.
- Colunas, além das de `BaseFazendaEntity`: `animal_id uuid NOT NULL`, `data timestamptz NOT NULL`, `observacoes text`
- Sync Stream: `cios`
- Changeset: db/changelog/changes/20261112012941-auto.yaml

## SC-023 — exames
- Motivo: Exame do animal, com validade.
- Colunas, além das de `BaseFazendaEntity`: `animal_id uuid NOT NULL`, `data timestamptz NOT NULL`, `tipo text (enum) NOT NULL`, `descricao text`, `resultado text (enum) NOT NULL`, `validade_dias integer`, `comprovante_uri text`
- Sync Stream: `exames`
- Changeset: db/changelog/changes/20261112012941-auto.yaml

## SC-024 — anotacoes_animal
- Motivo: Anotação livre da ficha do animal.
- Colunas, além das de `BaseFazendaEntity`: `animal_id uuid NOT NULL`, `data timestamptz NOT NULL`, `texto text NOT NULL`
- Sync Stream: `anotacoes_animal`
- Changeset: db/changelog/changes/20261112012941-auto.yaml

## SC-025 — ordenhas
- Motivo: Horário e registro de ordenha por turno.
- Colunas, além das de `BaseFazendaEntity`: `periodo text (enum) NOT NULL`, `horario_previsto text`, `horario_notificacao text`, `volume_litros numeric`, `data_registro timestamptz NOT NULL`
- Sync Stream: `ordenhas`
- Changeset: db/changelog/changes/20261112012941-auto.yaml

## SC-026 — maquinas
- Motivo: Máquina ou implemento; codigo é o ID curto impresso no QR.
- Colunas, além das de `BaseFazendaEntity`: `codigo text NOT NULL`, `nome text NOT NULL`, `tipo text (enum) NOT NULL`, `categoria text (enum) NOT NULL`, `marca text`, `modelo text`, `ano_fabricacao integer`, `numero_serie text`, `placa text`, `potencia_cv numeric`, `combustivel text (enum)`, `tanque_litros numeric`, `largura_trabalho_m numeric`, `possui_horimetro boolean NOT NULL`, `velocidade_max_kmh numeric`, `horimetro_inicial numeric`, `data_aquisicao timestamptz`, `status text (enum) NOT NULL`
- Sync Stream: `maquinas`
- Changeset: db/changelog/changes/20261112012941-auto.yaml

## SC-027 — usos_maquina
- Motivo: Uso de uma máquina (fim nulo = em uso), com o resumo do rastro.
- Colunas, além das de `BaseFazendaEntity`: `maquina_id uuid NOT NULL`, `inicio timestamptz NOT NULL`, `fim timestamptz`, `operador text`, `operador_id uuid`, `implemento_id uuid`, `atividade text (enum)`, `origem text (enum) NOT NULL`, `fim_motivo text (enum)`, `rastreado boolean NOT NULL`, `inicio_lat numeric`, `inicio_lng numeric`, `fim_lat numeric`, `fim_lng numeric`, `km_total numeric`, `km_trabalhando numeric`, `seg_trabalhando integer`, `seg_deslocando integer`, `seg_parada integer`, `seg_pausa integer`, `seg_sem_sinal integer`, `area_estimada_ha numeric`, `qualidade numeric`, `localizacao_simulada boolean NOT NULL`, `rastro text`, `talhoes text`, `parada_avisada_em timestamptz`, `continua_ate timestamptz`, `regras_desde timestamptz`
- Sync Stream: `usos_maquina`
- Changeset: db/changelog/changes/20261112012941-auto.yaml

## SC-028 — paradas_uso
- Motivo: Pausa ou resposta "Continua" durante um uso.
- Colunas, além das de `BaseFazendaEntity`: `uso_id uuid NOT NULL`, `motivo text (enum) NOT NULL`, `inicio timestamptz NOT NULL`, `fim timestamptz`
- Sync Stream: `paradas_uso`
- Changeset: db/changelog/changes/20261112012941-auto.yaml

## SC-029 — abastecimentos
- Motivo: Abastecimento de máquina.
- Colunas, além das de `BaseFazendaEntity`: `maquina_id uuid NOT NULL`, `uso_id uuid`, `operador text`, `operador_id uuid`, `litros numeric NOT NULL`, `tanque_cheio boolean NOT NULL`, `origem text (enum) NOT NULL`, `horimetro_h numeric`, `lat numeric`, `lng numeric`, `foto_uri text`, `abastecido_em timestamptz NOT NULL`
- Sync Stream: `abastecimentos`
- Changeset: db/changelog/changes/20261112012941-auto.yaml

## SC-030 — leituras_horimetro
- Motivo: Leitura digitada do horímetro.
- Colunas, além das de `BaseFazendaEntity`: `maquina_id uuid NOT NULL`, `uso_id uuid`, `operador text`, `operador_id uuid`, `valor_h numeric NOT NULL`, `estimado_h numeric`, `origem text (enum) NOT NULL`, `foto_uri text`, `lido_em timestamptz NOT NULL`
- Sync Stream: `leituras_horimetro`
- Changeset: db/changelog/changes/20261112012941-auto.yaml

## SC-031 — ocorrencias_maquina
- Motivo: Problema relatado na máquina (resolvida_em nulo = em aberto).
- Colunas, além das de `BaseFazendaEntity`: `maquina_id uuid NOT NULL`, `uso_id uuid`, `operador text`, `operador_id uuid`, `categoria text (enum) NOT NULL`, `pode_usar boolean NOT NULL`, `descricao text`, `foto_uri text`, `lat numeric`, `lng numeric`, `registrado_em timestamptz NOT NULL`, `resolvida_em timestamptz`
- Sync Stream: `ocorrencias_maquina`
- Changeset: db/changelog/changes/20261112012941-auto.yaml

## SC-032 — anotacoes_maquina
- Motivo: Anotação livre da máquina.
- Colunas, além das de `BaseFazendaEntity`: `maquina_id uuid NOT NULL`, `uso_id uuid`, `texto text NOT NULL`
- Sync Stream: `anotacoes_maquina`
- Changeset: db/changelog/changes/20261112012941-auto.yaml

## SC-033 — talhoes
- Motivo: Área do mapa (pasto, lavoura, reserva, outro) com geometria GeoJSON.
- Colunas, além das de `BaseFazendaEntity`: `nome text NOT NULL`, `tipo text (enum) NOT NULL`, `cor text NOT NULL`, `area_hectares numeric NOT NULL`, `perimetro_m numeric`, `geometria_json text NOT NULL`, `centro_lat numeric NOT NULL`, `centro_lng numeric NOT NULL`, `min_lat numeric NOT NULL`, `max_lat numeric NOT NULL`, `min_lng numeric NOT NULL`, `max_lng numeric NOT NULL`, `cultura text`, `fase_lavoura text (enum)`, `data_plantio timestamptz`
- Sync Stream: `talhoes`
- Changeset: db/changelog/changes/20261112012941-auto.yaml

## SC-034 — marcadores_mapa
- Motivo: Ponto de atenção no mapa (praga, daninha, erosão).
- Colunas, além das de `BaseFazendaEntity`: `talhao_id uuid`, `tipo text (enum) NOT NULL`, `titulo text NOT NULL`, `descricao text`, `lat numeric NOT NULL`, `lng numeric NOT NULL`, `resolvido boolean NOT NULL`
- Sync Stream: `marcadores_mapa`
- Changeset: db/changelog/changes/20261112012941-auto.yaml

## SC-035 — movimentacoes
- Motivo: Entrada e saída de animal num pasto (saida_em nulo = local atual).
- Colunas, além das de `BaseFazendaEntity`: `animal_id uuid NOT NULL`, `area_id uuid NOT NULL`, `lote_id uuid`, `entrada_em timestamptz NOT NULL`, `saida_em timestamptz`, `registrado_por text`, `registrado_por_id uuid`
- Sync Stream: `movimentacoes`
- Changeset: db/changelog/changes/20261112012941-auto.yaml

## SC-036 — calculos_cerca
- Motivo: Conta de cerca salva para uma área.
- Colunas, além das de `BaseFazendaEntity`: `area_id uuid NOT NULL`, `tipo_cerca text (enum) NOT NULL`, `fios integer NOT NULL`, `espacamento_m numeric NOT NULL`, `perimetro_m numeric NOT NULL`, `cantos integer NOT NULL`, `metros_arame numeric NOT NULL`, `rolos integer NOT NULL`, `mouroes integer NOT NULL`, `esticadores integer NOT NULL`, `preco_rolo numeric`, `preco_mourao numeric`, `preco_esticador numeric`, `custo numeric`, `desatualizado boolean NOT NULL`
- Sync Stream: `calculos_cerca`
- Changeset: db/changelog/changes/20261112012941-auto.yaml

## SC-037 — modelos_cultura
- Motivo: Modelo de cultura criado na fazenda.
- Colunas, além das de `BaseFazendaEntity`: `nome text NOT NULL`, `taxa_kg_ha numeric`, `saco_kg numeric NOT NULL`, `etapas text NOT NULL`
- Sync Stream: `modelos_cultura`
- Changeset: db/changelog/changes/20261112012941-auto.yaml

## SC-038 — ciclos_plantio
- Motivo: Ciclo de plantio de uma lavoura.
- Colunas, além das de `BaseFazendaEntity`: `area_id uuid NOT NULL`, `cultura text NOT NULL`, `modelo_id text`, `data_plantio timestamptz NOT NULL`, `area_ha numeric NOT NULL`, `taxa_kg_ha numeric NOT NULL`, `saco_kg numeric NOT NULL`, `sementes_kg numeric NOT NULL`, `sacos integer NOT NULL`, `calculo_desatualizado boolean NOT NULL`, `status text (enum) NOT NULL`, `encerrado_em timestamptz`
- Sync Stream: `ciclos_plantio`
- Changeset: db/changelog/changes/20261112012941-auto.yaml

## SC-039 — etapas_ciclo
- Motivo: Etapa da linha do tempo do ciclo; com responsável, vira tarefa.
- Colunas, além das de `BaseFazendaEntity`: `ciclo_id uuid NOT NULL`, `ordem integer NOT NULL`, `nome text NOT NULL`, `tipo text (enum) NOT NULL`, `prevista_em timestamptz NOT NULL`, `realizada_em timestamptz`, `observacao text`, `responsavel_id uuid`, `responsavel_nome text`, `atribuida_em timestamptz`, `atribuida_por uuid`
- Sync Stream: `etapas_ciclo`
- Changeset: db/changelog/changes/20261112012941-auto.yaml

## SC-040 — usos_area
- Motivo: Tempo e km de um uso de máquina numa área.
- Colunas, além das de `BaseFazendaEntity`: `uso_id uuid NOT NULL`, `area_id uuid NOT NULL`, `inicio timestamptz NOT NULL`, `segundos integer NOT NULL`, `km numeric NOT NULL`
- Sync Stream: `usos_area`
- Changeset: db/changelog/changes/20261112012941-auto.yaml

## SC-041 — colunas de texto como text
- Motivo: o contrato com o app usa `text` para todo texto; as tabelas da primeira leva tinham `varchar(255)`
- Colunas: `animais.nome`, `auditoria_fazenda.acao`, `convites.token_hash`, `fazenda_membros.papel`, `fazendas.nome`,
  `sessoes.device_id`, `sessoes.refresh_hash`, `sessoes.refresh_hash_anterior`, `sync_rejeicoes.motivo`,
  `sync_rejeicoes.op`, `sync_rejeicoes.registro_id`, `sync_rejeicoes.tabela`, `usuarios.avatar_url`,
  `usuarios.credencial_hash`, `usuarios.email`, `usuarios.nome`, `usuarios.telefone`
- Changeset: db/changelog/changes/20261112013000-colunas-de-texto.yaml (escrito à mão: o `liquibase:diff` não
  detecta troca de tipo de coluna que já existe)

## SC-042 — tarefas
- Motivo: Tabela única para ocorrências e tarefas, sincronizada por fazenda.
- Colunas, além das de `BaseFazendaEntity`: `titulo text`, `categoria text (enum)`, `origem text (enum)`, `descricao text`, `urgente boolean`, `status text (enum)`, `responsavel_id uuid`, `prazo timestamptz`, `area_id uuid`, `animal_id uuid`, `maquina_id uuid`, `maquina_parada boolean`, `audio_uri text`, `ponto_lat numeric`, `ponto_lng numeric`, `sync_status text (enum)`, `concluida_em timestamptz`
- Sync Stream: `tarefas`
- Changeset: db/changelog/changes/20261112024500-tarefas-e-eventos.yaml

## SC-043 — tarefa_eventos
- Motivo: Histórico append-only de cada tarefa.
- Colunas, além das de `BaseFazendaEntity`: `tarefa_id uuid`, `tipo text (enum)`, `autor_id uuid`, `ocorrido_em timestamptz`, `texto text`, `dados jsonb`
- Sync Stream: `tarefa_eventos`
- Changeset: db/changelog/changes/20261112024500-tarefas-e-eventos.yaml

## SC-044 — colunas com sufixo de unidade
- Motivo: o `CamelCaseToUnderscoresNamingStrategy` não separa a última letra maiúscula (`espacamentoM` virou `espacamentom`). O app manda `espacamento_m`; o sync ignorava a coluna e, nas NOT NULL, recusava o registro inteiro (cercas e leituras de horímetro sumiam do celular).
- Colunas: `calculos_cerca.espacamento_m`, `calculos_cerca.perimetro_m`, `talhoes.perimetro_m`, `maquinas.largura_trabalho_m`, `abastecimentos.horimetro_h`, `leituras_horimetro.valor_h`, `leituras_horimetro.estimado_h`, `animais.producao_leite_l`
- Entities fixam o nome com `@Column(name = ...)`.
- Changeset: db/changelog/changes/20261112120000-corrige-colunas-do-sync.yaml

## SC-045 — tarefa_eventos.dados como text
- Motivo: igual às outras colunas JSON do sync (o app grava e lê JSON em texto); o contrato Drizzle × Postgres acusava jsonb × text.
- Changeset: db/changelog/changes/20261112120000-corrige-colunas-do-sync.yaml

## SC-046 — reparo de usos descartados
- Motivo: uso ligado sem querer era apagado sem `fim`; telas que olhavam só `fim IS NULL` mostravam a máquina em uso para sempre. O app agora grava `fim` junto com `deleted_at`.
- Changeset: db/changelog/changes/20261112120000-corrige-colunas-do-sync.yaml

## SC-047 — bucket midias
- Motivo: fotos e áudios ficavam só no celular (a coluna guardava `file://`). Agora sobem para o Storage pela API (`/fazendas/{id}/midias`).
- Privado; leitura por URL assinada de 60 min. Pastas: `{fazendaId}/imagens|audios|documentos/{id}.{ext}`.
- Changeset: db/changelog/changes/20261112130000-bucket-midias.yaml (pulado fora do Supabase)

## SC-048 — SELECT do PowerSync
- Motivo: o GRANT original não cobria tabelas criadas depois; `tarefas` e `tarefa_eventos` davam permission denied no PowerSync e nunca desciam.
- Changeset: db/changelog/changes/zzz-grant-powersync.yaml (runAlways)
