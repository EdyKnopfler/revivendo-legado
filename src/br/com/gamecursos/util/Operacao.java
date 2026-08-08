package br.com.gamecursos.util;

import br.com.gamecursos.estoque.dao.ConflitoConcorrenciaException;

public interface Operacao {
	void executar() throws ConflitoConcorrenciaException;
}
