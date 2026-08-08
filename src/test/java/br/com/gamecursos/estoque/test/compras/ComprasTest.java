package br.com.gamecursos.estoque.test.compras;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import br.com.gamecursos.estoque.dao.CompraDao;
import br.com.gamecursos.estoque.dao.ConflitoConcorrenciaException;
import br.com.gamecursos.estoque.dao.EstoqueDao;
import br.com.gamecursos.estoque.dao.ItemCompraDao;
import br.com.gamecursos.estoque.model.Compra;
import br.com.gamecursos.estoque.model.ItemCompra;
import br.com.gamecursos.estoque.repo.CompraRep;
import br.com.gamecursos.estoque.test.Conexao;
import br.com.gamecursos.estoque.test.PreparaCadastros;

public class ComprasTest {

	private static Connection connection;
	private static PreparaCadastros dados;
	private Compra compra;

	private static CompraDao comDao;
	private static EstoqueDao estDao;
	private static CompraRep comRep;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		connection = Conexao.abrir();
		dados = new PreparaCadastros(connection);
		dados.clienteFornecedorProdutos();
		comDao = new CompraDao(connection);
		estDao = new EstoqueDao(connection);
		comRep = new CompraRep(comDao, estDao);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		dados.esvaziar();
		connection.close();
	}

	@Test
	public void test() throws SQLException, ConflitoConcorrenciaException {
		// Fazemos uma sequência de operações
		inclusao();
		alteracaoSimples();
		alteracaoComInclusao();
		alteracaoComExclusao();
		exclusao();
	}

	private void inclusao() throws SQLException, ConflitoConcorrenciaException {
		compra = new Compra();
		compra.setFornecedor(dados.getFornecedor());
		compra.setData(new Date());
		compra.setNota("NF-001");

		ItemCompra i1 = new ItemCompra();
		i1.setProduto(dados.getSofa());
		i1.setPrecoUnitario(1.00);
		i1.setQuantidade(2);

		ItemCompra i2 = new ItemCompra();
		i2.setProduto(dados.getCama());
		i2.setPrecoUnitario(1.00);
		i2.setQuantidade(1);

		compra.getItens().add(i1);
		compra.getItens().add(i2);

		comRep.incluir(compra);
		connection.commit();

		// Nos dados de amostra, há 10 de cada um!
		assertEquals(12, estDao.quantosTem(dados.getSofa()));
		assertEquals(11, estDao.quantosTem(dados.getCama()));
		assertEquals(10, estDao.quantosTem(dados.getGuardaRoupa()));
	}

	private void alteracaoSimples() throws SQLException, ConflitoConcorrenciaException {
		compra.getItens().get(1).setQuantidade(3);  // De 1 foi para 3
		comRep.alterar(compra);
		connection.commit();

		// Agora são 13 camas
		assertEquals(12, estDao.quantosTem(dados.getSofa()));
		assertEquals(13, estDao.quantosTem(dados.getCama()));
		assertEquals(10, estDao.quantosTem(dados.getGuardaRoupa()));
	}

	private void alteracaoComInclusao() throws SQLException, ConflitoConcorrenciaException {
		ItemCompra i3 = new ItemCompra();
		i3.setProduto(dados.getGuardaRoupa());
		i3.setPrecoUnitario(1.00);
		i3.setQuantidade(5);

		compra.getItens().add(i3);
		comRep.alterar(compra);
		connection.commit();

		// Chegaram 5 guarda-roupas
		assertEquals(12, estDao.quantosTem(dados.getSofa()));
		assertEquals(13, estDao.quantosTem(dados.getCama()));
		assertEquals(15, estDao.quantosTem(dados.getGuardaRoupa()));
	}

	private void alteracaoComExclusao() throws SQLException, ConflitoConcorrenciaException {
		ItemCompra removido = compra.getItens().remove(0);
		assertEquals(dados.getSofa(), removido.getProduto());

		comRep.alterar(compra);
		connection.commit();

		// Desistimos da compra do sofá
		assertEquals(10, estDao.quantosTem(dados.getSofa()));
		assertEquals(13, estDao.quantosTem(dados.getCama()));
		assertEquals(15, estDao.quantosTem(dados.getGuardaRoupa()));
	}

	private void exclusao() throws SQLException, ConflitoConcorrenciaException {
		comRep.excluir(compra);
		connection.commit();

		// Tudo deve voltar aos 10 iniciais!
		assertEquals(10, estDao.quantosTem(dados.getSofa()));
		assertEquals(10, estDao.quantosTem(dados.getCama()));
		assertEquals(10, estDao.quantosTem(dados.getGuardaRoupa()));

		// Excluir a compra deve arrastar os itens junto (ON DELETE CASCADE)
		assertTrue(new ItemCompraDao(connection).pegarItens(compra).isEmpty());
	}

}
