package br.com.gamecursos.estoque.test.produtos;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import br.com.gamecursos.estoque.dao.CompraDao;
import br.com.gamecursos.estoque.dao.DaoException;
import br.com.gamecursos.estoque.dao.ProdutoDao;
import br.com.gamecursos.estoque.model.Compra;
import br.com.gamecursos.estoque.model.Fornecedor;
import br.com.gamecursos.estoque.model.ItemCompra;
import br.com.gamecursos.estoque.model.Produto;
import br.com.gamecursos.estoque.test.Conexao;
import br.com.gamecursos.estoque.test.PreparaCadastros;

public class ProdutoDaoTest {

	private static Connection connection;
	private static PreparaCadastros dados;
	private static ProdutoDao prodDao;
	private static Fornecedor fornecedor;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		connection = Conexao.abrir();
		dados = new PreparaCadastros(connection);
		prodDao = new ProdutoDao(connection);
		fornecedor = dados.criarFornecedor("Fornecedor Padrão dos Testes");
		connection.commit();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		dados.esvaziar();
		connection.close();
	}

	@Test
	public void incluiAlteraEExclui() throws Exception {
		Produto produto = new Produto();
		produto.setNome("Cadeira Gamer");
		produto.setCodigo("CAD-001");
		produto.setCustoUnitario(500.0);
		produto.setFornecedor(fornecedor);

		prodDao.incluir(produto);
		connection.commit();
		assertNotNull(produto.getId());

		Produto carregado = prodDao.porId(produto.getId().intValue());
		assertEquals("Cadeira Gamer", carregado.getNome());
		assertEquals("CAD-001", carregado.getCodigo());
		assertEquals(500.0, carregado.getCustoUnitario(), 0.001);

		carregado.setCustoUnitario(450.0);
		prodDao.alterar(carregado);
		connection.commit();

		Produto atualizado = prodDao.porId(produto.getId().intValue());
		assertEquals(450.0, atualizado.getCustoUnitario(), 0.001);

		prodDao.excluir(atualizado);
		connection.commit();
		assertNull(prodDao.porId(produto.getId().intValue()));
	}

	@Test
	public void porNomeIgnoraCaixaEAcentoEPorCodigoEExato() throws Exception {
		Produto produto = dados.criarProduto("Poltrona Reclinável", fornecedor);
		produto.setCodigo("POL-002");
		prodDao.alterar(produto);
		connection.commit();

		List<Produto> porNome = prodDao.porNome("poltrona reclinavel");
		assertTrue(porNome.stream().anyMatch(p -> p.getId().equals(produto.getId())));

		Produto porCodigo = prodDao.porCodigo("POL-002");
		assertNotNull(porCodigo);
		assertEquals(produto.getId(), porCodigo.getId());
	}

	@Test
	public void excluirProdutoReferenciadoPorItemDeCompraFalha() throws Exception {
		Produto produto = dados.criarProduto("Produto Comprado", fornecedor);

		Compra compra = new Compra();
		compra.setFornecedor(fornecedor);
		compra.setData(new Date());
		compra.setNota("NF-TESTE");

		ItemCompra item = new ItemCompra();
		item.setProduto(produto);
		item.setPrecoUnitario(1.0);
		item.setQuantidade(1);
		compra.getItens().add(item);

		new CompraDao(connection).incluir(compra);
		connection.commit();

		assertThrows(DaoException.class, () -> prodDao.excluir(produto));
		connection.rollback();
	}

}
