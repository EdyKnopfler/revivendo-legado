package br.com.gamecursos.estoque.gui.compras;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import java.sql.Connection;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.gamecursos.estoque.Aplicacao;
import br.com.gamecursos.estoque.dao.FornecedorDao;
import br.com.gamecursos.estoque.dao.ProdutoDao;
import br.com.gamecursos.estoque.gui.fornecedores.PopupPesquisaFornecedores;
import br.com.gamecursos.estoque.gui.itens.*;
import br.com.gamecursos.estoque.gui.produtos.PopupPesquisaProdutos;
import br.com.gamecursos.estoque.model.Fornecedor;
import br.com.gamecursos.estoque.model.ItemCompra;
import br.com.gamecursos.estoque.model.Compra;
import br.com.gamecursos.estoque.model.Produto;
import br.com.gamecursos.swingcrud.*;
import br.com.gamecursos.util.JTextFieldLimit;
import br.com.gamecursos.util.pesquisa.*;
import br.com.gamecursos.util.table.EditorCelula;
import br.com.gamecursos.util.table.EditorInteiro;
import br.com.gamecursos.util.table.EditorMoeda;
import br.com.gamecursos.util.table.TabelaEdicao;

import static javax.swing.JOptionPane.*;

public class PainelCompras extends PainelCampos<Compra> {

	private static final long serialVersionUID = 1L;
	private static DateFormat formatoData = DateFormat.getDateInstance();
	private static DecimalFormat moeda = new DecimalFormat("#,##0.00");
	
	private PopupPesquisaProdutos popupProdutos;
	private JTextField fornecedor;
	private JLabel codigoFornecedor;
	private JTextField data;
	private JTextField nota;
	private ItensTableModel modelo;
	private TabelaEdicao itens;
	private JButton incluir, excluir;
	private JTextField total;
	
