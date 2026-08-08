package br.com.gamecursos.estoque.dao;

import br.com.gamecursos.estoque.model.Produto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EstoqueDao {
	
	private Connection connection;
	
	public EstoqueDao(Connection connection) {
		this.connection = connection;
	}
	
	public void entrada(Produto p, int quantidade) throws ConflitoConcorrenciaException {
		try {
			PreparedStatement ps = connection.prepareStatement(
				"UPDATE produtos SET quantidade = quantidade + ?" +
				"WHERE id_produto = ?"
			);

			try {
				ps.setInt(1, quantidade);
				ps.setLong(2, p.getId());
				ps.executeUpdate();
			}
			finally {
				ps.close();
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
			DaoException.relancar("Erro ao entrar produto", e);
		}
	}

	public void saida(Produto p, int quantidade) throws ConflitoConcorrenciaException {
		try {
			PreparedStatement ps = connection.prepareStatement(
				"UPDATE produtos SET quantidade = quantidade - ?" +
				"WHERE id_produto = ?"
			);

			try {
				ps.setInt(1, quantidade);
				ps.setLong(2, p.getId());
				ps.executeUpdate();
			}
			finally {
				ps.close();
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
			DaoException.relancar("Erro ao sair produto", e);
		}
	}
	
	public int quantosTem(Produto p) {
		try {
			PreparedStatement ps = connection.prepareStatement(
				"SELECT quantidade FROM produtos WHERE id_produto = ?"
			);
			
			ps.setLong(1, p.getId());
			
			ResultSet rs = ps.executeQuery();
			
			try {
				if (rs.next())
					return rs.getInt("quantidade");
				
				throw new DaoException("Produto não encontrado!");
			}
			finally {
				rs.close();
				ps.close();
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
			throw new DaoException("Erro ao consultar produto:\n\n" + e.getMessage());
		}
	}

}
