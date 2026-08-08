package br.com.gamecursos.estoque.test.pedidos;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import br.com.gamecursos.estoque.dao.ConflitoConcorrenciaException;
import br.com.gamecursos.estoque.dao.EstoqueDao;
import br.com.gamecursos.estoque.dao.PedidoDao;
import br.com.gamecursos.estoque.model.ItemPedido;
import br.com.gamecursos.estoque.model.Pedido;
import br.com.gamecursos.estoque.repo.PedidoRep;
import br.com.gamecursos.estoque.test.Conexao;
import br.com.gamecursos.estoque.test.PreparaCadastros;

public class PedidosTest {

	private static Connection connection;
	private static PreparaCadastros dados;
	private Pedido pedido;
	
	private static PedidoDao pedDao;
	private static EstoqueDao estDao;
	private static PedidoRep pedRep;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		connection = Conexao.abrir();
		dados = new PreparaCadastros(connection);
		dados.clienteFornecedorProdutos();
		pedDao = new PedidoDao(connection);
		estDao = new EstoqueDao(connection);
		pedRep = new PedidoRep(pedDao, estDao);
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
		pedido = new Pedido();
		pedido.setCliente(dados.getCliente());
		pedido.setData(new Date());
		
		ItemPedido i1 = new ItemPedido();
		i1.setProduto(dados.getSofa());
		i1.setPrecoUnitario(1.00);
		i1.setQuantidade(2);
		
		ItemPedido i2 = new ItemPedido();
		i2.setProduto(dados.getCama());
		i2.setPrecoUnitario(1.00);
		i2.setQuantidade(1);
		
		pedido.getItens().add(i1);
		pedido.getItens().add(i2);

		pedRep.incluir(pedido);
		connection.commit();
		
		// Nos dados de amostra, há 10 de cada um!
		assertEquals(8, estDao.quantosTem(dados.getSofa()));
		assertEquals(9, estDao.quantosTem(dados.getCama()));
		assertEquals(10, estDao.quantosTem(dados.getGuardaRoupa()));
	}

	private void alteracaoSimples() throws SQLException, ConflitoConcorrenciaException {
		pedido.getItens().get(1).setQuantidade(3);  // De 1 foi para 3
		pedRep.alterar(pedido);
		connection.commit();
		
		// Agora são 7 camas
		assertEquals(8, estDao.quantosTem(dados.getSofa()));
		assertEquals(7, estDao.quantosTem(dados.getCama()));
		assertEquals(10, estDao.quantosTem(dados.getGuardaRoupa()));
	}

	private void alteracaoComInclusao() throws SQLException, ConflitoConcorrenciaException {
		ItemPedido i3 = new ItemPedido();
		i3.setProduto(dados.getGuardaRoupa());
		i3.setPrecoUnitario(1.00);
		i3.setQuantidade(5);
		
		pedido.getItens().add(i3);
		pedRep.alterar(pedido);
		connection.commit();
		
		// Sobraram 5 guarda-roupas
		assertEquals(8, estDao.quantosTem(dados.getSofa()));
		assertEquals(7, estDao.quantosTem(dados.getCama()));
		assertEquals(5, estDao.quantosTem(dados.getGuardaRoupa()));
	}

	private void alteracaoComExclusao() throws SQLException, ConflitoConcorrenciaException {
		ItemPedido removido = pedido.getItens().remove(0);
		assertEquals(dados.getSofa(), removido.getProduto());
		
		pedRep.alterar(pedido);
		connection.commit();
		
		// Desistimos do sofá
		assertEquals(10, estDao.quantosTem(dados.getSofa()));
		assertEquals(7, estDao.quantosTem(dados.getCama()));
		assertEquals(5, estDao.quantosTem(dados.getGuardaRoupa()));
	}

	private void exclusao() throws SQLException, ConflitoConcorrenciaException {
		pedRep.excluir(pedido);
		connection.commit();
		
		// Tudo deve voltar aos 10 iniciais!
		assertEquals(10, estDao.quantosTem(dados.getSofa()));
		assertEquals(10, estDao.quantosTem(dados.getCama()));
		assertEquals(10, estDao.quantosTem(dados.getGuardaRoupa()));
	}
	
}
