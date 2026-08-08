# Notas técnicas

Legado didático (curso GameCursos), não está em produção — manutenção
é exercício. Uso: [README.md](README.md). Aqui só o "porquê" não óbvio.

- **Charset da conexão é configurável** (`charset` em `pedidos.properties`,
  via a tela `Configurador`) — a aplicação não assume nenhum charset fixo,
  então roda tanto contra bancos novos (UTF8/`UNICODE_CI_AI`) quanto contra
  uma base legada existente (ex.: WIN1252/`WIN_PTBR`). `Metadados.sql`/
  `01-schema.sh` só criam banco novo em UTF8 — não servem pra recriar um
  schema legado do zero, só pra bancos novos.
- **Container roda como root**: qualquer bind mount onde o *servidor*
  escreve vira arquivo `root:root` ilegível no host, sem sudo.
- **Backup usa `FBStreamingBackupManager`**, não `FBBackupManager`: os
  bytes trafegam pro cliente, que grava local — evita o problema acima
  e permite apontar pra pasta sincronizada com nuvem. Achado inspecionando
  o jar com `javap`, não está documentado.
- **Senha do `pedidos.properties` é ofuscada** (`Configuracao.java`), não
  editável à mão — gere pela tela `Configurador`.
- **Schema roda como `sysdba`**, app roda com usuário sem privilégio —
  `docker/initdb/01-schema.sh` faz os `GRANT`s. Só roda de novo sozinho em
  volume vazio; mudança de schema com volume já populado precisa de `isql`
  manual ou `docker compose down -v`.
- **Dois bancos** (`estoque.fdb` dev, `estoque-test.fdb` teste): já
  aconteceu de `PreparaCadastros.esvaziar()` (`DELETE` sem filtro) apagar
  cadastro real feito à mão. Testes usam o segundo por padrão
  ([Conexao.java](src/test/java/br/com/gamecursos/estoque/test/Conexao.java)).

## Pendências conhecidas

- `PreparedStatement` vaza em `incluir`/`alterar` dos DAOs (falta `finally`).
- Só `PedidoDao`/`PedidoRep` têm teste; `Cliente`/`Produto`/`Compra`/`Fornecedor` não têm nenhum.
