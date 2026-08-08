package br.com.gamecursos.estoque.gui.fornecedores;

import static javax.swing.JOptionPane.*;

import java.awt.event.*;
import javax.swing.*;

import java.sql.Connection;

import br.com.gamecursos.estoque.dao.ConflitoConcorrenciaException;
import br.com.gamecursos.estoque.dao.FornecedorDao;
import br.com.gamecursos.estoque.model.Fornecedor;
import br.com.gamecursos.swingcrud.CRUDAdapter;
import br.com.gamecursos.swingcrud.CRUDListener;
import br.com.gamecursos.util.FormCadastroPadrao;
import br.com.gamecursos.util.Operacao;
import br.com.gamecursos.util.TratamentoErros;

public class FormFornecedores extends FormCadastroPadrao<Fornecedor> {
	
	private static final long serialVersionUID = 1L;
	
	private FornecedorDao cadastro;
	private Connection connection;
	private JTextField pesquisa;
	private JButton pesquisar;
	
	public FormFornecedores(Connection connection) {
		super(new TableModelFornecedores(), new PainelFornecedor());
		
		this.connection = connection;
		cadastro = new FornecedorDao(connection);
		
		pesquisa = new JTextField(40);
		pesquisar = new JButton("Pesquisar");
		
		getPainelPesquisa().add(new JLabel("Procurar por:"));
		getPainelPesquisa().add(pesquisa);
		getPainelPesquisa().add(pesquisar);
		
		AcaoPesquisa acao = new AcaoPesquisa();
		pesquisa.addActionListener(acao);
		pesquisar.addActionListener(acao);
		
		setTitle("Fornecedores");
		tamanhoColuna(1, 600);
		
		getTableModel().setListaObjetos(cadastro.todos());
	}
	
	@Override
	public CRUDListener<Fornecedor> criaCrudListener() {
		return new FornecedoresListener();
	}
	
	private class FornecedoresListener extends CRUDAdapter<Fornecedor> {
		
		@Override
		public void aposBotaoIncluir() {
			((PainelFornecedor) getPainelCampos()).focoEdicao();
		}
		
		@Override
		public void aposBotaoAlterar() {
			((PainelFornecedor) getPainelCampos()).focoEdicao();
		}
		
		@Override
		public void acaoGravarInclusao(final Fornecedor f) throws Exception {
			TratamentoErros.executarTransacao(connection, new Operacao() {
				public void executar() throws ConflitoConcorrenciaException {
					cadastro.incluir(f);
				}
			});
		}

		@Override
		public void acaoGravarAlteracao(final Fornecedor f) throws Exception {
			TratamentoErros.executarTransacao(connection, new Operacao() {
				public void executar() throws ConflitoConcorrenciaException {
					cadastro.alterar(f);
				}
			});
		}
		
		@Override
		public void aposBotaoGravar() {
			getControlador().getAlterar().requestFocus();
		}
		
		@Override
		public boolean antesBotaoExcluir() {
			return (showConfirmDialog(null, "Tem certeza?", "Excluir", YES_NO_OPTION) == YES_OPTION);
		}
		
		@Override
		public void acaoExcluir(final Fornecedor f) throws Exception {
			TratamentoErros.executarTransacao(connection, new Operacao() {
				public void executar() throws ConflitoConcorrenciaException {
					cadastro.excluir(f);
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
			getTableModel().setListaObjetos(cadastro.porNome(pesquisa.getText()));
		}
	}

}
