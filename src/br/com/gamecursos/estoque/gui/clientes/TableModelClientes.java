package br.com.gamecursos.estoque.gui.clientes;

import java.text.DecimalFormat;

import br.com.gamecursos.estoque.model.Cliente;
import br.com.gamecursos.swingcrud.TableModel;

public class TableModelClientes extends TableModel<Cliente> {

	private static final long serialVersionUID = 1L;
	private static DecimalFormat codigo = new DecimalFormat("00000");
	
	@Override
	public String[] getColunas() {
		return new String[] {"Código", "Nome"};
	}

	@Override
	public Object getDadoColuna(int coluna, Cliente c) {
		switch (coluna) {
			case 0: return codigo.format(c.getId());
			case 1: return c.getNome();
		}
		return null;
	}

}
