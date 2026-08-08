package br.com.gamecursos.estoque.gui.clientes;

import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import javax.swing.*;
import javax.swing.text.MaskFormatter;

import br.com.gamecursos.estoque.model.Cliente;
import br.com.gamecursos.swingcrud.CRUDException;
import br.com.gamecursos.swingcrud.PainelCampos;
import br.com.gamecursos.util.JTextFieldLimit;

public class PainelCliente extends PainelCampos<Cliente> {

	private static final long serialVersionUID = 1L;
	
	private JTextField nome;
	private JFormattedTextField cpf;
	private JFormattedTextField nascimento;
	private JTextField rg;
	private JFormattedTextField telefone;
	private JFormattedTextField celular;
	private JTextField endereco;
	private JTextField bairro;
	private JTextField cidade;
	private JFormattedTextField cep;
	private JTextField estado;
	private JTextField email;
	private JTextField enderecoComercial;
	private JFormattedTextField cnpj;
	private JTextArea referencias;
	
	private Long id;
	
	public PainelCliente() {
		// Componentes
		nome = new JTextField(40);
		nome.setDocument(new JTextFieldLimit(40));
		rg = new JTextField(20);
		rg.setDocument(new JTextFieldLimit(20));
		endereco = new JTextField(50);
		endereco.setDocument(new JTextFieldLimit(50));
		bairro = new JTextField(30);
		bairro.setDocument(new JTextFieldLimit(30));
		cidade = new JTextField(30);
		cidade.setDocument(new JTextFieldLimit(30));
		estado = new JTextField(2);
		estado.setDocument(new JTextFieldLimit(2));
		email = new JTextField(30);
		email.setDocument(new JTextFieldLimit(80));
		enderecoComercial = new JTextField(50);
		enderecoComercial.setDocument(new JTextFieldLimit(50));
		referencias = new JTextArea(5, 30);
		
		try {
			nascimento = new JFormattedTextField(new MaskFormatter("##/##/####"));
			cpf = new JFormattedTextField(new MaskFormatter("###.###.###-##"));
			telefone = new JFormattedTextField(new MaskFormatter("(##) ####-####"));
			celular = new JFormattedTextField(new MaskFormatter("(##) ####-####"));
			cep = new JFormattedTextField(new MaskFormatter("#####-###"));
			cnpj = new JFormattedTextField(new MaskFormatter("##.###.###/####-##"));
		} 
		catch (ParseException e) {
			e.printStackTrace();
		}
		
		estado.addKeyListener(new DigitacaoEstado());
		
		// Layout
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.BOTH;
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(new JLabel("Nome:"), gbc);
		gbc.gridx = 3;
		add(new JLabel("CPF:"), gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 3;
		add(nome, gbc);
		gbc.gridx = 3;
		gbc.gridwidth = 1;
		add(cpf, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		add(new JLabel("Nascimento:"), gbc);
		gbc.gridx = 1;
		add(new JLabel("RG:"), gbc);
		gbc.gridx = 2;
		add(new JLabel("Telefone:"), gbc);
		gbc.gridx = 3;
		add(new JLabel("Celular:"), gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		add(nascimento, gbc);
		gbc.gridx = 1;
		add(rg, gbc);
		gbc.gridx = 2;
		add(telefone, gbc);
		gbc.gridx = 3;
		add(celular, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 4;
		add(new JLabel("Endereço:"), gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridwidth = 4;
		add(endereco, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 6;
		gbc.gridwidth = 1;
		add(new JLabel("Bairro"), gbc);
		gbc.gridx = 2;
		add(new JLabel("Cidade"), gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 7;
		gbc.gridwidth = 2;
		add(bairro, gbc);
		gbc.gridx = 2;
		add(cidade, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 8;
		gbc.gridwidth = 1;
		add(new JLabel("CEP:"), gbc);
		gbc.gridx = 1;
		add(new JLabel("Estado:"), gbc);
		gbc.gridx = 2;
		add(new JLabel("E-mail:"), gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 9;
		add(cep, gbc);
		gbc.gridx = 1;
		add(estado, gbc);
		gbc.gridwidth = 2;
		gbc.gridx = 2;
		add(email, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 10;
		gbc.gridwidth = 1;
		add(new JLabel("End. comercial:"), gbc);
		gbc.gridx = 3;
		add(new JLabel("CNPJ:"), gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 11;
		gbc.gridwidth = 3;
		add(enderecoComercial, gbc);
		gbc.gridx = 3;
		gbc.gridwidth = 1;
		add(cnpj, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 12;
		gbc.gridwidth = 1;
		add(new JLabel("Referências:"), gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 13;
		gbc.gridwidth = 4;
		add(new JScrollPane(referencias), gbc);
	}

	@Override
	public void exibir(Cliente c) {
		nome.setText(c.getNome());
		cpf.setText(c.getCpf());
		
		Date nasc = c.getNascimento();
		String formatada = nasc == null ? "" : DateFormat.getDateInstance().format(nasc);
		nascimento.setText(formatada);
		
		rg.setText(c.getRg());
		telefone.setText(c.getTelefone());
		celular.setText(c.getCelular());
		endereco.setText(c.getEndereco());
		bairro.setText(c.getBairro());
		cidade.setText(c.getCidade());
		cep.setText(c.getCep());
		estado.setText(c.getEstado());
		email.setText(c.getEmail());
		enderecoComercial.setText(c.getEnderecoComercial());
		cnpj.setText(c.getCnpj());
		referencias.setText(c.getReferencias());
		
		id = c.getId();
	}

	@Override
	public Cliente novoObjeto() throws CRUDException {
		try {
			Cliente c = new Cliente();
			c.setNome(nome.getText());
			c.setCpf(cpf.getText());
			
			if (!nascimento.getText().equals("  /  /    "))
				c.setNascimento(DateFormat.getDateInstance().parse(nascimento.getText()));
			
			c.setRg(rg.getText());
			c.setTelefone(telefone.getText());
			c.setCelular(celular.getText());
			c.setEndereco(endereco.getText());
			c.setBairro(bairro.getText());
			c.setCidade(cidade.getText());
			c.setCep(cep.getText());
			c.setEstado(estado.getText());
			c.setEmail(email.getText());
			c.setEnderecoComercial(enderecoComercial.getText());
			c.setCnpj(cnpj.getText());
			c.setReferencias(referencias.getText());
			return c;
		}
		catch (ParseException e) {
			nascimento.requestFocus();
			throw new CRUDException("Data inválida: " + nascimento.getText());
		}
	}

	@Override
	public Cliente objetoSendoAlterado() throws CRUDException {
		Cliente f = novoObjeto();
		f.setId(id);
		return f;
	}

	@Override
	public void limpar() {
		nome.setText("");
		cpf.setText("");
		nascimento.setText("");
		rg.setText("");
		telefone.setText("");
		celular.setText("");
		endereco.setText("");
		bairro.setText("");
		cidade.setText("");
		cep.setText("");
		estado.setText("");
		email.setText("");
		enderecoComercial.setText("");
		cnpj.setText("");
		referencias.setText("");
		
		id = null;
	}

	@Override
	public void habilitarCampos(boolean habilitar) {
		nome.setEditable(habilitar);
		cpf.setEditable(habilitar);
		nascimento.setEditable(habilitar);
		rg.setEditable(habilitar);
		telefone.setEditable(habilitar);
		celular.setEditable(habilitar);
		endereco.setEditable(habilitar);
		bairro.setEditable(habilitar);
		cidade.setEditable(habilitar);
		cep.setEditable(habilitar);
		estado.setEditable(habilitar);
		email.setEditable(habilitar);
		enderecoComercial.setEditable(habilitar);
		cnpj.setEditable(habilitar);
		referencias.setEditable(habilitar);
	}

	public void focoEdicao() {
		nome.requestFocus();
	}
	
	private class DigitacaoEstado extends KeyAdapter {
		@Override
		public void keyTyped(KeyEvent e) {
			e.setKeyChar(Character.toUpperCase(e.getKeyChar()));
		}
	}

}
