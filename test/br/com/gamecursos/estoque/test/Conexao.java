package br.com.gamecursos.estoque.test;

import java.sql.Connection;
import java.sql.DriverManager;

public class Conexao {

	public static Connection abrir() throws RuntimeException {
		try {
			Class.forName("org.firebirdsql.jdbc.FBDriver");
			Connection connection = DriverManager.getConnection(
				"jdbc:firebirdsql:localhost/3050:/home/ederson/Projetos/Aurelio/BD/PEDIDOS.FDB" +
				"?lc_ctype=WIN1252",
				"SYSDBA", "masterkey"
			);
			connection.setAutoCommit(false);
			return connection;
		} 
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}
	
}
