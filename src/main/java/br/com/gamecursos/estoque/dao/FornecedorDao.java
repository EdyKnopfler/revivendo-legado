package br.com.gamecursos.estoque.dao;

import br.com.gamecursos.estoque.model.Fornecedor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FornecedorDao {
	
	private Connection connection;
	
	public FornecedorDao(Connection connection) {
		this.connection = connection;
	}
	
	public void incluir(Fornecedor f) throws ConflitoConcorrenciaException {
		try {
			long id = new GeradorDao(connection).obter("GER_FORNECEDORES");
			PreparedStatement ps = connection.prepareStatement(
				"INSERT INTO fornecedores (" +
				"   nome, telefone_1, telefone_2, endereco, bairro, cidade, cep, estado, " +
				"   email, cnpj, inscricao_estadual, id_fornecedor" +
				") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
			);

			try {
				objParaPs(f, ps);
				ps.setLong(12, id);
				ps.executeUpdate();
				f.setId(id);
			}
			finally {
				ps.close();
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
			DaoException.relancar("Erro ao incluir fornecedor", e);
		}
	}

	public void alterar(Fornecedor f) throws ConflitoConcorrenciaException {
		try {
			PreparedStatement ps = connection.prepareStatement(
				"UPDATE fornecedores SET" +
				"   nome = ?, telefone_1 = ?, telefone_2 = ?, endereco = ?, bairro = ?, " +
				"   cidade = ?, cep = ?, estado = ?, email = ?, cnpj = ?, " +
				"   inscricao_estadual = ? " +
				"WHERE id_fornecedor = ?"
			);

			try {
				objParaPs(f, ps);
				ps.setLong(12, f.getId());
				ps.executeUpdate();
			}
			finally {
				ps.close();
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
			DaoException.relancar("Erro ao alterar fornecedor", e);
		}
	}

	public void excluir(Fornecedor f) throws ConflitoConcorrenciaException {
		try {
			PreparedStatement ps = connection.prepareStatement(
				"DELETE FROM fornecedores WHERE id_fornecedor = ?"
			);

			try {
				ps.setLong(1, f.getId());
				ps.executeUpdate();
			}
			finally {
				ps.close();
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
			DaoException.relancar("Erro ao excluir fornecedor", e);
		}
	}

	public Fornecedor porId(int id) {
		try {
			PreparedStatement ps = connection.prepareStatement(
				"SELECT * FROM fornecedores WHERE id_fornecedor = ?"
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
			throw new DaoException("Erro ao consultar fornecedor:\n\n" + e.getMessage());
		}
	}
	
	public List<Fornecedor> porNome(String nome) {
		try {
			PreparedStatement ps = connection.prepareStatement(
				"SELECT * FROM fornecedores WHERE nome LIKE ? ORDER BY nome"
			);
			
			ps.setString(1, "%" + nome + "%");
			
			ResultSet rs = ps.executeQuery();
			
			try {
				List<Fornecedor> lista = new ArrayList<Fornecedor>();
				
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
			throw new DaoException("Erro ao consultar fornecedores:\n\n" + e.getMessage());
		}
	}

	
	public List<Fornecedor> todos() {
		try {
			PreparedStatement ps = connection.prepareStatement(
				"SELECT * FROM fornecedores ORDER BY nome"
			);
			ResultSet rs = ps.executeQuery();
			
			try {
				List<Fornecedor> lista = new ArrayList<Fornecedor>();
				
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
			throw new DaoException("Erro ao consultar fornecedores:\n\n" + e.getMessage());
		}
	}
	
	void objParaPs(Fornecedor f, PreparedStatement ps) throws SQLException {
		ps.setString(1, f.getNome());
		ps.setString(2, f.getTelefone1());
		ps.setString(3, f.getTelefone2());
		ps.setString(4, f.getEndereco());
		ps.setString(5, f.getBairro());
		ps.setString(6, f.getCidade());
		ps.setString(7, f.getCep());
		ps.setString(8, f.getEstado());
		ps.setString(9, f.getEmail());
		ps.setString(10, f.getCnpj());
		ps.setString(11, f.getInscricaoEstadual());
	}
	
	Fornecedor rsParaObj(ResultSet rs) throws SQLException {
		Fornecedor forn = new Fornecedor();
		forn.setId(rs.getLong("id_fornecedor"));
		forn.setNome(rs.getString("nome"));
		forn.setTelefone1(rs.getString("telefone_1"));
		forn.setTelefone2(rs.getString("telefone_2"));
		forn.setEndereco(rs.getString("endereco"));
		forn.setBairro(rs.getString("bairro"));
		forn.setCidade(rs.getString("cidade"));
		forn.setCep(rs.getString("cep"));
		forn.setEstado(rs.getString("estado"));
		forn.setEmail(rs.getString("email"));
		forn.setCnpj(rs.getString("cnpj"));
		forn.setInscricaoEstadual(rs.getString("inscricao_estadual"));
		return forn;
	}

}