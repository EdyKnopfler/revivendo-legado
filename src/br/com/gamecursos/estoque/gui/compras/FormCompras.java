package br.com.gamecursos.estoque.gui.compras;

import static javax.swing.JOptionPane.*;

import java.sql.Connection;
import java.text.DateFormat;
import java.util.Date;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;

import java.text.ParseException;

import br.com.gamecursos.estoque.dao.ConflitoConcorrenciaException;
import br.com.gamecursos.estoque.dao.EstoqueDao;
import br.com.gamecursos.estoque.dao.CompraDao;
import br.com.gamecursos.estoque.model.Compra;
import br.com.gamecursos.estoque.repo.CompraRep;
import br.com.gamecursos.swingcrud.CRUDAdapter;
import br.com.gamecursos.swingcrud.CRUDListener;
import br.com.gamecursos.swingcrud.ControladorCRUD;
import br.com.gamecursos.util.FormCadastroPadrao;
import br.com.gamecursos.util.Operacao;
import br.com.gamecursos.util.TratamentoErros;

public class FormCompras extends FormCadastroPadrao<Compra> {

	private static final long serialVersionUID = 1L;
	
	private CompraRep cadastro;
	private Connection connection;
	private JComboBox<String> pesquisarPor;
	private JTextField textoPesquisa;
	private JButton pesquisar;
	
	public FormCompras(Connection connection) {
		super(new TableModelCompras(), new PainelCompras(connection));
		
		this.connection = connection;
		EstoqueDao estoqueDao = new EstoqueDao(connection);
		CompraDao CompraDao = new CompraDao(connection);
		this.cadastro = new CompraRep(CompraDao, estoqueDao);
		
		setTitle("Compras");
		
		tamanhoColuna(3, 450);

		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(SwingConstants.RIGHT);
		getControlador().getTabela().getColumnModel().getColumn(4).setCellRenderer(renderer);
		
		pesquisarPor = new JComboBox<String>(new String[] {"Data", "Cliente"});
		textoPesquisa = new JTextField(30);
		pesquisar = new JButton("Pesquisar");
		JPanel pesquisa = getPainelPesquisa();
		pesquisa.add(new JLabel("Procurar por:"));
		pesquisa.add(pesquisarPor);
		pesquisa.add(textoPesquisa);
		pesquisa.add(pesquisar);
		AcaoPesquisa acaoPesq = new AcaoPesquisa();
		textoPesquisa.addActionListener(acaoPesq);
		pesquisar.addActionListener(acaoPesq);
		
		getTableModel().setListaObjetos(cadastro.todos());
		getAbas().addChangeListener(new MudancaAba());
	}

	@Override
	public CRUDListener<Compra> criaCrudListener() {
		return new ComprasListener();
	}

	private class ComprasListener extends CRUDAdapter<Compra> {

		@Override
		public void aposBotaoIncluir() {
			((PainelCompras) getPainelCampos()).hoje();
			((PainelCompras) getPainelCampos()).focoEdicao();
		}

		@Override
		public void aposBotaoAlterar() {
			((PainelCompras) getPainelCampos()).focoEdicao();
		}

		@Override
		public void acaoGravarInclusao(final Compra p) throws Exception {
			TratamentoErros.executarTransacao(connection, new Operacao() {
				public void executar() throws ConflitoConcorrenciaException {
					cadastro.incluir(p);
				}
			});
		}

		@Override
		public void acaoGravarAlteracao(final Compra p) throws Exception {
			TratamentoErros.executarTransacao(connection, new Operacao() {
				public void executar() throws ConflitoConcorrenciaException {
					cadastro.alterar(p);
				}
			});
		}

		@Override
		public void aposBotaoGravar() {
			getControlador().getAlterar().requestFocus();
		}
		
		@Override
		public boolean antesBotaoCancelar() {
			((PainelCompras) getPainelCampos()).cancelaEdicao();
			return true;
		}
		
		@Override
		public void aposBotaoCancelar() {
			// A exibição dos itens fica a cargo da mudança de aba, não do "exibir".
			// Logo, os originais não aparecem automaticamente ao se cancelar uma edição.
			int linha = getControlador().getTabela().getSelectedRow();
			if (linha == -1) return;
			Compra p = getControlador().getTableModel().get(linha);
			((PainelCompras) getPainelCampos()).carregaItens(p.getItens());
		}
		
		@Override
		public boolean antesBotaoExcluir() {
			return (showConfirmDialog(null, "Tem certeza?", "Excluir", YES_NO_OPTION) == YES_OPTION);
		}
		
		@Override
		public void acaoExcluir(final Compra p) throws Exception {
			TratamentoErros.executarTransacao(connection, new Operacao() {
				public void executar() throws ConflitoConcorrenciaException {
					cadastro.excluir(p);
				}
			});
		}
		
		@Override
		public void aposBotaoExcluir() {
			getControlador().getIncluir().requestFocus();
		}

	}
	
	private class AcaoPesquisa implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (pesquisarPor.getSelectedIndex() == 0) {
					Date dt = DateFormat.getDateInstance().parse(textoPesquisa.getText());
					getTableModel().setListaObjetos(cadastro.porData(dt));
				}
				else
					getTableModel().setListaObjetos(cadastro.porNome(textoPesquisa.getText()));
			}
			catch (ParseException ex) {
				showMessageDialog(null, "Data inválida: " + textoPesquisa.getText(), 
						"Atenção!", WARNING_MESSAGE);
			}
		}
	}
	
	private class MudancaAba implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			if (getAbas().getSelectedIndex() == 1 && 
					getControlador().getEstado() == ControladorCRUD.Estado.NAVEGANDO) {
				int linha = getControlador().getTabela().getSelectedRow();
				if (linha == -1) {
					((PainelCompras) getPainelCampos()).carregaItens(null);
				}
				else {
					Compra p = getTableModel().get(linha);
					((PainelCompras) getPainelCampos()).carregaItens(p.getItens());
				}
			}
		}
	}

}
