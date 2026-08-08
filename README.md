# Sistema de Estoque (GameCursos)

Aplicação desktop Java Swing para controle de estoque (clientes, fornecedores,
produtos, compras, pedidos), com persistência em Firebird via DAO/repo e um
framework CRUD genérico próprio (`swingcrud`).

## Pré-requisitos

- JDK 17 ou superior
- Docker + Docker Compose (só para o banco de dados)

Maven **não** precisa estar instalado — o projeto já traz o Maven Wrapper
(`./mvnw`), que baixa e usa a versão certa sozinho.

## 1. Subir o banco de dados

```bash
cp .env.example .env
# edite o .env e troque as senhas de exemplo
docker compose up -d
```

Isso sobe um Firebird 5 e, **só na primeira vez** (quando o volume ainda não
tem banco), cria `estoque.fdb` em UTF8, aplica o schema de
[Metadados.sql](Metadados.sql) e concede as permissões necessárias ao usuário
de aplicação (`FIREBIRD_USER`/`FIREBIRD_PASSWORD` do `.env`) — o schema em si
é criado como `sysdba`, então sem esse `GRANT` a aplicação não conseguiria
nem inserir uma linha.

O volume nomeado `firebird-data` sobrevive a `docker compose down`. Só é
apagado com `docker compose down -v`.

## 2. Buildar

```bash
./mvnw clean package
```

Gera `target/estoque-<versão>.jar`, um jar único (Jaybird embutido via
`maven-shade-plugin`), pronto pra rodar com `java -jar`.

## 3. Configurar a conexão da aplicação

A aplicação lê `pedidos.properties` (no diretório de onde ela é executada).
A senha nesse arquivo fica com uma ofuscação própria do projeto — **não dá
pra editar esse campo à mão** (veja [pedidos.properties.example](pedidos.properties.example)).
Gere o arquivo de verdade com a tela de configuração:

```bash
./mvnw exec:java -Dexec.mainClass=br.com.gamecursos.bd.Configurador
```

Preencha:

| Campo | Valor |
|---|---|
| IP | `localhost` |
| Arquivo | `/firebird/data/estoque.fdb` (caminho **dentro do container**, não do seu disco) |
| Usuário | o mesmo `FIREBIRD_USER` do `.env` |
| Senha | o mesmo `FIREBIRD_PASSWORD` do `.env` |

Clique em "Salvar..." apontando para `pedidos.properties` na raiz do projeto.

## 4. Rodar a aplicação

```bash
./mvnw exec:java
# ou, depois de "mvn package":
java -jar target/estoque-1.0.0.jar
```

### Numa IDE

Como o projeto segue o layout padrão do Maven (`src/main/java`,
`src/main/resources`, `src/test/java`) e tem `pom.xml`, qualquer IDE Java
(Eclipse, IntelliJ, VS Code com o Extension Pack for Java) importa e roda
naturalmente — não precisa configurar classpath na mão.

No VS Code já tem dois launchers prontos em [.vscode/launch.json](.vscode/launch.json)
(aba Run and Debug):

- **Sistema de Estoque** — a aplicação principal
- **Configurar conexão com o banco** — a tela do passo 3

## 5. Backup e restauração

A tela de Backup (menu da aplicação) grava/lê o `.fbk` **localmente, pelo
cliente** — não no servidor. Ela usa o modo de streaming do driver JDBC
(`FBStreamingBackupManager`), então os bytes trafegam pela mesma conexão até
a aplicação, e é a aplicação (rodando no seu host, fora do Docker) que grava
o arquivo. Dá pra apontar direto para uma pasta sincronizada com nuvem
(Dropbox, Google Drive etc.) sem se preocupar com permissão de arquivo
dentro do container — o arquivo já nasce com o seu usuário.

O campo "Banco de dados" nessa tela continua sendo um caminho **dentro do
container** (o mesmo do `pedidos.properties`); só o campo "Backup" é local.

## 6. Rodar os testes automatizados

Os testes são de integração e batem no Firebird de verdade (não usam mock),
então o banco do passo 1 precisa estar no ar. Exportam as credenciais do
`.env`:

```bash
set -a; source .env; set +a
./mvnw test
```

`PedidosTest` roda um fluxo completo (inclusão, alteração com
adição/remoção de item, exclusão de pedido) e confere o saldo de estoque a
cada passo.

**Os testes rodam num banco separado (`estoque-test.fdb`), não no
`estoque.fdb` que você usa manualmente pela aplicação.** Isso existe porque
`PreparaCadastros.esvaziar()`, chamado ao final da suíte, faz `DELETE` sem
filtro nas tabelas de cadastro — se apontasse para o mesmo banco da
aplicação, apagaria qualquer dado real que você tivesse cadastrado na mão
(já aconteceu durante o desenvolvimento deste projeto). O container cria os
dois bancos (mesmo schema, mesmas permissões) já na primeira inicialização;
para apontar os testes para outro servidor/banco, use as variáveis de
ambiente `FIREBIRD_TEST_HOST`, `FIREBIRD_TEST_DATABASE`,
`FIREBIRD_TEST_USER` e `FIREBIRD_TEST_PASSWORD` (veja
[Conexao.java](src/test/java/br/com/gamecursos/estoque/test/Conexao.java)).

## Estrutura

```
src/main/java/        código da aplicação
src/main/resources/   ícones e templates de relatório (.rel), na raiz do
                       classpath porque o código carrega via getResource("/x")
src/test/java/        testes de integração (JUnit 4)
docker-compose.yml     Firebird local
docker/initdb/         schema + grants aplicados no primeiro start do container
Metadados.sql          schema do banco (fonte da verdade, também usado no init do Docker)
```
