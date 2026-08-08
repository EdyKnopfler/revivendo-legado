package br.com.gamecursos.estoque.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.gamecursos.estoque.model.Fornecedor;
import br.com.gamecursos.estoque.model.Compra;

public class CompraDao {
	
	private static final String select =
			"SELECT " +
			"   f.*, " +
			"   c.id_compra AS com_id, " +
			"   c.data AS com_data, " +
			"   c.nota AS com_nota, " +
			"   c.total AS com_total " +
			"FROM compras c " +
			"LEFT JOIN fornecedores f " +
			"ON c.id_fornecedor = f.id_fornecedor ";

	private Connection connection;
	
	public CompraDao(Connection connection) {
		this.connection = connection;
	}

	public void incluir(Compra c) throws ConflitoConcorrenciaException {
		try {
			long id = new GeradorDao(connection).obter("GER_ITENS_COMPRA");
			PreparedStatement ps = connection.prepareStatement(
				"INSERT INTO compras (" +
				"   id_fornecedor, data, nota, total, id_compra" +
				") VALUES (?, ?, ?, ?, ?)"
			);

			try {
				objParaPs(c, ps);
				ps.setLong(5, id);
				ps.executeUpdate();
				c.setId(id);
			}
			finally {
				ps.close();
			}

			new ItemCompraDao(connection).incluirItens(c);
		}
		catch (SQLException e) {
			e.printStackTrace();
			DaoException.relancar("Erro ao incluir compra", e);
		}
	}

	public void alterar(Compra c) throws ConflitoConcorrenciaException {
		try {
			PreparedStatement ps = connection.prepareStatement(
				"UPDATE compras SET" +
				"   id_fornecedor = ?, data = ?, nota = ?, total = ? " +
				"WHERE id_compra = ?"
			);

			try {
				objParaPs(c, ps);
				ps.setLong(5, c.getId());
				ps.executeUpdate();
			}
			finally {
				ps.close();
			}

			ItemCompraDao itemDao = new ItemCompraDao(connection);
			itemDao.excluirItens(c);
			itemDao.incluirItens(c);
		}
		catch (SQLException e) {
			e.printStackTrace();
			DaoException.relancar("Erro ao alterar compra", e);
		}
	}

	public void excluir(Compra c) throws ConflitoConcorrenciaException {
		try {
			PreparedStatement ps = connection.prepareStatement(
				"DELETE FROM compras WHERE id_compra = ?"
			);

			try {
				ps.setLong(1, c.getId());
				ps.executeUpdate();
			}
			finally {
				ps.close();
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
			DaoException.relancar("Erro ao excluir compra", e);
		}
	}

	public Compra porId(long id) {
		try {
			PreparedStatement ps = connection.prepareStatement(
				select + " WHERE c.id_compra = ?"
			);
			
			ps.setLong(1, id);
			
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
			throw new DaoException("Erro ao consultar compra:\n\n" + e.getMessage());
		}
	}
	
	void objParaPs(Compra c, PreparedStatement ps) throws SQLException {
		Date data = new Date(c.getData().getTime());
		ps.setLong(1, c.getFornecedor().getId());
		ps.setDate(2, data);
		ps.setString(3, c.getNota());
		ps.setDouble(4, c.getTotal());
	}
	
	Compra rsParaObj(ResultSet rs) throws SQLException {
		Fornecedor forn = new FornecedorDao(null).rsParaObj(rs);
		ItemCompraDao itemDao = new ItemCompraDao(connection);
		
		Compra c = new Compra(itemDao);
		c.setId(rs.getLong("com_id"));
		c.setData(rs.getDate("com_data"));
		c.setNota(rs.getString("com_nota"));
		c.setTotal(rs.getDouble("com_total"));
		c.setFornecedor(forn);
		
		return c;
	}

	public List<Compra> todos() {
		return executarSelect(select + " ORDER BY c.data, f.nome", null);
	}

	public List<Compra> porData(Date data) {
		return executarSelect(select + " WHERE c.data = ? ORDER BY f.nome", 
				new Object[] {data});
	}

	public List<Compra> porNome(String nome) {
		return executarSelect(select + " WHERE f.nome LIKE ? ORDER BY f.nome, c.data",
				new Object[] {"%" + nome + "%"});
	}
	
	private List<Compra> executarSelect(String sql, Object[] parametros) {
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			
			if (parametros != null)
				for (int i = 0; i < parametros.length; i++)
					ps.setObject(i + 1, parametros[i]);
			
			ResultSet rs = ps.executeQuery();
			
			try {
				List<Compra> lista = new ArrayList<Compra>();
				
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
			throw new DaoException("Erro ao consultar compras:\n\n" + e.getMessage());
		}

	}

}
