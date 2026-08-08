package br.com.gamecursos.estoque.gui.relatorios;

import static javax.swing.JOptionPane.*;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.*;
import javax.swing.text.MaskFormatter;

import br.com.gamecursos.util.Relatorio;

public class RelClientes extends JDialog {

	private static final long serialVersionUID = 1L;
	private static final DecimalFormat codigo = new DecimalFormat("0000"); 
	
	private Connection connection;
	private JCheckBox aniversariantes;
	private JFormattedTextField de;
	private JFormattedTextField ate;
	
	public RelClientes(Connection connection) {
		this.connection = connection;
		setTitle("Relatório de Clientes");
		setSize(300, 150);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModal(true);
		
		aniversariantes = new JCheckBox("Data de nascimento entre:");
		try {
			de = new JFormattedTextField(new MaskFormatter("##/##"));
			ate = new JFormattedTextField(new MaskFormatter("##/##"));
		} catch (ParseException e) {}
		JLabel lblE = new JLabel("    e    ");
		lblE.setHorizontalAlignment(SwingConstants.CENTER);
		
		JPanel campos = new JPanel();
		campos.add(aniversariantes);
		
		JPanel camposInterno = new JPanel();
		camposInterno.setLayout(new GridLayout(1, 3));
		camposInterno.add(de);
		camposInterno.add(lblE);
		camposInterno.add(ate);
		campos.add(camposInterno);
		
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
		
		ok.addActionListener(new BotaoOk());
		cancelar.addActionListener(new BotaoCancelar());
	}
	
	private void gerarRelatorio() throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			if (aniversariantes.isSelected()) {
				Date inicio = dataCampo(de);
				Date fim = dataCampo(ate);
				ps = montaQuery(inicio, fim);
			}
			else
				ps = connection.prepareStatement("SELECT * FROM clientes ORDER BY nome");
				
			Relatorio rel = new Relatorio("relatorio.html");
			
			rel.carregarModelo("/clientes-cabecalho.rel");
			rel.escrever();
					
			rs = ps.executeQuery();
			rel.carregarModelo("/clientes-registro.rel");
			
			while (rs.next()) {
				Date nasc = rs.getDate("NASCIMENTO");
				String nascS = nasc == null ? "" : DateFormat.getDateInstance().format(nasc);
				rel.substituirTag("<#ID_CLIENTE>", codigo.format(rs.getLong("ID_CLIENTE")));
				rel.substituirTag("<#NOME>", rs.getString("NOME"));
				rel.substituirTag("<#NASCIMENTO>", nascS);
				rel.substituirTag("<#CPF>", rs.getString("CPF"));
				rel.substituirTag("<#RG>", rs.getString("RG"));
				rel.substituirTag("<#TELEFONE>", rs.getString("TELEFONE"));
				rel.substituirTag("<#CELULAR>", rs.getString("CELULAR"));
				rel.substituirTag("<#ENDERECO>", rs.getString("ENDERECO"));
				rel.substituirTag("<#BAIRRO>", rs.getString("BAIRRO"));
				rel.substituirTag("<#CIDADE>", rs.getString("CIDADE"));
				rel.substituirTag("<#ESTADO>", rs.getString("ESTADO"));
				rel.substituirTag("<#CEP>", rs.getString("CEP"));
				rel.substituirTag("<#EMAIL>", rs.getString("EMAIL"));
				rel.substituirTag("<#ENDERECO_COMERCIAL>", rs.getString("ENDERECO_COMERCIAL"));
				rel.substituirTag("<#CNPJ>", rs.getString("CNPJ"));
				rel.escrever();
			}
			
			rel.carregarModelo("/clientes-rodape.rel");
			rel.escrever();
	
			rel.finalizar();
			File f = new File("relatorio.html");
			Desktop.getDesktop().browse(new URI("file:///" + 
					f.getAbsolutePath().replace("\\", "/").replace(" ", "%20")));
		}
		finally {
			if (rs != null) rs.close();
			if (ps != null) ps.close();
		}
	}
	
	private PreparedStatement montaQuery(Date inicio, Date fim) throws SQLException {
		PreparedStatement ps = connection.prepareStatement(
			"SELECT * FROM clientes " +
			"WHERE " +
			"	CAST( " +
			"		EXTRACT(DAY FROM nascimento) || '.' || " +
			"		EXTRACT(MONTH FROM nascimento) || '.2016' " +
			"		AS DATE " +
			"	) BETWEEN ? AND ? " +
			"ORDER BY nome"
		);
		ps.setDate(1, inicio);
		ps.setDate(2, fim);
		return ps;
	}
	
	private Date dataCampo(JFormattedTextField campo) throws ParseException {
		java.util.Date data = DateFormat.getDateInstance().parse(campo.getText() + "/2016");
		return new Date(data.getTime());
	}
	
	private class BotaoOk implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				gerarRelatorio();
			} 
			catch (ParseException pex) {
				showMessageDialog(null, "Preencha corretamente os pares dia/mês, ex.: 01/07.",
						"Atenção!", WARNING_MESSAGE);
			}
			catch (Exception ex) {
				ex.printStackTrace();
				showMessageDialog(null, ex.getMessage(), "ERRO AO GERAR RELATÓRIO", ERROR_MESSAGE);
			}

			dispose();
		}
	}
	
	private class BotaoCancelar implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			dispose();
		}
	}

}
