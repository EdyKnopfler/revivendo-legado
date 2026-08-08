package br.com.gamecursos.util.pesquisa;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class CampoPesquisa<T> extends JTextField {

	private static final long serialVersionUID = 1L;
	
	private PopupPesquisa<T> popup;
	private T valor;
	
	public CampoPesquisa(PopupPesquisa<T> popup) {
		super(40);
		this.popup = popup;
		addKeyListener(new Digitacao());
	}
	
	public T getValor() {
		return valor;
	}
	
	public void setValor(T valor) {
		setText(valor.toString());
		this.valor = valor;
	}
	
	private void mostraPopup(String texto) {
		Point pos = getLocationOnScreen();
		popup.setLocation((int) pos.getX(), (int) pos.getY());
		popup.setSize(getWidth(), 200);
		popup.pesquisaTexto(texto);
	}
	
	private class Digitacao extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			int c = e.getKeyCode();
			if (c == KeyEvent.VK_DELETE || c == KeyEvent.VK_BACK_SPACE ||
				c == KeyEvent.VK_ESCAPE)
				e.consume();
		}
		@Override
		public void keyTyped(KeyEvent e) {
			char c = e.getKeyChar();
			
			if (c == KeyEvent.VK_DELETE || c == KeyEvent.VK_BACK_SPACE ||
				c == KeyEvent.VK_ESCAPE) {
				e.consume();
				return;
			}
			
			mostraPopup(Character.toString(c));
			e.consume();
		}
	}
	
}
