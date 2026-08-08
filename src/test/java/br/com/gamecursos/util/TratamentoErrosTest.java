package br.com.gamecursos.util;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import br.com.gamecursos.estoque.dao.ConflitoConcorrenciaException;
import br.com.gamecursos.estoque.dao.DaoException;
import br.com.gamecursos.estoque.test.Conexao;
import br.com.gamecursos.swingcrud.CRUDException;

public class TratamentoErrosTest {

	private static Connection connection;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		connection = Conexao.abrir();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		connection.close();
	}

	@Test
	public void sucessoNaPrimeiraTentativa() throws CRUDException {
		AtomicInteger execucoes = new AtomicInteger();

		TratamentoErros.executarTransacao(connection, () -> execucoes.incrementAndGet());

		assertEquals(1, execucoes.get());
	}

	@Test
	public void conflitoResolvidoEmTentativaPosterior() throws CRUDException {
		AtomicInteger execucoes = new AtomicInteger();

		TratamentoErros.executarTransacao(connection, () -> {
			if (execucoes.incrementAndGet() < 3)
				throw new ConflitoConcorrenciaException("conflito simulado", null);
		});

		// Falhou nas duas primeiras tentativas e só vingou na terceira
		assertEquals(3, execucoes.get());
	}

	@Test
	public void desisteAposEsgotarTentativas() {
		AtomicInteger execucoes = new AtomicInteger();

		CRUDException erro = assertThrows(CRUDException.class, () ->
			TratamentoErros.executarTransacao(connection, () -> {
				execucoes.incrementAndGet();
				throw new ConflitoConcorrenciaException("conflito simulado", null);
			})
		);

		// 3 tentativas (MAX_TENTATIVAS), nenhuma a mais
		assertEquals(3, execucoes.get());
		assertEquals("Tente Novamente", erro.getTitulo());
	}

	@Test
	public void erroDeConexaoViraDaoException() throws Exception {
		Connection conexaoFechada = Conexao.abrir();
		conexaoFechada.close();

		// commit() numa conexão fechada estoura SQLException, não capturado pela
		// assinatura de Operacao — é o único jeito de chegar nesse branch sem mock.
		assertThrows(DaoException.class, () ->
			TratamentoErros.executarTransacao(conexaoFechada, () -> {})
		);
	}

	@Test
	public void runtimeExceptionPropagaSemMascarar() throws CRUDException {
		IllegalStateException original = new IllegalStateException("erro inesperado");

		IllegalStateException relancada = assertThrows(IllegalStateException.class, () ->
			TratamentoErros.executarTransacao(connection, () -> {
				throw original;
			})
		);

		assertSame(original, relancada);

		// A conexão não deve ter ficado numa transação travada após o rollback
		AtomicInteger execucoes = new AtomicInteger();
		TratamentoErros.executarTransacao(connection, () -> execucoes.incrementAndGet());
		assertEquals(1, execucoes.get());
	}

}
