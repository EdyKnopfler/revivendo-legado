package br.com.gamecursos.estoque.gui;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;

import javax.swing.*;

import br.com.gamecursos.estoque.Aplicacao;
import br.com.gamecursos.estoque.gui.clientes.FormClientes;
import br.com.gamecursos.estoque.gui.compras.FormCompras;
import br.com.gamecursos.estoque.gui.fornecedores.FormFornecedores;
import br.com.gamecursos.estoque.gui.pedidos.FormPedidos;
import br.com.gamecursos.estoque.gui.produtos.FormProdutos;
import br.com.gamecursos.estoque.gui.relatorios.RelClientes;
import br.com.gamecursos.estoque.gui.relatorios.RelEstoque;
import br.com.gamecursos.estoque.gui.relatorios.RelPedidos;
import br.com.gamecursos.estoque.gui.relatorios.RelVendas;
import br.com.gamecursos.util.BackupForm;

public class MenuForm extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private Aplicacao app;
	private FormPedidos pedidos;
	private FormCompras compras;
	private FormFornecedores fornecedores;
	private FormClientes clientes;
	private FormProdutos produtos;
	private RelEstoque relEstoque;
	
	public MenuForm(Aplicacao app) {
		this.app = app;
		
		setTitle("Sistema de Estoque");
		setSize(850, 600);
		addWindowListener(new EventosJanela());
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		JPanel botoes = new JPanel();
		botoes.setLayout(new GridBagLayout());
		Dimension tamBotao = new Dimension(170, 40);
		
		JButton btnFornecedores = new JButton("Fornecedores");
		URL urlFornecedores = Aplicacao.class.getResource("/fornecedores.png");
		ImageIcon imgFornecedores = new ImageIcon(urlFornecedores);
		btnFornecedores.setIcon(imgFornecedores);
		btnFornecedores.addActionListener(new BotaoFornecedores());
		btnFornecedores.setPreferredSize(tamBotao);

		JButton btnProdutos = new JButton("Produtos");
		URL urlProdutos = Aplicacao.class.getResource("/produtos.png");
		ImageIcon imgProdutos = new ImageIcon(urlProdutos);
		btnProdutos.setIcon(imgProdutos);
		btnProdutos.addActionListener(new BotaoProdutos());
		btnProdutos.setPreferredSize(tamBotao);

		JButton btnClientes = new JButton("Clientes");
		URL urlClientes = Aplicacao.class.getResource("/clientes.png");
		ImageIcon imgClientes = new ImageIcon(urlClientes);
		btnClientes.setIcon(imgClientes);
		btnClientes.addActionListener(new BotaoClientes());
		btnClientes.setPreferredSize(tamBotao);

		JButton btnCompras = new JButton("Compras");
		URL urlCompras = Aplicacao.class.getResource("/compras.png");
		ImageIcon imgCompras = new ImageIcon(urlCompras);
		btnCompras.setIcon(imgCompras);
		btnCompras.addActionListener(new BotaoCompras());
		btnCompras.setPreferredSize(tamBotao);
		
		JButton btnPedidos = new JButton("Pedidos");
		URL urlPedidos = Aplicacao.class.getResource("/pedidos.png");
		ImageIcon imgPedidos = new ImageIcon(urlPedidos);
		btnPedidos.setIcon(imgPedidos);
		btnPedidos.addActionListener(new BotaoPedidos());
		btnPedidos.setPreferredSize(tamBotao);
		
		JButton btnRelEstoque= new JButton("Estoque");
		URL urlEstoque = Aplicacao.class.getResource("/estoque.png");
		ImageIcon imgEstoque= new ImageIcon(urlEstoque);
		btnRelEstoque.setIcon(imgEstoque);
		btnRelEstoque.addActionListener(new BotaoRelEstoque());
		btnRelEstoque.setPreferredSize(tamBotao);
		
		JButton btnRelClientes = new JButton("Clientes");
		btnRelClientes.setIcon(imgClientes);  // Mesma imagem :)
		btnRelClientes.addActionListener(new BotaoRelClientes());
		btnRelClientes.setPreferredSize(tamBotao);

		JButton btnRelPedidos = new JButton("Pedidos");
		URL urlRelPedidos = Aplicacao.class.getResource("/rel-pedidos.png");
		ImageIcon imgRelPedidos = new ImageIcon(urlRelPedidos);
		btnRelPedidos.setIcon(imgRelPedidos);
		btnRelPedidos.addActionListener(new BotaoRelPedidos());
		btnRelPedidos.setPreferredSize(tamBotao);
		
		JButton btnRelVendas = new JButton("Vendas");
		URL urlRelVendas= Aplicacao.class.getResource("/vendas.png");
		ImageIcon imgRelVendas= new ImageIcon(urlRelVendas);
		btnRelVendas.setIcon(imgRelVendas);
		btnRelVendas.addActionListener(new BotaoRelVendas());
		btnRelVendas.setPreferredSize(tamBotao);
		
		JButton btnBackup = new JButton("Backup");
		URL urlBackup = Aplicacao.class.getResource("/backup.png");
		ImageIcon imgBackup = new ImageIcon(urlBackup);
		btnBackup.setIcon(imgBackup);
		btnBackup.addActionListener(new BotaoBackup());
		btnBackup.setPreferredSize(tamBotao);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);

		gbc.gridx = 0;
		gbc.gridy = 0;
		botoes.add(new JLabel("Cadastros"), gbc);
		gbc.gridy = 1;
		botoes.add(btnProdutos, gbc);
		gbc.gridy = 2;
		botoes.add(btnFornecedores, gbc);
		gbc.gridy = 3;
		botoes.add(btnClientes, gbc);
		gbc.gridy = 4;
		botoes.add(btnCompras, gbc);
		gbc.gridy = 5;
		botoes.add(btnPedidos, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		botoes.add(new JLabel("Relatórios"), gbc);
		gbc.gridy = 1;
		botoes.add(btnRelEstoque, gbc);
		gbc.gridy = 2;
		botoes.add(btnRelClientes, gbc);
		gbc.gridy = 3;
		botoes.add(btnRelPedidos, gbc);
		gbc.gridy = 4;
		botoes.add(btnRelVendas, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 6;
		gbc.gridwidth = 2;
		botoes.add(btnBackup, gbc);
		
		FlowLayout fl = new FlowLayout();
		fl.setAlignment(FlowLayout.LEFT);
		setLayout(fl);
		getContentPane().add(botoes);
	}
	
	private class EventosJanela extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			if (produtos != null) produtos.dispose();
			if (fornecedores != null) fornecedores.dispose();
			if (clientes != null) clientes.dispose();
			if (compras != null) compras.dispose();
			if (pedidos != null) pedidos.dispose();
			if (relEstoque != null) relEstoque.dispose();
			
			try {
				app.desconectar();
			}
			catch (Exception ex) { /* Estamos indo embora! */ }
			
			MenuForm.this.dispose();
		}
	}
	
	private class BotaoFornecedores implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (fornecedores == null)
				fornecedores = new FormFornecedores(app.getConnection());
			
			fornecedores.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosed(WindowEvent e) {
					fornecedores = null;
				}
			});
			
			fornecedores.setVisible(true);
		}
	}
	
	private class BotaoProdutos implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (produtos == null)
				produtos = new FormProdutos(app.getConnection());
			
			produtos.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosed(WindowEvent e) {
					produtos = null;
				}
			});
			
			produtos.setVisible(true);
		}
	}
	
	private class BotaoClientes implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (clientes == null)
				clientes = new FormClientes(app.getConnection());
			
			clientes.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosed(WindowEvent e) {
					clientes = null;
				}
			});
			
			clientes.setVisible(true);
		}
	}
	
	private class BotaoCompras implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (compras == null)
				compras = new FormCompras(app.getConnection());
			
			compras.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosed(WindowEvent e) {
					compras = null;
				}
			});
			
			compras.setVisible(true);
		}
	}
	
	private class BotaoPedidos implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (pedidos == null)
				pedidos = new FormPedidos(app.getConnection());
			
			pedidos.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosed(WindowEvent e) {
					pedidos = null;
				}
			});
			
			pedidos.setVisible(true);
		}
	}
	
	private class BotaoRelEstoque implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (relEstoque == null)
				relEstoque = new RelEstoque(app.getConnection());
			
			relEstoque.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosed(WindowEvent e) {
					relEstoque = null;
				}
			});
			
			relEstoque.setVisible(true);
		}
	}
	
	private class BotaoRelClientes implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			new RelClientes(app.getConnection()).setVisible(true);
		}
	}
	
	private class BotaoRelPedidos implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			new RelPedidos(app.getConnection()).setVisible(true);
		}
	}
	
	private class BotaoRelVendas implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			new RelVendas(app.getConnection()).setVisible(true);
		}
	}
	
	private class BotaoBackup implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			new BackupForm(app).setVisible(true);
		}
	}

}
