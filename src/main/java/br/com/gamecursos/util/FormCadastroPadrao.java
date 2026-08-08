package br.com.gamecursos.util;

import static javax.swing.JOptionPane.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.*;

import br.com.gamecursos.estoque.Aplicacao;
import br.com.gamecursos.swingcrud.*;

public abstract class FormCadastroPadrao<T> extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private TableModel<T> tableModel;
	private PainelCampos<T> painelCampos;
	private CRUDListener<T> crudListener;
	private ControladorCRUD<T> controlador;
	private JPanel painelPesquisa;
	private JTabbedPane abas;
	private JPanel painelBotoes;
	
	public FormCadastroPadrao(TableModel<T> tableModel, PainelCampos<T> painelCampos) {
		this.tableModel = tableModel;
		this.painelCampos = painelCampos;
		this.crudListener = criaCrudListener();
		controlador = new ControladorPadrao<T>(tableModel, painelCampos, crudListener);
		
		JTable tabela = controlador.getTabela();
		JButton incluir = controlador.getIncluir();
		JButton alterar = controlador.getAlterar();
		JButton gravar = controlador.getGravar();
		JButton cancelar = controlador.getCancelar();
		JButton excluir = controlador.getExcluir();
		JButton fechar = new JButton("Fechar");
		fechar.addActionListener(new BotaoFechar());
		
		Dimension d = new Dimension(130, 40);
		incluir.setPreferredSize(d);
		alterar.setPreferredSize(d);
		gravar.setPreferredSize(d);
		cancelar.setPreferredSize(d);
		excluir.setPreferredSize(d);
		fechar.setPreferredSize(d);
		
		URL urlIncluir = Aplicacao.class.getResource("/incluir.png");		
		URL urlAlterar = Aplicacao.class.getResource("/alterar.png");		
		URL urlGravar = Aplicacao.class.getResource("/gravar.png");		
		URL urlCancelar = Aplicacao.class.getResource("/cancelar.png");		
		URL urlExcluir = Aplicacao.class.getResource("/excluir.png");		
		URL urlFechar = Aplicacao.class.getResource("/fechar.png");		
		ImageIcon imgIncluir = new ImageIcon(urlIncluir);
		ImageIcon imgAlterar = new ImageIcon(urlAlterar);
		ImageIcon imgGravar = new ImageIcon(urlGravar);
		ImageIcon imgCancelar = new ImageIcon(urlCancelar);
		ImageIcon imgExcluir = new ImageIcon(urlExcluir);
		ImageIcon imgFechar= new ImageIcon(urlFechar);
		incluir.setIcon(imgIncluir);
		alterar.setIcon(imgAlterar);
		gravar.setIcon(imgGravar);
		cancelar.setIcon(imgCancelar);
		excluir.setIcon(imgExcluir);
		fechar.setIcon(imgFechar);
		
		painelPesquisa = new JPanel();
		painelBotoes = new JPanel();
		painelBotoes.add(incluir);
		painelBotoes.add(alterar);
		painelBotoes.add(gravar);
		painelBotoes.add(cancelar);
		painelBotoes.add(excluir);
		painelBotoes.add(fechar);

		JScrollPane scroll = new JScrollPane(tabela);
		scroll.getViewport().setBackground(Color.WHITE);
		
		JPanel painelListagem = new JPanel();
		painelListagem.setLayout(new BoxLayout(painelListagem, BoxLayout.Y_AXIS));
		painelListagem.add(painelPesquisa);
		painelListagem.add(scroll);
		
		abas = new JTabbedPane();
		abas.addTab("Listagem", painelListagem);
		abas.addTab("Dados", painelCampos);
		abas.addChangeListener(new MudancaAba());

		setLayout(new BorderLayout());
		add(abas, BorderLayout.CENTER);
		add(painelBotoes, BorderLayout.SOUTH);
		
		setSize(850, 600);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new EventosJanela());
	}
	
	public abstract CRUDListener<T> criaCrudListener();
	
	private void fechar() {
		ControladorCRUD<T> controlador = getControlador();
		try {
			if (controlador.getEstado() != ControladorCRUD.Estado.NAVEGANDO)
				controlador.disparaGravar();
			
			tableModel.setListaObjetos(new ArrayList<T>());
			dispose();
		}
		catch (CRUDException crex) {
			showMessageDialog(null, crex.getMessage(), crex.getTitulo(), WARNING_MESSAGE);
		}
		catch (Exception ex) {
			showMessageDialog(null, ex.getMessage(), "ERRO AO GRAVAR", ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}

	protected JTabbedPane getAbas() {
		return abas;
	}

	protected JPanel getPainelPesquisa() {
		return painelPesquisa;
	}
	
	protected JPanel getPainelBotoes() {
		return painelBotoes;
	}

	protected ControladorCRUD<T> getControlador() {
		return controlador;
	}
	
	protected JTable getTabela() {
		return controlador.getTabela();
	}
	
	protected TableModel<T> getTableModel() {
		return tableModel;
	}
	
	protected PainelCampos<T> getPainelCampos() {
		return painelCampos;
	}

	protected void tamanhoColuna(int indice, int largura) {
		controlador.getTabela().getColumnModel().getColumn(indice).setPreferredWidth(largura);
	}
	
	@SuppressWarnings("hiding")
	private class ControladorPadrao<T> extends ControladorCRUD<T> {

		public ControladorPadrao(TableModel<T> tableModel, PainelCampos<T> painelCampos, 
								 CRUDListener<T> crudListener) {
			super(tableModel, painelCampos, crudListener);
		}
		
		@Override
		public void disparaIncluir() {
			abas.setSelectedIndex(1);
			super.disparaIncluir();
		}
		
		@Override
		public void disparaAlterar() {
			abas.setSelectedIndex(1);
			super.disparaAlterar();
		}
		
	}
	
	private class MudancaAba implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			if (abas.getSelectedIndex() == 0 && 
				controlador.getEstado() != ControladorCRUD.Estado.NAVEGANDO) {
				try {
					controlador.disparaGravar();
				}
				catch (CRUDException crex) {
					abas.setSelectedIndex(1);
					showMessageDialog(null, crex.getMessage(), crex.getTitulo(), WARNING_MESSAGE);
				}
				catch (Exception ex) {
					abas.setSelectedIndex(1);
					showMessageDialog(null, ex.getMessage(), "ERRO AO GRAVAR", ERROR_MESSAGE);
					ex.printStackTrace();
				}
			}
		}
	}

	private class EventosJanela extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			fechar();
		}
	}
	
	private class BotaoFechar implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			fechar();
		}
	}

}
