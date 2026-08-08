package br.com.gamecursos.estoque.gui.produtos;

import static javax.swing.JOptionPane.*;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import br.com.gamecursos.estoque.dao.ConflitoConcorrenciaException;
import br.com.gamecursos.estoque.dao.ProdutoDao;
import br.com.gamecursos.estoque.model.Produto;
import br.com.gamecursos.swingcrud.CRUDAdapter;
import br.com.gamecursos.swingcrud.CRUDListener;
import br.com.gamecursos.util.FormCadastroPadrao;
import br.com.gamecursos.util.Operacao;
import br.com.gamecursos.util.TratamentoErros;

public class FormProdutos extends FormCadastroPadrao<Produto> {
	
	private static final long serialVersionUID = 1L;
	
	private ProdutoDao cadastro;
	private Connection connection;
	private JComboBox<String> pesquisarPor;
	private JTextField textoPesquisa;
	private JButton pesquisar;
	
	public FormProdutos(Connection connection) {
		super(new TableModelProdutos(), new PainelProduto(connection));
		
		this.connection = connection;
		cadastro = new ProdutoDao(connection);
		
		pesquisarPor = new JComboBox<String>(new String[] {"Código", "Nome"});
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
		
		setTitle("Produtos");
		tamanhoColuna(1, 600);
		
		getTableModel().setListaObjetos(cadastro.todos());
		getAbas().addChangeListener(new MudancaAbas());
	}
	
	@Override
	public CRUDListener<Produto> criaCrudListener() {
		return new ProdutosListener();
	}
	
	private class ProdutosListener extends CRUDAdapter<Produto> {
		
		@Override
		public void aposBotaoIncluir() {
			((PainelProduto) getPainelCampos()).focoEdicao();
		}
		
		@Override
		public void aposBotaoAlterar() {
			((PainelProduto) getPainelCampos()).focoEdicao();
		}
		
		@Override
		public void acaoGravarInclusao(final Produto p) throws Exception {
			TratamentoErros.executarTransacao(connection, new Operacao() {
				public void executar() throws ConflitoConcorrenciaException {
					cadastro.incluir(p);
				}
			});
		}

		@Override
		public void acaoGravarAlteracao(final Produto p) throws Exception {
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
		public boolean antesBotaoExcluir() {
			return (showConfirmDialog(null, "Tem certeza?", "Excluir", YES_NO_OPTION) == YES_OPTION);
		}
		
		@Override
		public void acaoExcluir(final Produto p) throws Exception {
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
			if (pesquisarPor.getSelectedIndex() == 0) {
				Produto p = cadastro.porCodigo(textoPesquisa.getText());
				List<Produto> lista = new ArrayList<Produto>(); 
				if (p != null) lista.add(p);
				getTableModel().setListaObjetos(lista);
			}
			else
				getTableModel().setListaObjetos(cadastro.porNome(textoPesquisa.getText()));
		}
	}
	
	private class MudancaAbas implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			if (getAbas().getSelectedIndex() == 1)
				((PainelProduto) getPainelCampos()).estoqueEValor();
		}
	}

}
