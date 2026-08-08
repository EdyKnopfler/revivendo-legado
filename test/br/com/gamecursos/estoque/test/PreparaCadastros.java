package br.com.gamecursos.estoque.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import br.com.gamecursos.estoque.dao.ClienteDao;
import br.com.gamecursos.estoque.dao.ConflitoConcorrenciaException;
import br.com.gamecursos.estoque.dao.EstoqueDao;
import br.com.gamecursos.estoque.dao.FornecedorDao;
import br.com.gamecursos.estoque.dao.ProdutoDao;
import br.com.gamecursos.estoque.model.Cliente;
import br.com.gamecursos.estoque.model.Fornecedor;
import br.com.gamecursos.estoque.model.Produto;

public class PreparaCadastros {
	
	private Connection connection;
	private Cliente cliente;
	private Fornecedor fornecedor;
	private Produto sofa;
	private Produto cama;
	private Produto guardaRoupa;
	
	public PreparaCadastros(Connection connection) {
		this.connection = connection;
	}
	
	public void clienteFornecedorProdutos() throws SQLException, ConflitoConcorrenciaException {
		cliente = new Cliente();
		cliente.setNome("Kânia");
		ClienteDao cliDao = new ClienteDao(connection);
		cliDao.incluir(cliente);
		
		fornecedor = new Fornecedor();
		fornecedor.setNome("Móveis Planejados S/A");
		FornecedorDao fornDao = new FornecedorDao(connection);
		fornDao.incluir(fornecedor);
		
		sofa = new Produto();
		sofa.setNome("Sofá");
		sofa.setFornecedor(fornecedor);
		
		cama = new Produto();
		cama.setNome("Cama");
		cama.setFornecedor(fornecedor);
		
		guardaRoupa = new Produto();
		guardaRoupa.setNome("Guarda-Roupa");
		guardaRoupa.setFornecedor(fornecedor);
		
		ProdutoDao prodDao = new ProdutoDao(connection);
		prodDao.incluir(sofa);
		prodDao.incluir(cama);
		prodDao.incluir(guardaRoupa);
		
		// Faz de conta que já há mais pedidos :)
		EstoqueDao estDao = new EstoqueDao(connection);
		estDao.entrada(sofa, 10);
		estDao.entrada(cama, 10);
		estDao.entrada(guardaRoupa, 10);
	}

	public Cliente getCliente() {
		return cliente;
	}

	public Produto getSofa() {
		return sofa;
	}

	public Produto getCama() {
		return cama;
	}

	public Produto getGuardaRoupa() {
		return guardaRoupa;
	}

	public void esvaziar() throws SQLException {
		Statement st = connection.createStatement();
		st.execute("DELETE FROM PEDIDOS");
		st.execute("DELETE FROM COMPRAS");
		st.execute("DELETE FROM PRODUTOS");
		st.execute("DELETE FROM CLIENTES");
		st.execute("DELETE FROM FORNECEDORES");
		st.close();
		connection.commit();
	}

}
