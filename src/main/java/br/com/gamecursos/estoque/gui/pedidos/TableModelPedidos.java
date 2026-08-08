package br.com.gamecursos.estoque.gui.pedidos;

import java.text.DateFormat;
import java.text.DecimalFormat;

import br.com.gamecursos.estoque.model.Pedido;
import br.com.gamecursos.swingcrud.TableModel;

public class TableModelPedidos extends TableModel<Pedido> {

	private static final long serialVersionUID = 1L;
	
	private static DateFormat data = DateFormat.getDateInstance();
	private static DecimalFormat codigo = new DecimalFormat("00000");
	private static DecimalFormat moeda = new DecimalFormat("#,##0.00"); 

	@Override
	public String[] getColunas() {
		return new String[] {"Código", "Data", "Cliente", "Total"};
	}

	@Override
	public Object getDadoColuna(int coluna, Pedido pedido) {
		String dt = data.format(pedido.getData().getTime());
		String cod = codigo.format(pedido.getId());
		String total = moeda.format(pedido.getTotal());
		
		switch (coluna) {
			case 0: return cod;
			case 1: return dt;
			case 2: return pedido.getCliente().getNome();
			case 3: return total;
		};
		
		return null;
	}

}
