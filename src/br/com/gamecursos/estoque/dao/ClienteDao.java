package br.com.gamecursos.estoque.dao;

import br.com.gamecursos.estoque.model.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;

public class ClienteDao {
	
	private Connection connection;
	
	public ClienteDao(Connection connection) {
		this.connection = connection;
	}
	
	public void incluir(Cliente c) throws ConflitoConcorrenciaException {
		try {
			long id = new GeradorDao(connection).obter("GER_CLIENTES");
			PreparedStatement ps = connection.prepareStatement(
				"INSERT INTO clientes (" +
				"   nome, nascimento, cpf, rg, telefone, celular, " +
				"   endereco, bairro, cidade, cep, estado, email, " +
				"   endereco_comercial, cnpj, referencias, id_cliente " +
				") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
			);
			
			objParaPs(c, ps);
			ps.setLong(16, id);
			ps.executeUpdate();
			ps.close();
			c.setId(id);
		} 
		catch (SQLException e) {
			e.printStackTrace();
			DaoException.relancar("Erro ao incluir cliente", e);
		}
	}

	public void alterar(Cliente c) throws ConflitoConcorrenciaException {
		try {
			PreparedStatement ps = connection.prepareStatement(
				"UPDATE clientes SET" +
				"   nome = ?, nascimento = ?, cpf = ?, rg = ?, telefone = ?, " +
				"   celular = ?, endereco = ?, bairro = ?, cidade = ?, cep = ?, estado = ?, " +
				"   email = ?, endereco_comercial = ?, cnpj = ?, referencias = ? " +
				"WHERE id_cliente = ?"
			);
			
			objParaPs(c, ps);	
			ps.setLong(16, c.getId());
			ps.executeUpdate();
			ps.close();
		} 
		catch (SQLException e) {
			e.printStackTrace();
			DaoException.relancar("Erro ao alterar cliente", e);
		}
	}

	public void excluir(Cliente c) throws ConflitoConcorrenciaException {
		try {
			PreparedStatement ps = connection.prepareStatement(
				"DELETE FROM clientes WHERE id_cliente = ?"
			);
			
			ps.setLong(1, c.getId());
			ps.executeUpdate();
		} 
		catch (SQLException e) {
			e.printStackTrace();
			DaoException.relancar("Erro ao excluir cliente", e);
		}
	}

	public Cliente porId(int id) {
		try {
			PreparedStatement ps = connection.prepareStatement(
				"SELECT * FROM clientes WHERE id_cliente = ?"
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
			throw new DaoException("Erro ao consultar cliente:\n\n" + e.getMessage());
		}
	}
	
	public List<Cliente> porNome(String nome) {
		try {
			PreparedStatement ps = connection.prepareStatement(
				"SELECT * FROM clientes WHERE nome LIKE ?"
			);
			
			ps.setString(1, "%" + nome + "%");
			
			ResultSet rs = ps.executeQuery();
			
			try {
				List<Cliente> lista = new ArrayList<Cliente>();
				
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
			throw new DaoException("Erro ao consultar clientes:\n\n" + e.getMessage());
		}
	}
	
	public List<Cliente> todos() {
		try {
			PreparedStatement ps = connection.prepareStatement(
				"SELECT * FROM clientes ORDER BY nome"
			);
			ResultSet rs = ps.executeQuery();
			
			try {
				List<Cliente> lista = new ArrayList<Cliente>();
				
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
			throw new DaoException("Erro ao consultar clientes:\n\n" + e.getMessage());
		}
	}

	void objParaPs(Cliente c, PreparedStatement ps) throws SQLException {
		Date nasc = null;
		if (c.getNascimento() != null) nasc = new Date(c.getNascimento().getTime());
		ps.setString(1, c.getNome());
		ps.setDate(2, nasc);
		ps.setString(3, c.getCpf());
		ps.setString(4, c.getRg());
		ps.setString(5, c.getTelefone());
		ps.setString(6, c.getCelular());
		ps.setString(7, c.getEndereco());
		ps.setString(8, c.getBairro());
		ps.setString(9, c.getCidade());
		ps.setString(10, c.getCep());
		ps.setString(11, c.getEstado());
		ps.setString(12, c.getEmail());
		ps.setString(13, c.getEnderecoComercial());
		ps.setString(14, c.getCnpj());
		ps.setString(15, c.getReferencias());
	}
	
	Cliente rsParaObj(ResultSet rs) throws SQLException {
		Cliente cli = new Cliente();
		cli.setId(rs.getLong("id_cliente"));
		cli.setNome(rs.getString("nome"));
		cli.setNascimento(rs.getDate("nascimento"));
		cli.setCpf(rs.getString("cpf"));
		cli.setRg(rs.getString("rg"));
		cli.setTelefone(rs.getString("telefone"));
		cli.setCelular(rs.getString("celular"));
		cli.setEndereco(rs.getString("endereco"));
		cli.setBairro(rs.getString("bairro"));
		cli.setCidade(rs.getString("cidade"));
		cli.setCep(rs.getString("cep"));
		cli.setEstado(rs.getString("estado"));
		cli.setEmail(rs.getString("email"));
		cli.setEnderecoComercial(rs.getString("endereco_comercial"));
		cli.setCnpj(rs.getString("cnpj"));
		cli.setReferencias(rs.getString("referencias"));
		return cli;
	}

}