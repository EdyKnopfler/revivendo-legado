#!/bin/sh
set -e

ISQL="${PREFIX}/bin/isql"
MAIN_DB="${DBPATH}/${FIREBIRD_DATABASE}"
TEST_DB="${DBPATH}/estoque-test.fdb"

aplicar_schema_e_grants() {
  db="$1"
  "${ISQL}" -user sysdba "${db}" -i /docker-entrypoint-initdb.d/sql/Metadados.sql

  if [ -n "${FIREBIRD_USER}" ]; then
    {
      for tbl in CLIENTES COMPRAS FORNECEDORES ITENS_COMPRA ITENS_PEDIDO PEDIDOS PRODUTOS; do
        echo "GRANT ALL ON ${tbl} TO ${FIREBIRD_USER};"
      done
      for gen in GER_CLIENTES GER_COMPRAS GER_CORRECOES GER_FORNECEDORES GER_ITENS_COMPRA GER_ITENS_PEDIDO GER_PEDIDOS GER_PRODUTOS; do
        echo "GRANT USAGE ON GENERATOR ${gen} TO ${FIREBIRD_USER};"
      done
      echo "COMMIT;"
    } | "${ISQL}" -user sysdba "${db}"
  fi
}

aplicar_schema_e_grants "${MAIN_DB}"

# Banco separado para os testes automatizados (mvn test), para eles nunca
# encostarem nos dados usados manualmente no banco principal: PreparaCadastros
# esvazia as tabelas ao final da suite, o que apagaria cadastros reais se
# apontasse para o mesmo arquivo.
if [ ! -f "${TEST_DB}" ]; then
  echo "CREATE DATABASE '${TEST_DB}' USER 'sysdba' PASSWORD '${ISC_PASSWORD}' DEFAULT CHARACTER SET UTF8;" | "${ISQL}"
  aplicar_schema_e_grants "${TEST_DB}"
fi
