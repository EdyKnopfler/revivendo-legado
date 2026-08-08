package br.com.gamecursos.estoque.gui.relatorios;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URI;
import java.sql.ResultSet;
import java.text.DecimalFormat;

import javax.swing.*;

import java.sql.Connection;
import java.sql.PreparedStatement;

import br.com.gamecursos.estoque.dao.FornecedorDao;
import br.com.gamecursos.estoque.gui.fornecedores.PopupPesquisaFornecedores;
import br.com.gamecursos.estoque.model.Fornecedor;
import br.com.gamecursos.util.Relatorio;
import br.com.gamecursos.util.pesquisa.AcaoSelecao;
import br.com.gamecursos.util.pesquisa.CampoPesquisa;

import static javax.swing.JOptionPane.*;

public class RelEstoque extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private Connection connection;
	private JCheckBox selecionarFornecedor;
	private CampoPesquisa<Fornecedor> fornecedor;
	private Fornecedor selecionado;
	private JLabel codigoFornecedor;
	private JRadioButton porProduto;
	private JRadioButton porFornecedor;

	private static final DecimalFormat moeda = new DecimalFormat("#,##0.00");
	private static final String camposEstoque = 
		"( " +
		"   SELECT SUM(i.quantidade) AS qtd_comprada FROM itens_compra i " +
		"   WHERE i.id_produto = p.id_produto " +
		"), " +
		"( " +
		"   SELECT SUM(i.quantidade) AS qtd_vendida FROM itens_pedido i " +
		"   WHERE i.id_produto = p.id_produto " +
		") ";			

	public RelEstoque(Connection connection) {
		this.connection = connection;
		setTitle("Relatório de Estoque");
		setSize(550, 200);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		selecionarFornecedor = new JCheckBox("Selecionar fornecedor:");
		PopupPesquisaFornecedores popup = new PopupPesquisaFornecedores(
				new SelecaoFornecedor(), new FornecedorDao(connection));
		fornecedor = new CampoPesquisa<Fornecedor>(popup);
		codigoFornecedor = new JLabel();
		porProduto = new JRadioButton("Produto"); 
		porFornecedor = new JRadioButton("Fornecedor");
		
		ButtonGroup grupo = new ButtonGroup();
		grupo.add(porProduto);
		grupo.add(porFornecedor);
		
		porProduto.setSelected(true);
		
		JPanel campos = new JPanel();
		campos.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 5, 5, 5);

		gbc.gridx = 0;
		gbc.gridy = 0;
		campos.add(selecionarFornecedor, gbc);
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		campos.add(fornecedor, gbc);
		gbc.gridx = 3;
		gbc.gridwidth = 1;
		campos.add(codigoFornecedor, gbc);
		gbc.gridx = 0;
		gbc.gridy = 2;
		campos.add(new JLabel("Ordenar por:"), gbc);
		gbc.gridy = 3;
		campos.add(porProduto, gbc);
		gbc.gridx = 1;
		campos.add(porFornecedor, gbc);
		
		JButton ok = new JButton("OK");
		JButton cancelar = new JButton("Cancelar");
		
		Dimension d = new Dimension(100, 30);
		ok.setPreferredSize(d);
		cancelar.setPreferredSize(d);
		
		JPanel botoes = new JPanel();
		botoes.add(ok);
		botoes.add(cancelar);
		
		setLayout(new BorderLayout());
		getContentPane().add(campos, BorderLayout.CENTER);
		getContentPane().add(botoes, BorderLayout.SOUTH);
		
		fornecedor.setEditable(false);
		selecionarFornecedor.addActionListener(new MudancaSelecao());
		ok.addActionListener(new BotaoOk());
		cancelar.addActionListener(new BotaoCancelar());
	}
	
	private void comFornecedor() throws Exception {
		PreparedStatement ps = connection.prepareStatement(
			" SELECT p.*, " + camposEstoque + " FROM produtos p " +
			" WHERE id_fornecedor = ? " +
			" ORDER BY p.nome "
		);
		ps.setLong(1, selecionado.getId());
		ResultSet rs = ps.executeQuery();
		
		Relatorio rel = new Relatorio("relatorio.html");
		rel.carregarModelo("/estoqueforn-cabecalho.rel");
		rel.substituirTag("<$fornecedor>", selecionado.getNome());
		rel.escrever();
		
		rel.carregarModelo("/estoqueforn-registro.rel");
		double total = 0.00;
		
		while (rs.next()) 
			total += registro(rs, rel, false);
		
		rel.carregarModelo("/estoqueforn-rodape.rel");
		rel.substituirTag("<$total>", moeda.format(total));
		rel.escrever();

		rs.close();
		ps.close();

		rel.finalizar();
		File f = new File("relatorio.html");
		Desktop.getDesktop().browse(new URI("file:///" + 
				f.getAbsolutePath().replace("\\", "/").replace(" ", "%20")));
	}
	
	private void semFornecedor() throws Exception {
		PreparedStatement ps = connection.prepareStatement(
			" SELECT p.*, f.nome AS fornecedor, " + camposEstoque +
			" FROM produtos p LEFT JOIN fornecedores f " +
			" ON p.id_fornecedor = f.id_fornecedor " +
			" ORDER BY " + (porProduto.isSelected() ? "p.nome" : "f.nome")
		);
		ResultSet rs = ps.executeQuery();
		
		Relatorio rel = new Relatorio("relatorio.html");
		rel.carregarModelo("/estoque-cabecalho.rel");
		rel.escrever();
		
		rel.carregarModelo("/estoque-registro.rel");
		double total = 0.00;
		
		while (rs.next()) 
			total += registro(rs, rel, true);
		
		rel.carregarModelo("/estoque-rodape.rel");
		rel.substituirTag("<$total>", moeda.format(total));
		rel.escrever();

		rs.close();
		ps.close();

		rel.finalizar();
		File f = new File("relatorio.html");
		Desktop.getDesktop().browse(new URI("file:///" + 
				f.getAbsolutePath().replace("\\", "/").replace(" ", "%20")));
	}
	
	private class MudancaSelecao implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			fornecedor.setEditable(selecionarFornecedor.isSelected());
		}
	}
	
	private double registro(ResultSet rs, Relatorio rel, boolean campoFornecedor)
			throws Exception {
		
		int qtdComprada = rs.getInt("QTD_COMPRADA");
		int qtdVendida = rs.getInt("QTD_VENDIDA");
		int quantidade = rs.getInt("QUANTIDADE");
		double custoUnitario = rs.getDouble("PRECO_CUSTO_UNIT");
		double custoTotal = quantidade * custoUnitario;
		
		if (quantidade != qtdComprada - qtdVendida)
			showMessageDialog(null, rs.getString("NOME"), "Erro no estoque!", WARNING_MESSAGE);
		
		rel.substituirTag("<#CODIGO>", rs.getString("CODIGO"));
		rel.substituirTag("<#NOME>", rs.getString("NOME"));
		
		if (campoFornecedor)
			rel.substituirTag("<#FORNECEDOR>", rs.getString("FORNECEDOR"));
		
		rel.substituirTag("<#QTD_COMPRADA>", String.valueOf(qtdComprada));
		rel.substituirTag("<#QTD_VENDIDA>", String.valueOf(qtdVendida));
		rel.substituirTag("<#QUANTIDADE>", String.valueOf(quantidade));
		rel.substituirTag("<#PRECO_CUSTO_UNIT>", moeda.format(custoUnitario));
		rel.substituirTag("<#CUSTO_TOTAL>", moeda.format(custoTotal));
		rel.escrever();
		return custoTotal;
	}
	
	private class SelecaoFornecedor implements AcaoSelecao<Fornecedor> {
		@Override
		public void selecionou(Fornecedor f) {
			fornecedor.setText(f.getNome());
			selecionado = f;
			codigoFornecedor.setText(String.valueOf(f.getId()));
		}
	}

	private class BotaoOk implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			boolean selecionarForn = selecionarFornecedor.isSelected();
			
			if (selecionarForn && selecionado == null) {
				showMessageDialog(null, "Selecione um fornecedor!", "Atenção!", WARNING_MESSAGE);
				fornecedor.requestFocus();
				return;
			}
			
			try {
				if (selecionarForn)
					comFornecedor();
				else
					semFornecedor();
				
				dispose();
			}
			catch (Exception ex) {
				ex.printStackTrace();
				showMessageDialog(null, ex.getMessage(), "ERRO AO GERAR RELATÓRIO", ERROR_MESSAGE);
			}
		}
	}
	
	private class BotaoCancelar implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			dispose();
		}
	}

}
