package br.com.gamecursos.estoque.gui.relatorios;

import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import javax.swing.*;
import javax.swing.text.MaskFormatter;

public abstract class RelPeriodo extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private JFormattedTextField de;
	private JFormattedTextField ate;
	private JButton ok;
	
	@SuppressWarnings("deprecation")
	public RelPeriodo() {
		setTitle("Defina um título!");
		setModal(true);
		setSize(300, 120);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		try {
			de = new JFormattedTextField(new MaskFormatter("##/##/####"));
			ate = new JFormattedTextField(new MaskFormatter("##/##/####"));
		} catch (ParseException e) {}
		
		JPanel campos = new JPanel();
		campos.setLayout(new GridLayout(2, 2));
		campos.add(new JLabel("De:"));
		campos.add(de);
		campos.add(new JLabel("Até:"));
		campos.add(ate);
		
		ok = new JButton("OK");
		
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
		
		Date primeira = new Date();
		primeira.setDate(1);
		de.setText(DateFormat.getDateInstance().format(primeira));
		
		Date ultima = new Date();
		int mes = ultima.getMonth();
		ultima.setDate(31);
		while (ultima.getMonth() > mes) ultima.setDate(ultima.getDate() - 1);
		ate.setText(DateFormat.getDateInstance().format(ultima));
		
		ActionListener botaoOk = new BotaoOk();
		de.addActionListener(botaoOk);
		ate.addActionListener(botaoOk);
		ok.addActionListener(botaoOk);
		cancelar.addActionListener(new BotaoCancelar());
	}
	
	protected abstract void cliqueOk();
	
	protected Date getDe() throws Exception {
		return getDataCampo(de);
	}
	
	protected Date getAte() throws Exception {
		return getDataCampo(ate);
	}
	
	private Date getDataCampo(JTextField campo) throws Exception {
		try {
			return DateFormat.getDateInstance().parse(campo.getText());
		}
		catch (ParseException e) {
			campo.requestFocus();
			throw new Exception("Data inválida: " + campo.getText());
		}
	}
	
	private class BotaoOk implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			cliqueOk();
		}
	}

	private class BotaoCancelar implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			dispose();
		}
	}

}
