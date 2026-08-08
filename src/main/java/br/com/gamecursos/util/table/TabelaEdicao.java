package br.com.gamecursos.util.table;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;

import java.awt.Component;
import java.util.EventObject;
import javax.swing.text.JTextComponent;
import java.awt.event.KeyEvent;

public class TabelaEdicao extends JTable {

	private static final long serialVersionUID = 1L;

	public TabelaEdicao(TableModel modelo) {
		super(modelo);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	public boolean editCellAt(int linha, int coluna, EventObject e) {
		boolean result = super.editCellAt(linha, coluna, e);
		final Component editor = getEditorComponent();
		if (editor == null || !(editor instanceof JTextComponent))
			return result;
		if (e instanceof KeyEvent)
			((JTextComponent) editor).selectAll();
		return result;
	}

}
