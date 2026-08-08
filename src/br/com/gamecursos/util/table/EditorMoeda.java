package br.com.gamecursos.util.table;

import java.text.DecimalFormat;
import java.text.ParseException;
import javax.swing.JTextField;

public class EditorMoeda extends EditorCelula {

	private static final long serialVersionUID = 1L;
	private static final DecimalFormat moeda = new DecimalFormat("#,##0.00");

	public Object getCellEditorValue() {
		try {
			String str = ((JTextField) getComponent()).getText();
			return moeda.parse(str).doubleValue();
		} catch (ParseException e) {
			return null;
		}
	}

}
