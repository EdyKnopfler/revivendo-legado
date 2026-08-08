package br.com.gamecursos.estoque.test.fornecedores;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import br.com.gamecursos.estoque.dao.DaoException;
import br.com.gamecursos.estoque.dao.FornecedorDao;
import br.com.gamecursos.estoque.model.Fornecedor;
import br.com.gamecursos.estoque.test.Conexao;
import br.com.gamecursos.estoque.test.PreparaCadastros;

public class FornecedorDaoTest {

	private static Connection connection;
	private static PreparaCadastros dados;
	private static FornecedorDao fornDao;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		connection = Conexao.abrir();
		dados = new PreparaCadastros(connection);
		fornDao = new FornecedorDao(connection);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		dados.esvaziar();
		connection.close();
	}

	@Test
	public void incluiAlteraEExclui() throws Exception {
		Fornecedor fornecedor = new Fornecedor();
		fornecedor.setNome("Móveis Planejados S/A");
		fornecedor.setCnpj("12.345.678/0001-00");
		fornecedor.setEmail("contato@moveisplanejados.example.com");

		fornDao.incluir(fornecedor);
		connection.commit();
		assertNotNull(fornecedor.getId());

		Fornecedor carregado = fornDao.porId(fornecedor.getId().intValue());
		assertEquals("Móveis Planejados S/A", carregado.getNome());
		assertEquals("12.345.678/0001-00", carregado.getCnpj());
		assertEquals("contato@moveisplanejados.example.com", carregado.getEmail());

		carregado.setEmail("financeiro@moveisplanejados.example.com");
		fornDao.alterar(carregado);
		connection.commit();

		Fornecedor atualizado = fornDao.porId(fornecedor.getId().intValue());
		assertEquals("financeiro@moveisplanejados.example.com", atualizado.getEmail());

		fornDao.excluir(atualizado);
		connection.commit();
		assertNull(fornDao.porId(fornecedor.getId().intValue()));
	}

	@Test
	public void porNomeIgnoraCaixaEAcento() throws Exception {
		Fornecedor fornecedor = dados.criarFornecedor("Estofados Irmãos Corrêa");
		connection.commit();

		List<Fornecedor> encontrados = fornDao.porNome("estofados irmaos correa");

		assertTrue(encontrados.stream().anyMatch(f -> f.getId().equals(fornecedor.getId())));
	}

	@Test
	public void excluirFornecedorComProdutoFalha() throws Exception {
		Fornecedor fornecedor = dados.criarFornecedor("Fornecedor Com Produto");
		dados.criarProduto("Produto Qualquer", fornecedor);
		connection.commit();

		assertThrows(DaoException.class, () -> fornDao.excluir(fornecedor));
		connection.rollback();
	}

}
