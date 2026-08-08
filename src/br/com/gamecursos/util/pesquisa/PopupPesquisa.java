package br.com.gamecursos.util.pesquisa;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.List;

public abstract class PopupPesquisa<T> extends JFrame {

	private static final long serialVersionUID = 1L;

	private JTextField campoPesquisa;
	private JList<T> lista;
	private AcaoSelecao<T> acaoSelecao;

	public PopupPesquisa(AcaoSelecao<T> acaoSelecao) {
		this.acaoSelecao = acaoSelecao;

		setSize(300, 300);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setUndecorated(true);

		campoPesquisa = new JTextField();
		lista = new JList<T>();
		campoPesquisa.getDocument().addDocumentListener(new EdicaoPesquisa());
		campoPesquisa.addKeyListener(new DigitacaoCampo());
		lista.addKeyListener(new DigitacaoLista());
		lista.addMouseListener(new DuploCliqueLista());

		setLayout(new BorderLayout());
		getContentPane().add(campoPesquisa, BorderLayout.NORTH);
		getContentPane().add(new JScrollPane(lista), BorderLayout.CENTER);

		addWindowListener(new SaidaPopup());
	}
	
	public void pesquisaTexto(String texto) {
		setVisible(true);
		campoPesquisa.requestFocus();
		campoPesquisa.setText(texto);
		digitou();
	}
	
	public abstract List<T> realizarPesquisa(String texto);
	
	private void digitou() {
		List<T> pesq = realizarPesquisa(campoPesquisa.getText());
		DefaultListModel<T> model = new DefaultListModel<T>();
		
		for (T obj: pesq)
			model.addElement(obj);
		
		lista.setModel(model);
		
		if (model.getSize() > 0)
			lista.getSelectionModel().setSelectionInterval(0, 0);
	}
	
	private void selecionou() {
		int pos = lista.getSelectionModel().getMaxSelectionIndex();
		if (pos == -1) return;
		T item = lista.getModel().getElementAt(pos);
		acaoSelecao.selecionou(item);
		dispose();
	}
	
	private class EdicaoPesquisa implements DocumentListener {
		@Override
		public void changedUpdate(DocumentEvent e) {
			digitou();
		}
		@Override
		public void insertUpdate(DocumentEvent e) {
			digitou();
		}
		@Override
		public void removeUpdate(DocumentEvent e) {
			digitou();
		}
	}
	
	private class DigitacaoCampo extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				int novo = lista.getSelectionModel().getMaxSelectionIndex() + 1;
				if (novo < lista.getModel().getSize())
					lista.getSelectionModel().setSelectionInterval(novo, novo);
			} 
			else if (e.getKeyCode() == KeyEvent.VK_UP) {
				int novo = lista.getSelectionModel().getMaxSelectionIndex() - 1;
				if (novo >= 0)
					lista.getSelectionModel().setSelectionInterval(novo, novo);
			} 
			else if (e.getKeyCode() == KeyEvent.VK_ENTER)
				selecionou();
			else if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
				dispose();
		}
	}

	private class DigitacaoLista extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER)
				selecionou();
			else if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
				dispose();
		}
	}

	private class DuploCliqueLista extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2)
				selecionou();
		}
	}

	private class SaidaPopup extends WindowAdapter {
		@Override
		public void windowDeactivated(WindowEvent e) {
			dispose();
		}
	}

}
