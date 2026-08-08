package br.com.gamecursos.util.table;

import javax.swing.*;

public class EditorCelula extends DefaultCellEditor {

	private static final long serialVersionUID = 1L;

	public EditorCelula() {
		super(new JTextField());
	}

	public boolean stopCellEditing() {
		if (getCellEditorValue() != null) {
			fireEditingStopped();
			return true;
		}
		return false;
	}

}
