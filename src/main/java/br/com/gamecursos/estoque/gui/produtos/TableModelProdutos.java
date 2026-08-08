package br.com.gamecursos.estoque.gui.produtos;

import br.com.gamecursos.estoque.model.Produto;
import br.com.gamecursos.swingcrud.TableModel;

public class TableModelProdutos extends TableModel<Produto> {

	private static final long serialVersionUID = 1L;
	
	@Override
	public String[] getColunas() {
		return new String[] {"Código", "Nome"};
	}

	@Override
	public Object getDadoColuna(int coluna, Produto p) {
		switch (coluna) {
			case 0: return p.getCodigo();
			case 1: return p.getNome();
		}
		return null;
	}

}
