package br.com.gamecursos.estoque.repo;

import java.util.Date;
import java.util.List;

import br.com.gamecursos.estoque.dao.ConflitoConcorrenciaException;
import br.com.gamecursos.estoque.dao.EstoqueDao;
import br.com.gamecursos.estoque.dao.CompraDao;
import br.com.gamecursos.estoque.model.ItemCompra;
import br.com.gamecursos.estoque.model.Compra;

public class CompraRep {

	private CompraDao CompraDao;
	private EstoqueDao estoqueDao;

	public CompraRep(CompraDao CompraDao, EstoqueDao estoqueDao) {
		this.CompraDao = CompraDao;
		this.estoqueDao = estoqueDao;
	}

	public void incluir(Compra c) throws ConflitoConcorrenciaException {
		CompraDao.incluir(c);
		baixa(c);
	}

	public void alterar(Compra c) throws ConflitoConcorrenciaException {
		Compra original = CompraDao.porId(c.getId());
		estorna(original);
		CompraDao.alterar(c);
		baixa(c);
	}

	public void excluir(Compra c) throws ConflitoConcorrenciaException {
		CompraDao.excluir(c);
		estorna(c);
	}

	private void baixa(Compra c) throws ConflitoConcorrenciaException {
		for (ItemCompra i: c.getItens()) {
			estoqueDao.entrada(i.getProduto(), i.getQuantidade());
		}
	}

	private void estorna(Compra c) throws ConflitoConcorrenciaException {
		for (ItemCompra i: c.getItens()) {
			estoqueDao.saida(i.getProduto(), i.getQuantidade());
		}
	}

	public Compra porId(long id) {
		return CompraDao.porId(id);
	}

	public List<Compra> todos() {
		return CompraDao.todos();
	}

	public List<Compra> porData(Date data) {
		return CompraDao.porData(new java.sql.Date(data.getTime()));
	}

	public List<Compra> porNome(String nome) {
		return CompraDao.porNome(nome);
	}
	
}
