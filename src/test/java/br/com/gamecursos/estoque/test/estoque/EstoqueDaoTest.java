package br.com.gamecursos.estoque.test.estoque;

import static org.junit.Assert.*;

import java.sql.Connection;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import br.com.gamecursos.estoque.dao.EstoqueDao;
import br.com.gamecursos.estoque.model.Fornecedor;
import br.com.gamecursos.estoque.model.Produto;
import br.com.gamecursos.estoque.test.Conexao;
import br.com.gamecursos.estoque.test.PreparaCadastros;

public class EstoqueDaoTest {

	private static Connection connection;
	private static PreparaCadastros dados;
	private static EstoqueDao estDao;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		connection = Conexao.abrir();
		dados = new PreparaCadastros(connection);
		estDao = new EstoqueDao(connection);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		dados.esvaziar();
		connection.close();
	}

	// Não é bug: fica negativo de propósito, pro relatório de estoque sinalizar o que precisa comprar.
	@Test
	public void saidaMaiorQueOEstoqueFicaNegativo() throws Exception {
		Fornecedor fornecedor = dados.criarFornecedor("Fornecedor do Teste de Estoque");
		Produto produto = dados.criarProduto("Produto com Pouco Estoque", fornecedor);
		dados.darEntrada(produto, 2);
		connection.commit();

		estDao.saida(produto, 5);
		connection.commit();

		assertEquals(-3, estDao.quantosTem(produto));
	}

}
