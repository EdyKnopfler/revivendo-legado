package br.com.gamecursos.swingcrud;

import javax.swing.JPanel;

public abstract class PainelCampos<T> extends JPanel {

	private static final long serialVersionUID = 1L;
	
	public abstract void exibir(T objeto);
	public abstract T novoObjeto() throws CRUDException;
	public abstract T objetoSendoAlterado() throws CRUDException;
	public abstract void limpar();
	public abstract void habilitarCampos(boolean habilitar);

}
