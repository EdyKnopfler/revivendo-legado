package br.com.gamecursos.estoque.test.clientes;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import br.com.gamecursos.estoque.dao.ClienteDao;
import br.com.gamecursos.estoque.dao.DaoException;
import br.com.gamecursos.estoque.dao.PedidoDao;
import br.com.gamecursos.estoque.model.Cliente;
import br.com.gamecursos.estoque.model.Pedido;
import br.com.gamecursos.estoque.test.Conexao;
import br.com.gamecursos.estoque.test.PreparaCadastros;

public class ClienteDaoTest {

	private static Connection connection;
	private static PreparaCadastros dados;
	private static ClienteDao cliDao;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		connection = Conexao.abrir();
		dados = new PreparaCadastros(connection);
		cliDao = new ClienteDao(connection);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		dados.esvaziar();
		connection.close();
	}

	@Test
	public void incluiAlteraEExclui() throws Exception {
		Cliente cliente = new Cliente();
		cliente.setNome("Ana Paula");
		cliente.setCpf("123.456.789-00");
		cliente.setEmail("ana@example.com");

		cliDao.incluir(cliente);
		connection.commit();
		assertNotNull(cliente.getId());

		Cliente carregado = cliDao.porId(cliente.getId().intValue());
		assertEquals("Ana Paula", carregado.getNome());
		assertEquals("123.456.789-00", carregado.getCpf());
		assertEquals("ana@example.com", carregado.getEmail());

		carregado.setEmail("ana.paula@example.com");
		cliDao.alterar(carregado);
		connection.commit();

		Cliente atualizado = cliDao.porId(cliente.getId().intValue());
		assertEquals("ana.paula@example.com", atualizado.getEmail());

		cliDao.excluir(atualizado);
		connection.commit();
		assertNull(cliDao.porId(cliente.getId().intValue()));
	}

	@Test
	public void porNomeIgnoraCaixaEAcento() throws Exception {
		Cliente cliente = dados.criarCliente("José Ávila");
		connection.commit();

		List<Cliente> encontrados = cliDao.porNome("jose avila");

		assertTrue(encontrados.stream().anyMatch(c -> c.getId().equals(cliente.getId())));
	}

	@Test
	public void excluirClienteComPedidoFalha() throws Exception {
		Cliente cliente = dados.criarCliente("Cliente Com Pedido");

		Pedido pedido = new Pedido();
		pedido.setCliente(cliente);
		pedido.setData(new Date());
		new PedidoDao(connection).incluir(pedido);
		connection.commit();

		assertThrows(DaoException.class, () -> cliDao.excluir(cliente));
		connection.rollback();
	}

}
