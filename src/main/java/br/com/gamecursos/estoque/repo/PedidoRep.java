package br.com.gamecursos.estoque.repo;

import java.util.Date;
import java.util.List;

import br.com.gamecursos.estoque.dao.ConflitoConcorrenciaException;
import br.com.gamecursos.estoque.dao.EstoqueDao;
import br.com.gamecursos.estoque.dao.PedidoDao;
import br.com.gamecursos.estoque.model.ItemPedido;
import br.com.gamecursos.estoque.model.Pedido;

public class PedidoRep {

	private PedidoDao pedidoDao;
	private EstoqueDao estoqueDao;

	public PedidoRep(PedidoDao pedidoDao, EstoqueDao estoqueDao) {
		this.pedidoDao = pedidoDao;
		this.estoqueDao = estoqueDao;
	}

	public void incluir(Pedido p) throws ConflitoConcorrenciaException {
		pedidoDao.incluir(p);
		baixa(p);
	}

	public void alterar(Pedido p) throws ConflitoConcorrenciaException {
		Pedido original = pedidoDao.porId(p.getId());
		estorna(original);
		pedidoDao.alterar(p);
		baixa(p);
	}

	public void excluir(Pedido p) throws ConflitoConcorrenciaException {
		pedidoDao.excluir(p);
		estorna(p);
	}

	private void baixa(Pedido p) throws ConflitoConcorrenciaException {
		for (ItemPedido i: p.getItens()) {
			estoqueDao.saida(i.getProduto(), i.getQuantidade());
		}
	}

	private void estorna(Pedido p) throws ConflitoConcorrenciaException {
		for (ItemPedido i: p.getItens()) {
			estoqueDao.entrada(i.getProduto(), i.getQuantidade());
		}
	}

	public Pedido porId(long id) {
		return pedidoDao.porId(id);
	}

	public List<Pedido> todos() {
		return pedidoDao.todos();
	}

	public List<Pedido> porData(Date data) {
		return pedidoDao.porData(new java.sql.Date(data.getTime()));
	}

	public List<Pedido> porNome(String nome) {
		return pedidoDao.porNome(nome);
	}
	
}
