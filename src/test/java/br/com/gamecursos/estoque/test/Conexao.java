package br.com.gamecursos.estoque.test;

import java.sql.Connection;
import java.sql.DriverManager;

public class Conexao {

	public static Connection abrir() throws RuntimeException {
		try {
			String host = getenv("FIREBIRD_TEST_HOST", "localhost");
			String arquivo = getenv("FIREBIRD_TEST_DATABASE", "/firebird/data/estoque.fdb");
			String usuario = getenv("FIREBIRD_TEST_USER", System.getenv("FIREBIRD_USER"));
			String senha = getenv("FIREBIRD_TEST_PASSWORD", System.getenv("FIREBIRD_PASSWORD"));

			if (usuario == null || senha == null)
				throw new IllegalStateException(
					"Defina FIREBIRD_USER/FIREBIRD_PASSWORD (ou FIREBIRD_TEST_USER/FIREBIRD_TEST_PASSWORD) " +
					"com as credenciais do banco de teste, ex.: as mesmas do .env do docker-compose.");

			Class.forName("org.firebirdsql.jdbc.FBDriver");
			Connection connection = DriverManager.getConnection(
				"jdbc:firebirdsql:" + host + "/3050:" + arquivo + "?lc_ctype=UTF8",
				usuario, senha
			);
			connection.setAutoCommit(false);
			return connection;
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

	private static String getenv(String chave, String padrao) {
		String valor = System.getenv(chave);
		return valor != null ? valor : padrao;
	}
	
}
