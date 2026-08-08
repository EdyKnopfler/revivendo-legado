package br.com.gamecursos.estoque.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.gamecursos.estoque.model.Fornecedor;
import br.com.gamecursos.estoque.model.Produto;

public class ProdutoDao {
	
	private static final String select =
		"SELECT " +
		"   f.*, " +
		"   p.id_produto AS prod_id, " +
		"   p.codigo AS prod_codigo, " +
		"   p.nome AS prod_nome, " +
		"   p.preco_custo_unit AS prod_custo " +
		"FROM produtos p " +
		"LEFT JOIN fornecedores f " +
		"ON p.id_fornecedor = f.id_fornecedor ";
	
	private Connection connection;
	
	public ProdutoDao(Connection connection) {
		this.connection = connection;
	}
	
	public void incluir(Produto p) throws ConflitoConcorrenciaException {
		try {
			long id = new GeradorDao(connection).obter("GER_PRODUTOS");
			PreparedStatement ps = connection.prepareStatement(
				"INSERT INTO produtos (" +
				"   id_fornecedor, codigo, nome, preco_custo_unit, id_produto, quantidade" +
				") VALUES (?, ?, ?, ?, ?, 0)"  // Importante começar com zero!
			);
			
			objParaPs(p, ps);
			ps.setLong(5, id);
			ps.executeUpdate();
			ps.close();
			p.setId(id);
		}
		catch (SQLException e) {
			e.printStackTrace();
			DaoException.relancar("Erro ao incluir produto", e);
		}
	}

	public void alterar(Produto p) throws ConflitoConcorrenciaException {
		try {
			PreparedStatement ps = connection.prepareStatement(
				"UPDATE produtos SET" +
				"   id_fornecedor = ?, codigo = ?, nome = ?, preco_custo_unit = ? " +
				"WHERE id_produto = ?"
			);
			
			objParaPs(p, ps);	
			ps.setLong(5, p.getId());
			ps.executeUpdate();
			ps.close();
		} 
		catch (SQLException e) {
			e.printStackTrace();
			DaoException.relancar("Erro ao alterar produto", e);
		}
	}

	public void excluir(Produto p) throws ConflitoConcorrenciaException {
		try {
			PreparedStatement ps = connection.prepareStatement(
				"DELETE FROM produtos WHERE id_produto = ?"
			);
			
			ps.setLong(1, p.getId());
			ps.executeUpdate();
		} 
		catch (SQLException e) {
			e.printStackTrace();
			DaoException.relancar("Erro ao excluir produto", e);
		}
	}
	
	public Produto porId(int id) {
		try {
			PreparedStatement ps = connection.prepareStatement(
				select + " WHERE p.id_produto = ?"
			);
			
			ps.setInt(1, id);
			
			ResultSet rs = ps.executeQuery();
			
			try {
				if (rs.next())
					return rsParaObj(rs);
				
				return null;
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
	
	public List<Produto> porNome(String nome) {
		try {
			PreparedStatement ps = connection.prepareStatement(
					select + " WHERE p.nome LIKE ?"
					);
			
			ps.setString(1, "%" + nome + "%");
			
			ResultSet rs = ps.executeQuery();
			
			try {
				List<Produto> lista = new ArrayList<Produto>();
				
				while (rs.next())
					lista.add(rsParaObj(rs));
				
				return lista;
			}
			finally {
				rs.close();
				ps.close();
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
			throw new DaoException("Erro ao consultar produtos:\n\n" + e.getMessage());
		}
	}
	
	public Produto porCodigo(String codigo) {
		try {
			PreparedStatement ps = connection.prepareStatement(
				select + " WHERE p.codigo LIKE ?"
			);
			
			ps.setString(1, codigo);
			
			ResultSet rs = ps.executeQuery();
			
			try {
				if (rs.next())
					return rsParaObj(rs);
				
				return null;
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

	public List<Produto> todos() {
		try {
			PreparedStatement ps = connection.prepareStatement(
				select + " ORDER BY p.nome"
			);
			
			List<Produto> lista = new ArrayList<Produto>();
			ResultSet rs = ps.executeQuery();
			
			try {
				while (rs.next())
					lista.add(rsParaObj(rs));
				
				return lista;
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

	void objParaPs(Produto p, PreparedStatement ps) throws SQLException {
		ps.setLong(1, p.getFornecedor().getId());
		ps.setString(2, p.getCodigo());
		ps.setString(3, p.getNome());
		ps.setDouble(4, p.getCustoUnitario());
	}
	
	Produto rsParaObj(ResultSet rs) throws SQLException {
		Produto prod = new Produto();
		Fornecedor forn = new FornecedorDao(null).rsParaObj(rs);
		prod.setFornecedor(forn);
		prod.setId(rs.getLong("prod_id"));
		prod.setCodigo(rs.getString("prod_codigo"));
		prod.setNome(rs.getString("prod_nome"));
		prod.setCustoUnitario(rs.getDouble("prod_custo"));
		return prod;
	}
	
}
