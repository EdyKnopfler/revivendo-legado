package br.com.gamecursos.estoque.gui.produtos;

import java.util.List;
import br.com.gamecursos.estoque.dao.ProdutoDao;
import br.com.gamecursos.estoque.model.Produto;
import br.com.gamecursos.util.pesquisa.AcaoSelecao;
import br.com.gamecursos.util.pesquisa.PopupPesquisa;

public class PopupPesquisaProdutos extends PopupPesquisa<Produto> {

	private static final long serialVersionUID = 1L;
	
	private ProdutoDao produtoDao;
	
	public PopupPesquisaProdutos(AcaoSelecao<Produto> acaoSelecao,
			ProdutoDao produtoDao) {
		super(acaoSelecao);
		this.produtoDao = produtoDao;
	}

	@Override
	public List<Produto> realizarPesquisa(String nome) {
		return produtoDao.porNome(nome);
	}

}
