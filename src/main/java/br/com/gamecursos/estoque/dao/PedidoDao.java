package br.com.gamecursos.estoque.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.gamecursos.estoque.model.Cliente;
import br.com.gamecursos.estoque.model.Pedido;

public class PedidoDao {
	
	private static final String select =
			"SELECT " +
			"   c.*, " +
			"   p.id_pedido AS ped_id, " +
			"   p.data AS ped_data, " +
			"   p.total AS ped_total " +
			"FROM pedidos p " +
			"LEFT JOIN clientes c " +
			"ON p.id_cliente = c.id_cliente ";

	private Connection connection;
	
	public PedidoDao(Connection connection) {
		this.connection = connection;
	}

	public void incluir(Pedido p) throws ConflitoConcorrenciaException {
		try {
			long id = new GeradorDao(connection).obter("GER_PEDIDOS");
			PreparedStatement ps = connection.prepareStatement(
				"INSERT INTO pedidos (" +
				"   id_cliente, data, total, id_pedido" +
				") VALUES (?, ?, ?, ?)"
			);

			try {
				objParaPs(p, ps);
				ps.setLong(4, id);
				ps.executeUpdate();
				p.setId(id);
			}
			finally {
				ps.close();
			}

			new ItemPedidoDao(connection).incluirItens(p);
		}
		catch (SQLException e) {
			e.printStackTrace();
			DaoException.relancar("Erro ao incluir pedido", e);
		}
	}

	public void alterar(Pedido p) throws ConflitoConcorrenciaException {
		try {
			PreparedStatement ps = connection.prepareStatement(
				"UPDATE pedidos SET" +
				"   id_cliente = ?, data = ?, total = ? " +
				"WHERE id_pedido = ?"
			);

			try {
				objParaPs(p, ps);
				ps.setLong(4, p.getId());
				ps.executeUpdate();
			}
			finally {
				ps.close();
			}

			ItemPedidoDao itemDao = new ItemPedidoDao(connection);
			itemDao.excluirItens(p);
			itemDao.incluirItens(p);
		}
		catch (SQLException e) {
			e.printStackTrace();
			DaoException.relancar("Erro ao alterar pedido", e);
		}
	}

	public void excluir(Pedido p) throws ConflitoConcorrenciaException {
		try {
			PreparedStatement ps = connection.prepareStatement(
				"DELETE FROM pedidos WHERE id_pedido = ?"
			);

			try {
				ps.setLong(1, p.getId());
				ps.executeUpdate();
			}
			finally {
				ps.close();
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
			DaoException.relancar("Erro ao excluir pedido", e);
		}
	}

	public Pedido porId(long id) {
		try {
			PreparedStatement ps = connection.prepareStatement(
				select + " WHERE p.id_pedido = ?"
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
			throw new DaoException("Erro ao consultar pedido:\n\n" + e.getMessage());
		}
	}
	
	void objParaPs(Pedido p, PreparedStatement ps) throws SQLException {
		Date data = new Date(p.getData().getTime());
		ps.setLong(1, p.getCliente().getId());
		ps.setDate(2, data);
		ps.setDouble(3, p.getTotal());
	}
	
	Pedido rsParaObj(ResultSet rs) throws SQLException {
		Cliente cli = new ClienteDao(null).rsParaObj(rs);
		ItemPedidoDao itemDao = new ItemPedidoDao(connection);
		
		Pedido ped = new Pedido(itemDao);
		ped.setId(rs.getLong("ped_id"));
		ped.setData(rs.getDate("ped_data"));
		ped.setTotal(rs.getDouble("ped_total"));
		ped.setCliente(cli);
		
		return ped;
	}

	public List<Pedido> todos() {
		return executarSelect(select + " ORDER BY p.data, c.nome", null);
	}

	public List<Pedido> porData(Date data) {
		return executarSelect(select + " WHERE p.data = ? ORDER BY c.nome", 
				new Object[] {data});
	}

	public List<Pedido> porNome(String nome) {
		return executarSelect(select + " WHERE c.nome LIKE ? ORDER BY c.nome, p.data",
				new Object[] {"%" + nome + "%"});
	}
	
	private List<Pedido> executarSelect(String sql, Object[] parametros) {
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			
			if (parametros != null)
				for (int i = 0; i < parametros.length; i++)
					ps.setObject(i + 1, parametros[i]);
			
			ResultSet rs = ps.executeQuery();
			
			try {
				List<Pedido> lista = new ArrayList<Pedido>();
				
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
			throw new DaoException("Erro ao consultar pedidos:\n\n" + e.getMessage());
		}

	}

}
