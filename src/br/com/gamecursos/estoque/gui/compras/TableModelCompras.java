package br.com.gamecursos.estoque.gui.compras;

import java.text.DateFormat;
import java.text.DecimalFormat;

import br.com.gamecursos.estoque.model.Compra;
import br.com.gamecursos.swingcrud.TableModel;

public class TableModelCompras extends TableModel<Compra> {

	private static final long serialVersionUID = 1L;
	
	private static DateFormat data = DateFormat.getDateInstance();
	private static DecimalFormat codigo = new DecimalFormat("00000");
	private static DecimalFormat moeda = new DecimalFormat("#,##0.00"); 

	@Override
	public String[] getColunas() {
		return new String[] {"Código", "Data", "Nota", "Cliente", "Total"};
	}

	@Override
	public Object getDadoColuna(int coluna, Compra c) {
		String dt = data.format(c.getData().getTime());
		String cod = codigo.format(c.getId());
		String total = moeda.format(c.getTotal());
		
		switch (coluna) {
			case 0: return cod;
			case 1: return dt;
			case 2: return c.getNota();
			case 3: return c.getFornecedor().getNome();
			case 4: return total;
		};
		
		return null;
	}

}
