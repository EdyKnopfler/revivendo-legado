package br.com.gamecursos.util;

import java.sql.Connection;
import java.sql.SQLException;

import br.com.gamecursos.estoque.dao.ConflitoConcorrenciaException;
import br.com.gamecursos.estoque.dao.DaoException;
import br.com.gamecursos.swingcrud.CRUDException;

public class TratamentoErros {

	private static final int MAX_TENTATIVAS = 3;
	private static final long BACKOFF_BASE_MS = 200;

	public static void executarTransacao(Connection connection, Operacao operacao) throws CRUDException {
		for (int tentativa = 1; ; tentativa++) {
			try {
				operacao.executar();
				connection.commit();
				return;
			}
			catch (ConflitoConcorrenciaException cce) {
				rollbackSilencioso(connection);

				if (tentativa >= MAX_TENTATIVAS)
					throw new CRUDException(
						"Duas operações concorrentes tentaram alterar o mesmo registro.\n" +
						"Tente novamente em instantes.",
						"Tente Novamente"
					);

				esperar(BACKOFF_BASE_MS * tentativa);
			}
			catch (SQLException e) {
				rollbackSilencioso(connection);
				throw new DaoException("Erro ao confirmar transação:\n\n" + e.getMessage(), e);
			}
			catch (RuntimeException e) {
				rollbackSilencioso(connection);
				throw e;
			}
		}
	}

	private static void esperar(long ms) {
		try {
			Thread.sleep(ms);
		}
		catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
		}
	}

	private static void rollbackSilencioso(Connection connection) {
		try {
			connection.rollback();
		}
		catch (SQLException se) {
			se.printStackTrace();
		}
	}

}
