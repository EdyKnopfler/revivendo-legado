package br.com.gamecursos.estoque.gui.produtos;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.ParseException;

import br.com.gamecursos.estoque.dao.EstoqueDao;
import br.com.gamecursos.estoque.dao.FornecedorDao;
import br.com.gamecursos.estoque.gui.fornecedores.PopupPesquisaFornecedores;
import br.com.gamecursos.estoque.model.Fornecedor;
import br.com.gamecursos.estoque.model.Produto;
import br.com.gamecursos.swingcrud.CRUDException;
import br.com.gamecursos.swingcrud.PainelCampos;
import br.com.gamecursos.util.JTextFieldLimit;
import br.com.gamecursos.util.pesquisa.AcaoSelecao;
import br.com.gamecursos.util.pesquisa.CampoPesquisa;

public class PainelProduto extends PainelCampos<Produto> {

	private static final long serialVersionUID = 1L;
	private static DecimalFormat moeda = new DecimalFormat("#,##0.00");
	
	private EstoqueDao estoqueDao;
	
	private Fornecedor fornecedorSelecionado;
	private Produto produtoSendoAlterado;
	
	private JTextField fornecedor;
	private JLabel codigoFornecedor;
	private JTextField codigo;
	private JTextField nome;
	private JTextField custoUnitario;
	private JTextField quantidade;
	private JTextField valorTotal;
	
	public PainelProduto(Connection connection) {
		estoqueDao = new EstoqueDao(connection);
		PopupPesquisaFornecedores popup = new PopupPesquisaFornecedores( 
				new SelecaoFornecedor(), new FornecedorDao(connection));
				
		fornecedor = new CampoPesquisa<Fornecedor>(popup);
		codigoFornecedor = new JLabel();
		codigo = new JTextField(10);
		nome = new JTextField(40);
		custoUnitario = new JTextField(10);
		quantidade = new JTextField(10);
		valorTotal = new JTextField(10);
		
		codigo.addKeyListener(new DigitacaoCodigo());
		codigo.setDocument(new JTextFieldLimit(10));
		nome.setDocument(new JTextFieldLimit(80));
		custoUnitario.getDocument().addDocumentListener(new EdicaoCustoUnitario());
		quantidade.setEditable(false);
		valorTotal.setEditable(false);
		
		// Layout
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 5, 5, 5);
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(new JLabel("Fornecedor:"), gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 3;
		add(fornecedor, gbc);
		gbc.gridx = 3;
		gbc.gridwidth = 1;
		add(codigoFornecedor, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		add(new JLabel("Código:"), gbc);
		gbc.gridy = 3;
		add(codigo, gbc);

		gbc.gridx = 0;
		gbc.gridy = 4;
		add(new JLabel("Nome:"), gbc);
		gbc.gridy = 5;
		gbc.gridwidth = 3;
		add(nome, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 6;
		gbc.gridwidth = 1;
		add(new JLabel("Custo unit.:"), gbc);
		gbc.gridx = 1;
		add(new JLabel("Quantidade:"), gbc);
		gbc.gridx = 2;
		add(new JLabel("Valor total:"), gbc);

		gbc.gridx = 0;
		gbc.gridy = 7;
		add(custoUnitario, gbc);
		gbc.gridx = 1;
		add(quantidade, gbc);
		gbc.gridx = 2;
		add(valorTotal, gbc);
	}

	@Override
	public void exibir(Produto p) {
		fornecedorSelecionado = p.getFornecedor();
		codigoFornecedor.setText(String.valueOf(p.getFornecedor().getId()));
		fornecedor.setText(p.getFornecedor().getNome());
		codigo.setText(p.getCodigo());
		nome.setText(p.getNome());
		custoUnitario.setText(moeda.format(p.getCustoUnitario()));
		
		produtoSendoAlterado = p;
	}

	@Override
	public Produto novoObjeto() throws CRUDException {
		try {
			Produto p = new Produto();
			
			if (fornecedorSelecionado == null) {
				fornecedor.requestFocus();
				throw new CRUDException("Selecione um fornecedor!");
			}
			
			if (codigo.getText().trim().equals("")) {
				codigo.requestFocus();
				throw new CRUDException("Informe um código!");
			}
			
			p.setFornecedor(fornecedorSelecionado);
			p.setCodigo(codigo.getText());
			p.setNome(nome.getText());
			p.setCustoUnitario(moeda.parse(custoUnitario.getText()).doubleValue());
				
			return p;
		}
		catch (ParseException e) {
			custoUnitario.requestFocus();
			throw new CRUDException("Valor inválido: " + custoUnitario.getText());
		}
	}

	@Override
	public Produto objetoSendoAlterado() throws CRUDException {
		Produto p = novoObjeto();
		p.setId(produtoSendoAlterado.getId());
		return p;
	}

	@Override
	public void limpar() {
		fornecedor.setText("");
		codigoFornecedor.setText("");
		codigo.setText("");
		nome.setText("");
		custoUnitario.setText("");
		quantidade.setText("");
		valorTotal.setText("");
		
		fornecedorSelecionado = null;
		produtoSendoAlterado = null;
	}

	@Override
	public void habilitarCampos(boolean habilitar) {
		fornecedor.setEditable(habilitar);
		codigo.setEditable(habilitar);
		nome.setEditable(habilitar);
		custoUnitario.setEditable(habilitar);
	}
	
	public void focoEdicao() {
		fornecedor.requestFocus();
	}
	
	void estoqueEValor() {
		if (produtoSendoAlterado == null) {
			quantidade.setText("");
			valorTotal.setText("");
			return;
		}
		
		try {
			int estoque = estoqueDao.quantosTem(produtoSendoAlterado);
			double custo = moeda.parse(custoUnitario.getText()).doubleValue();
			double valor = estoque * custo;
			quantidade.setText(String.valueOf(estoque));
			valorTotal.setText(moeda.format(valor));
		}
		catch (ParseException e) {
			valorTotal.setText("");
		}
	}
	
	private class SelecaoFornecedor implements AcaoSelecao<Fornecedor> {
		@Override
		public void selecionou(Fornecedor f) {
			fornecedor.setText(f.getNome());
			codigoFornecedor.setText(String.valueOf(f.getId()));
			fornecedorSelecionado = f;
		}
	}
	
	private class DigitacaoCodigo extends KeyAdapter {
		@Override
		public void keyTyped(KeyEvent e) {
			e.setKeyChar(Character.toUpperCase(e.getKeyChar()));
		}
	}
	
	private class EdicaoCustoUnitario implements DocumentListener {
		@Override
		public void changedUpdate(DocumentEvent e) {
			estoqueEValor();
		}
		@Override
		public void insertUpdate(DocumentEvent e) {
			estoqueEValor();
		}
		@Override
		public void removeUpdate(DocumentEvent e) {
			estoqueEValor();
		}
	}

}
