# Sistema de Estoque (GameCursos)

Aplicação desktop Java Swing para controle de estoque (clientes, fornecedores,
produtos, compras, pedidos), com persistência em Firebird.

Detalhes técnicos e decisões de arquitetura: [CLAUDE.md](CLAUDE.md).

## Pré-requisitos

- JDK 17+
- Docker + Docker Compose

Maven não precisa estar instalado — use o wrapper (`./mvnw`).

## 1. Subir o banco de dados

```bash
cp .env.example .env
# edite o .env e troque as senhas de exemplo
docker compose up -d
```

## 2. Buildar

```bash
./mvnw clean package
```

Gera `target/estoque-<versão>.jar`, jar único pronto pra `java -jar`.

## 3. Configurar a conexão

```bash
./mvnw exec:java -Dexec.mainClass=br.com.gamecursos.bd.Configurador
```

Preencha com os dados do `.env` e salve como `pedidos.properties` na raiz
do projeto:

| Campo | Valor |
|---|---|
| IP | `localhost` |
| Arquivo | `/firebird/data/estoque.fdb` |
| Usuário | `FIREBIRD_USER` do `.env` |
| Senha | `FIREBIRD_PASSWORD` do `.env` |

## 4. Rodar a aplicação

```bash
./mvnw exec:java
# ou: java -jar target/estoque-1.0.0.jar
```

Também roda direto numa IDE (Eclipse, IntelliJ, VS Code). No VS Code já tem
launchers prontos em [.vscode/launch.json](.vscode/launch.json).

## 5. Rodar os testes

```bash
set -a; source .env; set +a
./mvnw test
```

## Estrutura

```
src/main/java/        código da aplicação
src/main/resources/   ícones e templates de relatório (.rel)
src/test/java/        testes de integração (JUnit 4)
docker-compose.yml     Firebird local
docker/initdb/         schema + grants no primeiro start do container
Metadados.sql          schema do banco
```
