package br.com.gamecursos.estoque.dao;

public class ConflitoConcorrenciaException extends Exception {

	private static final long serialVersionUID = 1L;

	public ConflitoConcorrenciaException(String msg, Throwable causa) {
		super(msg, causa);
	}

}