	private Compra compraAtual;
	private Fornecedor fornecedorSelecionado;
	private boolean editando;
	
	
	public PainelCompras(Connection connection) {
		// Componentes
		PopupPesquisaFornecedores popup = new PopupPesquisaFornecedores( 
			new SelecaoFornecedor(), new FornecedorDao(connection));
		ProdutoDao produtoDao = new ProdutoDao(connection);
		
		popupProdutos = new PopupPesquisaProdutos(new SelecaoProduto(), produtoDao);
		fornecedor = new CampoPesquisa<Fornecedor>(popup);
		codigoFornecedor = new JLabel();
		data = new JTextField(10);
		nota = new JTextField(10);
		nota.setDocument(new JTextFieldLimit(10));
		
		modelo = new ItensTableModel(produtoDao);
		modelo.addTableModelListener(new AlteracaoTabela());
		itens = new TabelaEdicao(modelo);
		itens.setSurrendersFocusOnKeystroke(true);
		itens.addKeyListener(new DigitacaoTabela());
		itens.getSelectionModel().addListSelectionListener(new SelecaoTabela());
		itens.setPreferredScrollableViewportSize(new Dimension(700, 300));

		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(SwingConstants.RIGHT);
		itens.getColumnModel().getColumn(2).setCellRenderer(renderer);
		itens.getColumnModel().getColumn(3).setCellRenderer(renderer);
		itens.getColumnModel().getColumn(4).setCellRenderer(renderer);
		itens.getColumnModel().getColumn(1).setPreferredWidth(300);

		EditorCelula codigo = new EditorCelula();
		itens.getColumnModel().getColumn(0).setCellEditor(codigo);

		EditorMoeda precoUnit = new EditorMoeda();
		itens.getColumnModel().getColumn(2).setCellEditor(precoUnit);

		EditorInteiro quant = new EditorInteiro();
		itens.getColumnModel().getColumn(3).setCellEditor(quant);
		
		incluir = new JButton();
		excluir = new JButton();
		URL urlIncluir = Aplicacao.class.getResource("/incluir.png");		
		URL urlExcluir = Aplicacao.class.getResource("/excluir.png");		
		ImageIcon imgIncluir = new ImageIcon(urlIncluir);
		ImageIcon imgExcluir = new ImageIcon(urlExcluir);
		incluir.setIcon(imgIncluir);
		excluir.setIcon(imgExcluir);
		incluir.addActionListener(new AcaoIncluir());
		excluir.addActionListener(new AcaoExcluir());
		
		total = new JTextField(10);
		total.setHorizontalAlignment(JTextField.RIGHT);
		total.setEditable(false);
		Font f = new Font(total.getFont().getName(), Font.BOLD, total.getFont().getSize());
		total.setFont(f);
		
		// Layout
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 5, 5, 5);
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(new JLabel("Fornec.:"), gbc);
		gbc.gridy = 1;
		add(new JLabel("Data:"), gbc);
		gbc.gridy = 2;
		add(new JLabel("Nota:"), gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		add(fornecedor, gbc);
		gbc.gridy = 1;
		add(data, gbc);
		gbc.gridy = 2;
		add(nota, gbc);

		gbc.gridx = 2;
		gbc.gridy = 0;
		add(codigoFornecedor, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 3;
		JScrollPane scroll = new JScrollPane(itens);
		scroll.getViewport().setBackground(Color.WHITE);
		add(scroll, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 1;
		add(incluir, gbc);
		gbc.gridx = 1;
		add(excluir, gbc);
		gbc.gridx = 2;
		add(new JLabel("Total:"), gbc);
		
		gbc.gridx = 2;
		gbc.gridy = 4;
		gbc.anchor = GridBagConstraints.EAST;
		add(total, gbc);
	}

	@Override
	public void exibir(Compra c) {
		compraAtual = c;
		codigoFornecedor.setText(String.valueOf(c.getFornecedor().getId()));
		fornecedor.setText(c.getFornecedor().getNome());
		fornecedorSelecionado = c.getFornecedor();
		data.setText(formatoData.format(c.getData().getTime()));
		nota.setText(c.getNota());
	}

	@Override
	public Compra novoObjeto() throws CRUDException {
		Compra novo = new Compra();
		popula(novo);
		compraAtual = novo;
		return novo;
	}

	@Override
	public Compra objetoSendoAlterado() throws CRUDException {
		popula(compraAtual);
		return compraAtual;
	}

	@Override
	public void limpar() {
		fornecedorSelecionado = null;
		codigoFornecedor.setText("");
		fornecedor.setText("");
		data.setText("");
		nota.setText("");
		modelo.setarLista(new ArrayList<ItemModel>());
	}

	@Override
	public void habilitarCampos(boolean habilitar) {
		fornecedor.setEditable(habilitar);
		data.setEditable(habilitar);
		nota.setEditable(habilitar);
		modelo.setEditando(habilitar);
		editando = habilitar;
		habilitaBotoes();
	}

	private void popula(Compra c) throws CRUDException {
		try {
			if (!finalizaEdicao())
				throw new CRUDException("Complete o preenchimento do item!");
			
			if (fornecedorSelecionado == null) {
				fornecedor.requestFocus();
				throw new CRUDException("Selecione um fornecedor!"); 
			}
			
			c.setFornecedor(fornecedorSelecionado);
			c.setData(formatoData.parse(data.getText()));
			c.setNota(nota.getText());
			
			List<ItemModel> models = modelo.getListaItens();
			List<ItemCompra> itens = new ArrayList<ItemCompra>();
			for (ItemModel m: models) {
				if (m.getProduto() == null)
					throw new CRUDException("Selecione um produto!");
				
				ItemCompra i = new ItemCompra();
				i.setProduto(m.getProduto());
				i.setPrecoUnitario(m.getPrecoUnitario());
				i.setQuantidade(m.getQuantidade());
				itens.add(i);
			}
			c.setItens(itens);
		}
		catch (ParseException e) {
			data.requestFocus();
			throw new CRUDException("Data inválida: " + data.getText());
		}
	}

	public void hoje() {
		String hj = DateFormat.getDateInstance().format(new Date());
		data.setText(hj);
	}
	
	public void carregaItens(List<ItemCompra> itens) {
		List<ItemModel> lista = new ArrayList<ItemModel>();

		if (itens != null) {
			for (ItemCompra i: itens) {
				ItemModel m = new ItemModel();
				m.setId(i.getId());
				m.setProduto(i.getProduto());
				m.setPrecoUnitario(i.getPrecoUnitario());
				m.setQuantidade(i.getQuantidade());
				lista.add(m);
			}
		}
		
		modelo.setarLista(lista);
	}


	public void focoEdicao() {
		fornecedor.requestFocus();
	}
	
	public boolean finalizaEdicao() {
		if (itens.isEditing())
			return itens.getCellEditor().stopCellEditing();
		return true;
	}
	
	public void cancelaEdicao() {
		if (itens.isEditing())
			itens.getCellEditor().cancelCellEditing();
	}
	
	private void habilitaBotoes() {
		int ultima = itens.getRowCount() - 1;
		int linha = itens.getSelectedRow();
		incluir.setEnabled(editando && !itens.isEditing());
		excluir.setEnabled(editando && linha != -1 && linha != ultima);
	}
	
	private class SelecaoFornecedor implements AcaoSelecao<Fornecedor> {
		@Override
		public void selecionou(Fornecedor f) {
			fornecedor.setText(f.getNome());
			codigoFornecedor.setText(String.valueOf(f.getId()));
			fornecedorSelecionado = f;
		}
	}
	
	private class DigitacaoTabela extends KeyAdapter {
		@Override
		public void keyTyped(KeyEvent e) {
			if (editando && itens.getSelectedColumn() == 1) {
				e.consume();
				
				Rectangle ret = itens.getCellRect(
						itens.getSelectedRow(), itens.getSelectedColumn(), true);
				Point pos = new Point((int) ret.getX(), (int) ret.getY());
				SwingUtilities.convertPointToScreen(pos, itens);
				
				int largura = itens.getColumnModel().getColumn(1).getWidth();
				popupProdutos.setLocation((int) pos.getX(), (int) pos.getY());
				popupProdutos.setSize(largura, 300);
				popupProdutos.pesquisaTexto(Character.toString(e.getKeyChar()));
			}
		}
	}
	
	private class SelecaoProduto implements AcaoSelecao<Produto> {
		@Override
		public void selecionou(Produto p) {
			ItemModel i = new ItemModel();
			i.setProduto(p);
			modelo.setarItem(itens.getSelectedRow(), i);
		}
	}

	private class AlteracaoTabela implements TableModelListener {
		@Override
		public void tableChanged(TableModelEvent e) {
			total.setText(moeda.format(modelo.getTotal()));
			habilitaBotoes();
		}
	}
	
	private class SelecaoTabela implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			habilitaBotoes();
		}
	}
	
	private class AcaoIncluir implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (itens.isEditing()) { 
				if (!itens.getCellEditor().stopCellEditing()) {
					showMessageDialog(null, "Complete o preenchimento do item!", "Dados Incorretos",
							WARNING_MESSAGE);
					return;
				}
			}
			int ultima = itens.getRowCount() - 1;
			itens.setColumnSelectionInterval(0, 0);
			itens.setRowSelectionInterval(ultima, ultima);
			itens.requestFocus();
		}
	}

	private class AcaoExcluir implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (itens.isEditing()) itens.getCellEditor().cancelCellEditing();
			modelo.excluirItem(itens.getSelectedRow());
		}
	}

}
