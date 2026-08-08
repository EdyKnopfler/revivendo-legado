package br.com.gamecursos.estoque.gui.itens;

import javax.swing.table.*;

import br.com.gamecursos.estoque.dao.ProdutoDao;
import br.com.gamecursos.estoque.model.Produto;

import java.util.List;
import java.util.ArrayList;
import java.text.DecimalFormat;

public class ItensTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private static final String[] nomesColunas = new String[] { "Código", "Descrição", "Preço Unit.",
			"Quant.","Total Item" };

	private static final DecimalFormat moeda = new DecimalFormat("#,##0.00");

	private List<ItemModel> lista = new ArrayList<ItemModel>();
	private ProdutoDao produtoDao;
	private boolean editando;

	public ItensTableModel(ProdutoDao produtoDao) {
		this.produtoDao = produtoDao;
		criarEmBranco();
	}

	public int getRowCount() {
		return lista.size();
	}

	public int getColumnCount() {
		return 5;
	}

	public Object getValueAt(int linha, int coluna) {
		ItemModel i = lista.get(linha);
		Produto p = i.getProduto();
		
		switch (coluna) {
		case 0:
			return p != null ? p.getCodigo() : null;
		case 1:
			return p != null ? p.getNome() : null;
		case 2:
			return moeda.format(i.getPrecoUnitario());
		case 3:
			return i.getQuantidade();
		case 4:
			return moeda.format(i.getValorPagar());
		}
		return null;
	}

	public String getColumnName(int coluna) {
		return nomesColunas[coluna];
	}

	public boolean isCellEditable(int linha, int coluna) {
		if (editando)
			return coluna != 1 && coluna != 4;
		return false;
	}

	public void setValueAt(Object valor, int linha, int coluna) {
		ItemModel i = lista.get(linha);

		switch (coluna) {
		case 0:
			Produto p = produtoDao.porCodigo((String) valor);
			i.setProduto(p);
			break;
		case 1:
			// Não editável
			break;
		case 2:
			i.setPrecoUnitario((Double) valor);
			break;
		case 3:
			i.setQuantidade((Integer) valor);
			break;
		}

		if (i.isEmBranco()) {
			i.setEmBranco(false);
			criarEmBranco();
		}

		fireTableRowsUpdated(linha, coluna);
	}

	public double getTotal() {
		double total = 0.00;
		
		for (ItemModel i: lista)
			total += i.getValorPagar();
		
		return total;
	}

	private void criarEmBranco() {
		int pos = lista.size();
		ItemModel novo = new ItemModel();
		novo.setEmBranco(true);
		lista.add(novo);
		fireTableRowsInserted(pos, pos);
	}
	
	public void setEditando(boolean editando) {
		this.editando = editando;
	}
	
	public boolean isEditando() {
		return editando;
	}

	public void setarItem(int linha, ItemModel model) {
		model.setEmBranco(false);
		ItemModel atual = lista.get(linha);
		if (atual.isEmBranco()) criarEmBranco();
		lista.set(linha, model);
		fireTableRowsUpdated(linha, linha);
	}
	
	public void setarLista(List<ItemModel> lista) {
		for (ItemModel i: lista) i.setEmBranco(false);
		this.lista = lista;
		criarEmBranco();
		fireTableDataChanged();
	}

	public List<ItemModel> getListaItens() {
		List<ItemModel> itens = new ArrayList<ItemModel>();
		
		for (ItemModel i: lista) {
			if (!i.isEmBranco())
				itens.add(i);
		}
		
		return itens;
	}

	public void excluirItem(int linha) {
		lista.remove(linha);
		fireTableDataChanged();
	}

}
