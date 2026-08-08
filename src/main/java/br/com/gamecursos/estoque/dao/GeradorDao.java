package br.com.gamecursos.estoque.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GeradorDao {

	private Connection connection;
	
	public GeradorDao(Connection connection) {
		this.connection = connection;
	}

	public long obter(String ger) throws SQLException {
		Statement st = connection.createStatement();
		ResultSet rs = st.executeQuery(
			"SELECT GEN_ID(" + ger + ", 1) AS valor FROM RDB$DATABASE"
		);
		try {
			rs.next();
			return rs.getLong(1); 
		}
		finally {
			rs.close();
			st.close();
		}
	}

}
