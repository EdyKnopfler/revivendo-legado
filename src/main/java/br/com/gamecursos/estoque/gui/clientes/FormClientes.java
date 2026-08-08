package br.com.gamecursos.estoque.gui.clientes;

import static javax.swing.JOptionPane.*;

import java.awt.event.*;
import javax.swing.*;

import java.sql.Connection;

import br.com.gamecursos.estoque.dao.ClienteDao;
import br.com.gamecursos.estoque.dao.ConflitoConcorrenciaException;
import br.com.gamecursos.estoque.model.Cliente;
import br.com.gamecursos.swingcrud.CRUDAdapter;
import br.com.gamecursos.swingcrud.CRUDListener;
import br.com.gamecursos.util.FormCadastroPadrao;
import br.com.gamecursos.util.Operacao;
import br.com.gamecursos.util.TratamentoErros;

public class FormClientes extends FormCadastroPadrao<Cliente> {
	
	private static final long serialVersionUID = 1L;
	
	private ClienteDao cadastro;
	private Connection connection;
	private JTextField pesquisa;
	private JButton pesquisar;
	
	public FormClientes(Connection connection) {
		super(new TableModelClientes(), new PainelCliente());
		
		this.connection = connection;
		cadastro = new ClienteDao(connection);
		
		pesquisa = new JTextField(40);
		pesquisar = new JButton("Pesquisar");
		
		getPainelPesquisa().add(new JLabel("Procurar por:"));
		getPainelPesquisa().add(pesquisa);
		getPainelPesquisa().add(pesquisar);
		
		AcaoPesquisa acao = new AcaoPesquisa();
		pesquisa.addActionListener(acao);
		pesquisar.addActionListener(acao);
		
		setTitle("Clientes");
		tamanhoColuna(1, 600);
		
		getTableModel().setListaObjetos(cadastro.todos());
	}
	
	@Override
	public CRUDListener<Cliente> criaCrudListener() {
		return new ClientesListener();
	}
	
	private class ClientesListener extends CRUDAdapter<Cliente> {
		
		@Override
		public void aposBotaoIncluir() {
			((PainelCliente) getPainelCampos()).focoEdicao();
		}
		
		@Override
		public void aposBotaoAlterar() {
			((PainelCliente) getPainelCampos()).focoEdicao();
		}
		
		@Override
		public void acaoGravarInclusao(final Cliente c) throws Exception {
			TratamentoErros.executarTransacao(connection, new Operacao() {
				public void executar() throws ConflitoConcorrenciaException {
					cadastro.incluir(c);
				}
			});
		}

		@Override
		public void acaoGravarAlteracao(final Cliente c) throws Exception {
			TratamentoErros.executarTransacao(connection, new Operacao() {
				public void executar() throws ConflitoConcorrenciaException {
					cadastro.alterar(c);
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
		public void acaoExcluir(final Cliente c) throws Exception {
			TratamentoErros.executarTransacao(connection, new Operacao() {
				public void executar() throws ConflitoConcorrenciaException {
					cadastro.excluir(c);
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
