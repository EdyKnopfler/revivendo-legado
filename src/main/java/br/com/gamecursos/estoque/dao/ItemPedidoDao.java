package br.com.gamecursos.estoque.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.gamecursos.estoque.model.ItemPedido;
import br.com.gamecursos.estoque.model.Pedido;
import br.com.gamecursos.estoque.model.Produto;

public class ItemPedidoDao {

	private static final String select =
			"SELECT " +
			"   p.id_produto, p.codigo, p.nome, " +
			"   i.id_item AS item_id, " +
			"   i.preco_unitario AS item_preco, " +
			"   i.quantidade AS item_quant " +
			"FROM itens_pedido i " +
			"LEFT JOIN produtos p " +
			"ON i.id_produto = p.id_produto ";

	private Connection connection;
	
	public ItemPedidoDao(Connection connection) {
		this.connection = connection;
	}

	public void incluirItens(Pedido p) throws ConflitoConcorrenciaException {
		for (ItemPedido i: p.getItens())
			incluir(i, p.getId());
	}

	public void excluirItens(Pedido p) throws ConflitoConcorrenciaException {
		try {
			PreparedStatement ps = connection.prepareStatement(
				"DELETE FROM itens_pedido WHERE id_pedido = ?"
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
			DaoException.relancar("Erro ao excluir itens do pedido", e);
		}
	}

	private void incluir(ItemPedido i, long idPedido) throws ConflitoConcorrenciaException {
		try {
			long id = new GeradorDao(connection).obter("GER_ITENS_PEDIDO");
			PreparedStatement ps = connection.prepareStatement(
				"INSERT INTO itens_pedido (" +
				"   id_pedido, id_produto, preco_unitario, quantidade, id_item" +
				") VALUES (?, ?, ?, ?, ?)"
			);

			try {
				ps.setLong(1, idPedido);
				ps.setLong(2, i.getProduto().getId());
				ps.setDouble(3, i.getPrecoUnitario());
				ps.setInt(4, i.getQuantidade());
				ps.setLong(5, id);
				ps.executeUpdate();
				i.setId(id);
			}
			finally {
				ps.close();
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
			DaoException.relancar("Erro ao incluir item do pedido", e);
		}
	}

	/**
	 * OBS: Objeto Produto de cada item vai somente com ID, Nome e Código!
	 */
	public List<ItemPedido> pegarItens(Pedido p) {
		try {
			PreparedStatement ps = connection.prepareStatement(
				select + " WHERE i.id_pedido = ?"
			);
			
			ps.setLong(1, p.getId());
			
			ResultSet rs = ps.executeQuery();
			
			try {
				List<ItemPedido> lista = new ArrayList<ItemPedido>();
				
				while (rs.next()) {
					ItemPedido i = new ItemPedido();
					Produto prod = new Produto();
					
					i.setId(rs.getLong("item_id"));
					i.setPrecoUnitario(rs.getDouble("item_preco"));
					i.setQuantidade(rs.getInt("item_quant"));
					i.setProduto(prod);
					
					prod.setId(rs.getLong("id_produto"));
					prod.setCodigo(rs.getString("codigo"));
					prod.setNome(rs.getString("nome"));
					
					lista.add(i);
				}
				
				return lista;
			}
			finally {
				rs.close();
				ps.close();
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
			throw new DaoException("Erro ao consultar itens do pedido:\n\n" + e.getMessage());
		}
	}
	
}
