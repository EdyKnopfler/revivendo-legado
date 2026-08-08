package br.com.gamecursos.estoque.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.gamecursos.estoque.model.Compra;
import br.com.gamecursos.estoque.model.ItemCompra;
import br.com.gamecursos.estoque.model.Produto;

public class ItemCompraDao {

	private static final String select =
			"SELECT " +
			"   p.id_produto, p.codigo, p.nome, " +
			"   i.id_item AS item_id, " +
			"   i.preco_unitario AS item_preco, " +
			"   i.quantidade AS item_quant " +
			"FROM itens_compra i " +
			"LEFT JOIN produtos p " +
			"ON i.id_produto = p.id_produto ";

	private Connection connection;
	
	public ItemCompraDao(Connection connection) {
		this.connection = connection;
	}

	public void incluirItens(Compra c) throws ConflitoConcorrenciaException {
		for (ItemCompra i: c.getItens())
			incluir(i, c.getId());
	}

	public void excluirItens(Compra c) throws ConflitoConcorrenciaException {
		try {
			PreparedStatement ps = connection.prepareStatement(
				"DELETE FROM itens_compra WHERE id_compra = ?"
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
			DaoException.relancar("Erro ao excluir itens da compra", e);
		}
	}

	private void incluir(ItemCompra i, long idCompra) throws ConflitoConcorrenciaException {
		try {
			long id = new GeradorDao(connection).obter("GER_ITENS_COMPRA");
			PreparedStatement ps = connection.prepareStatement(
				"INSERT INTO itens_compra (" +
				"   id_compra, id_produto, preco_unitario, quantidade, id_item" +
				") VALUES (?, ?, ?, ?, ?)"
			);

			try {
				ps.setLong(1, idCompra);
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
			DaoException.relancar("Erro ao incluir item da compra", e);
		}
	}

	/**
	 * OBS: Objeto Produto de cada item vai somente com ID, Nome e Código!
	 */
	public List<ItemCompra> pegarItens(Compra c) {
		try {
			PreparedStatement ps = connection.prepareStatement(
				select + " WHERE i.id_compra = ?"
			);
			
			ps.setLong(1, c.getId());
			
			ResultSet rs = ps.executeQuery();
			
			try {
				List<ItemCompra> lista = new ArrayList<ItemCompra>();
				
				while (rs.next()) {
					ItemCompra i = new ItemCompra();
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
			throw new DaoException("Erro ao consultar itens da compra:\n\n" + e.getMessage());
		}
	}
	
}
