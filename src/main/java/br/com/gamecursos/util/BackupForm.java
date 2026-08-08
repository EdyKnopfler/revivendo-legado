package br.com.gamecursos.util;

import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.swing.*;

import org.firebirdsql.management.FBStreamingBackupManager;

import br.com.gamecursos.estoque.Aplicacao;

import static javax.swing.JOptionPane.*;

public class BackupForm extends JDialog {

	private static final long serialVersionUID = 1L;

	private Aplicacao app;
	private JTextField fdb, fbk;
	private Configuracao config;
	
	public BackupForm(Aplicacao app) {
		this.app = app;
		setTitle("Backup do Banco de Dados");
		setSize(550, 200);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModal(true);
		
		fdb = new JTextField(30);
		fbk = new JTextField(30);
		
		config = new Configuracao();
		try {
			config.carregar("pedidos.properties");
		} 
		catch (Exception e) {
			e.printStackTrace();
			showMessageDialog(null, e.getMessage(), "ERRO AO ABRIR BACKUP", ERROR_MESSAGE);
		}
		fdb.setText(config.getArquivo());
		fbk.setText(config.getBackup());
		
		JPanel campos = criarPainelCampos();
		JPanel botoes = criarPainelBotoes();
		
		setLayout(new BorderLayout());
		Container c = getContentPane();
		c.add(campos, BorderLayout.CENTER);
		c.add(botoes, BorderLayout.SOUTH);
		
	}
	
	private JPanel criarPainelCampos() {
		JPanel campos = new JPanel();
		
		campos.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5,0,5,5);
		gbc.gridy = 0;
		gbc.gridx = 0;
		campos.add(new JLabel("Banco de dados:"), gbc);
		gbc.gridx = 1;
		campos.add(fdb, gbc);
		gbc.gridy = 1;
		gbc.gridx = 0;
		campos.add(new JLabel("Backup (arquivo local):"), gbc);
		gbc.gridx = 1;
		campos.add(fbk, gbc);
		
		return campos;
	}
	
	private JPanel criarPainelBotoes() {
		JPanel botoes = new JPanel();
		JButton backup = new JButton("Backup");
		JButton restaurar = new JButton("Restaurar");
		JButton fechar = new JButton("Fechar");
		backup.addActionListener(new AcaoBackup());
		restaurar.addActionListener(new AcaoRestaurar());
		fechar.addActionListener(new AcaoFechar());
		botoes.add(backup);
		botoes.add(restaurar);
		botoes.add(fechar);
		return botoes;
	}
	
	private class AcaoBackup implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			FBStreamingBackupManager backup = new FBStreamingBackupManager();
			backup.setUser(config.getUsuario());
			backup.setPassword(config.getSenha());
			backup.setHost(config.getIp());
			backup.setPort(3050);
			backup.setDatabase(fdb.getText());

			try (FileOutputStream saida = new FileOutputStream(fbk.getText())) {
				backup.setBackupOutputStream(saida);
				backup.backupDatabase();
				showMessageDialog(null, "Backup efetuado com sucesso.", "Backup", INFORMATION_MESSAGE);
				dispose();
			}
			catch (Exception ex) {
				ex.printStackTrace();
				showMessageDialog(null, ex.getMessage(), "ERRO AO INICIAR BACKUP", ERROR_MESSAGE);
			}
		}
	}
	
	private class AcaoRestaurar implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (showConfirmDialog(null, "Os dados atuais serão perdidos! Convém realizar um backup " +
					"antes de restaurar.\n\nContinua?", "ATENÇÃO!!!", YES_NO_OPTION, WARNING_MESSAGE)
				== NO_OPTION)
				return;
			
			FBStreamingBackupManager restaurar = new FBStreamingBackupManager();
			restaurar.setUser(config.getUsuario());
			restaurar.setPassword(config.getSenha());
			restaurar.setHost(config.getIp());
			restaurar.setPort(3050);
			restaurar.setDatabase(fdb.getText());
			restaurar.setRestoreReplace(true);

			try (FileInputStream entrada = new FileInputStream(fbk.getText())) {
				restaurar.setRestoreInputStream(entrada);
				app.desconectar();
				restaurar.restoreDatabase();
				showMessageDialog(null, "Restauração efetuada com sucesso.", "Backup", INFORMATION_MESSAGE);
				dispose();
			}
			catch (Exception ex) {
				ex.printStackTrace();
				showMessageDialog(null, ex.getMessage(), "ERRO AO INICIAR RESTAURAÇÃO", ERROR_MESSAGE);
			}
			finally {
				try { 
					app.conectar(); 
				} 
				catch (Exception ex) {
					ex.printStackTrace();
					showMessageDialog(null, ex.getMessage(), "ERRO AO RECONECTAR", ERROR_MESSAGE);
				}
			}
		}
	}
	
	private class AcaoFechar implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			dispose();
		}
	}

}
