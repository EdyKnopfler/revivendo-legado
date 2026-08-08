#!/bin/sh
set -e

ISQL="${PREFIX}/bin/isql"
DB="${DBPATH}/${FIREBIRD_DATABASE}"

"${ISQL}" -user sysdba "${DB}" -i /docker-entrypoint-initdb.d/sql/Metadados.sql

if [ -n "${FIREBIRD_USER}" ]; then
  {
    for tbl in CLIENTES COMPRAS FORNECEDORES ITENS_COMPRA ITENS_PEDIDO PEDIDOS PRODUTOS; do
      echo "GRANT ALL ON ${tbl} TO ${FIREBIRD_USER};"
    done
    for gen in GER_CLIENTES GER_COMPRAS GER_CORRECOES GER_FORNECEDORES GER_ITENS_COMPRA GER_ITENS_PEDIDO GER_PEDIDOS GER_PRODUTOS; do
      echo "GRANT USAGE ON GENERATOR ${gen} TO ${FIREBIRD_USER};"
    done
    echo "COMMIT;"
  } | "${ISQL}" -user sysdba "${DB}"
fi
