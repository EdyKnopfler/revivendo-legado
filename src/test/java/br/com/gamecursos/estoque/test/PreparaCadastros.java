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
	
	public Cliente criarCliente(String nome) throws SQLException, ConflitoConcorrenciaException {
		Cliente c = new Cliente();
		c.setNome(nome);
		new ClienteDao(connection).incluir(c);
		return c;
	}

	public Fornecedor criarFornecedor(String nome) throws SQLException, ConflitoConcorrenciaException {
		Fornecedor f = new Fornecedor();
		f.setNome(nome);
		new FornecedorDao(connection).incluir(f);
		return f;
	}

	public Produto criarProduto(String nome, Fornecedor fornecedor) throws SQLException, ConflitoConcorrenciaException {
		Produto p = new Produto();
		p.setNome(nome);
		p.setFornecedor(fornecedor);
		new ProdutoDao(connection).incluir(p);
		return p;
	}

	public void darEntrada(Produto produto, int quantidade) throws ConflitoConcorrenciaException {
		new EstoqueDao(connection).entrada(produto, quantidade);
	}

	public void clienteFornecedorProdutos() throws SQLException, ConflitoConcorrenciaException {
		cliente = criarCliente("Kânia");
		fornecedor = criarFornecedor("Móveis Planejados S/A");

		sofa = criarProduto("Sofá", fornecedor);
		cama = criarProduto("Cama", fornecedor);
		guardaRoupa = criarProduto("Guarda-Roupa", fornecedor);

		// Faz de conta que já há mais pedidos :)
		darEntrada(sofa, 10);
		darEntrada(cama, 10);
		darEntrada(guardaRoupa, 10);
	}

	public Cliente getCliente() {
		return cliente;
	}

	public Fornecedor getFornecedor() {
		return fornecedor;
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
