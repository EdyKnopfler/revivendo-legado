package br.com.gamecursos.estoque.gui.fornecedores;

import java.text.ParseException;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.MaskFormatter;

import br.com.gamecursos.estoque.model.Fornecedor;
import br.com.gamecursos.swingcrud.CRUDException;
import br.com.gamecursos.swingcrud.PainelCampos;
import br.com.gamecursos.util.JTextFieldLimit;

public class PainelFornecedor extends PainelCampos<Fornecedor> {

	private static final long serialVersionUID = 1L;
	
	private JTextField nome;
	private JFormattedTextField cnpj;
	private JTextField inscricaoEstadual;
	private JFormattedTextField telefone1;
	private JFormattedTextField telefone2;
	private JTextField endereco;
	private JTextField bairro;
	private JTextField cidade;
	private JFormattedTextField cep;
	private JTextField estado;
	private JTextField email;
	
	private Long id;
	
	public PainelFornecedor() {
		// Componentes
		nome = new JTextField(40);
		nome.setDocument(new JTextFieldLimit(40));
		inscricaoEstadual = new JTextField(15);
		inscricaoEstadual.setDocument(new JTextFieldLimit(20));
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
		
		try {
			cnpj = new JFormattedTextField(new MaskFormatter("##.###.###/####-##"));
			telefone1 = new JFormattedTextField(new MaskFormatter("(##) ####-####"));
			telefone2 = new JFormattedTextField(new MaskFormatter("(##) ####-####"));
			cep = new JFormattedTextField(new MaskFormatter("#####-###"));
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
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 4;
		add(nome, gbc);

		gbc.gridwidth = 1;
		gbc.gridy = 2;
		gbc.gridx = 0;
		add(new JLabel("CNPJ:"), gbc);
		gbc.gridx = 1;
		add(new JLabel("Insc. Est.:"), gbc);
		gbc.gridx = 2;
		add(new JLabel("Telefone 1:"), gbc);
		gbc.gridx = 3;
		add(new JLabel("Telefone 2:"), gbc);

		gbc.gridwidth = 1;
		gbc.gridy = 3;
		gbc.gridx = 0;
		add(cnpj, gbc);
		gbc.gridx = 1;
		add(inscricaoEstadual, gbc);
		gbc.gridx = 2;
		add(telefone1, gbc);
		gbc.gridx = 3;
		add(telefone2, gbc);

		gbc.gridx = 0;
		gbc.gridy = 4;
		add(new JLabel("Endereço:"), gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridwidth = 4;
		add(endereco, gbc);

		gbc.gridwidth = 1;
		gbc.gridy = 6;
		gbc.gridx = 0;
		add(new JLabel("Bairro:"), gbc);
		gbc.gridx = 2;
		add(new JLabel("Cidade:"), gbc);

		gbc.gridwidth = 2;
		gbc.gridy = 7;
		gbc.gridx = 0;
		add(bairro, gbc);
		gbc.gridx = 2;
		add(cidade, gbc);

		gbc.gridwidth = 1;
		gbc.gridy = 8;
		gbc.gridx = 0;
		add(new JLabel("CEP:"), gbc);
		gbc.gridx = 1;
		add(new JLabel("Estado:"), gbc);
		gbc.gridx = 2;
		add(new JLabel("E-mail:"), gbc);
		gbc.gridy = 9;
		gbc.gridx = 0;
		add(cep, gbc);
		gbc.gridx = 1;
		add(estado, gbc);
		gbc.gridx = 2;
		gbc.gridwidth = 2;
		add(email, gbc);
	}

	@Override
	public void exibir(Fornecedor f) {
		nome.setText(f.getNome());
		cnpj.setText(f.getCnpj());
		inscricaoEstadual.setText(f.getInscricaoEstadual());
		telefone1.setText(f.getTelefone1());
		telefone2.setText(f.getTelefone2());
		endereco.setText(f.getEndereco());
		bairro.setText(f.getBairro());
		cidade.setText(f.getCidade());
		cep.setText(f.getCep());
		estado.setText(f.getEstado());
		email.setText(f.getEmail());
		
		id = f.getId();
	}

	@Override
	public Fornecedor novoObjeto() throws CRUDException {
		Fornecedor f = new Fornecedor();
		f.setNome(nome.getText());
		f.setCnpj(cnpj.getText());
		f.setInscricaoEstadual(inscricaoEstadual.getText());
		f.setTelefone1(telefone1.getText());
		f.setTelefone2(telefone2.getText());
		f.setEndereco(endereco.getText());
		f.setBairro(bairro.getText());
		f.setCidade(cidade.getText());
		f.setCep(cep.getText());
		f.setEstado(estado.getText());
		f.setEmail(email.getText());
		return f;
	}

	@Override
	public Fornecedor objetoSendoAlterado() throws CRUDException {
		Fornecedor f = novoObjeto();
		f.setId(id);
		return f;
	}

	@Override
	public void limpar() {
		nome.setText("");
		cnpj.setText("");
		inscricaoEstadual.setText("");
		telefone1.setText("");
		telefone2.setText("");
		endereco.setText("");
		bairro.setText("");
		cidade.setText("");
		cep.setText("");
		estado.setText("");
		email.setText("");
		
		id = null;
	}

	@Override
	public void habilitarCampos(boolean habilitar) {
		nome.setEditable(habilitar);
		cnpj.setEditable(habilitar);
		inscricaoEstadual.setEditable(habilitar);
		telefone1.setEditable(habilitar);
		telefone2.setEditable(habilitar);
		endereco.setEditable(habilitar);
		bairro.setEditable(habilitar);
		cidade.setEditable(habilitar);
		cep.setEditable(habilitar);
		estado.setEditable(habilitar);
		email.setEditable(habilitar);
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
