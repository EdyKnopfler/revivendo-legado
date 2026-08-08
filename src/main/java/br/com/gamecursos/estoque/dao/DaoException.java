package br.com.gamecursos.estoque.dao;

import java.sql.SQLException;

public class DaoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	// 40001: SQLState padrão ANSI para falha de serialização (deadlock/lock conflict),
	// usado por Firebird/Jaybird e pela maioria dos SGBDs.
	private static final String SQLSTATE_CONFLITO_CONCORRENCIA = "40001";

	public DaoException(String msg) {
		super(msg);
	}

	public DaoException(String msg, Throwable causa) {
		super(msg, causa);
	}

	public static void relancar(String acao, SQLException causa) throws ConflitoConcorrenciaException {
		String msg = acao + ":\n\n" + causa.getMessage();

		if (SQLSTATE_CONFLITO_CONCORRENCIA.equals(causa.getSQLState()))
			throw new ConflitoConcorrenciaException(msg, causa);

		throw new DaoException(msg, causa);
	}

}
