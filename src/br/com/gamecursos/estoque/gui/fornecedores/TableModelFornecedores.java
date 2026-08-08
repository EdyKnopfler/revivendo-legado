package br.com.gamecursos.estoque.gui.fornecedores;

import java.text.DecimalFormat;

import br.com.gamecursos.estoque.model.Fornecedor;
import br.com.gamecursos.swingcrud.TableModel;

public class TableModelFornecedores extends TableModel<Fornecedor> {

	private static final long serialVersionUID = 1L;
	private static DecimalFormat codigo = new DecimalFormat("00000");
	
	@Override
	public String[] getColunas() {
		return new String[] {"Código", "Nome"};
	}

	@Override
	public Object getDadoColuna(int coluna, Fornecedor f) {
		switch (coluna) {
			case 0: return codigo.format(f.getId());
			case 1: return f.getNome();
		}
		return null;
	}

}
