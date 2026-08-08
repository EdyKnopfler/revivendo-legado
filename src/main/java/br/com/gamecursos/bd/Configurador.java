package br.com.gamecursos.bd;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import br.com.gamecursos.util.Configuracao;

public class Configurador extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private JTextField ip, arquivo, usuario, senha, charset;

	public Configurador() {
		setTitle("Configurar Conexão");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		ip = new JTextField(25);
		arquivo = new JTextField(25);
		usuario = new JTextField(25);
		senha = new JPasswordField(25);
		charset = new JTextField(25);
		charset.setText("UTF8");

		JPanel campos = new JPanel();
		campos.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.WEST;

		gbc.gridx = 0;
		gbc.gridy = 0;
		campos.add(new JLabel("IP"), gbc);
		gbc.gridy++;
		campos.add(new JLabel("Arquivo"), gbc);
		gbc.gridy++;
		campos.add(new JLabel("Usuário"), gbc);
		gbc.gridy++;
		campos.add(new JLabel("Senha"), gbc);
		gbc.gridy++;
		campos.add(new JLabel("Charset"), gbc);

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.gridx = 1;
		gbc.gridy = 0;
		campos.add(ip, gbc);
		gbc.gridy++;
		campos.add(arquivo, gbc);
		gbc.gridy++;
		campos.add(usuario, gbc);
		gbc.gridy++;
		campos.add(senha, gbc);
		gbc.gridy++;
		campos.add(charset, gbc);
		
		JButton bd = new JButton("...");
		bd.addActionListener(new SelecionaBD());
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0.0;
		gbc.gridx = 2;
		gbc.gridy = 1;
		campos.add(bd, gbc);
		
		JPanel botoes = new JPanel();
		botoes.setLayout(new FlowLayout());
		JButton abrir = new JButton("Abrir...");
		abrir.addActionListener(new BotaoAbrir());
		botoes.add(abrir);
		JButton salvar = new JButton("Salvar...");
		salvar.addActionListener(new BotaoSalvar());
		botoes.add(salvar);
		
		setLayout(new BorderLayout());
		getContentPane().add(campos, BorderLayout.CENTER);
		getContentPane().add(botoes, BorderLayout.SOUTH);

		pack();
		setLocationRelativeTo(null);
	}
	
	private class SelecionaBD implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser dialogo = new JFileChooser();
		    FileNameExtensionFilter filtro = new FileNameExtensionFilter(
		        "Bancos de dados", "FDB");
		    dialogo.setFileFilter(filtro);
		    int escolha = dialogo.showOpenDialog(Configurador.this);
		    
		    if (escolha == JFileChooser.APPROVE_OPTION) 
		    	arquivo.setText(dialogo.getSelectedFile().getAbsolutePath());
		}
		
	}
	
	private class BotaoAbrir implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent evt) {
			JFileChooser dialogo = new JFileChooser();
		    FileNameExtensionFilter filtro = new FileNameExtensionFilter(
		        "Arquivos de configuração", "properties");
		    dialogo.setDialogTitle("Abrir Configuração");
		    dialogo.setFileFilter(filtro);
		    int escolha = dialogo.showOpenDialog(Configurador.this);
		    
		    if (escolha == JFileChooser.APPROVE_OPTION) {
		    	try {
		    		String caminho = dialogo.getSelectedFile().getAbsolutePath();
		    		if (!caminho.endsWith(".properties")) caminho += ".properties";
		    		Configuracao c = new Configuracao();
		    		c.carregar(caminho);
		    		ip.setText(c.getIp());
		    		arquivo.setText(c.getArquivo());
		    		usuario.setText(c.getUsuario());
		    		senha.setText(c.getSenha());
		    		charset.setText(c.getCharset());
		    	}
		    	catch (Exception ex) {
		    		ex.printStackTrace();
		    	}
		    }
		}
		
	}
	
	private class BotaoSalvar implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent evt) {
			JFileChooser dialogo = new JFileChooser();
		    FileNameExtensionFilter filtro = new FileNameExtensionFilter(
		        "Arquivos de configuração", "properties");
		    dialogo.setFileFilter(filtro);
		    dialogo.setDialogTitle("Salvar Configuração");
		    int escolha = dialogo.showSaveDialog(Configurador.this);
		    
		    if (escolha == JFileChooser.APPROVE_OPTION) {
		    	try {
		    		String caminho = dialogo.getSelectedFile().getAbsolutePath();
		    		if (!caminho.endsWith(".properties")) caminho += ".properties";
		    		Configuracao c = new Configuracao();
		    		c.setIp(ip.getText());
		    		c.setArquivo(arquivo.getText());
		    		c.setUsuario(usuario.getText());
		    		c.setSenha(senha.getText());
		    		c.setCharset(charset.getText());
		    		c.salvar(caminho);
		    	}
		    	catch (Exception ex) {
		    		ex.printStackTrace();
		    	}
		    }
		}
		
	}
	
	public static void main(String[] args) {
		new Configurador().setVisible(true);
	}

}
