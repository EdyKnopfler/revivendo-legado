package br.com.gamecursos.estoque;

import java.sql.Connection;

import br.com.gamecursos.estoque.gui.MenuForm;
import br.com.gamecursos.util.Configuracao;

import static javax.swing.JOptionPane.*;

public class Aplicacao {
	
	private Connection connection;
	
	public Aplicacao() {
		try {
			conectar();
		}
		catch (Exception e) {
			e.printStackTrace();
			showMessageDialog(null, "Erro ao conectar-se ao banco de dados: \n\n" +
					e.getMessage(), "Sistema de Estoque", ERROR_MESSAGE);
		}
	}
	
	public void conectar() throws Exception {
		Configuracao config = new Configuracao();
		config.carregar("pedidos.properties");
		connection = config.conectar();
	}
	
	public void desconectar() throws Exception {
		connection.close();
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	public void menu() {
		new MenuForm(this).setVisible(true);
	}

	public static void main(String[] args) {
		new Aplicacao().menu();
	}


}
