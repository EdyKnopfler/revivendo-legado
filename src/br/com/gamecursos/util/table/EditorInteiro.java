package br.com.gamecursos.util.table;

import javax.swing.JTextField;

public class EditorInteiro extends EditorCelula {

	private static final long serialVersionUID = 1L;

	public Object getCellEditorValue() {
		try {
			String str = ((JTextField) getComponent()).getText();
			return Integer.valueOf(str);
		} catch (NumberFormatException e) {
			return null;
		}
	}

}
