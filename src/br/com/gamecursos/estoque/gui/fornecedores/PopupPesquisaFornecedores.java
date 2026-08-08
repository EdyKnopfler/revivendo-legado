package br.com.gamecursos.estoque.gui.fornecedores;

import java.util.List;

import br.com.gamecursos.estoque.dao.FornecedorDao;
import br.com.gamecursos.estoque.model.Fornecedor;
import br.com.gamecursos.util.pesquisa.AcaoSelecao;
import br.com.gamecursos.util.pesquisa.PopupPesquisa;

public class PopupPesquisaFornecedores extends PopupPesquisa<Fornecedor> {

	private static final long serialVersionUID = 1L;
	
	private FornecedorDao fornecedorDao;
	
	public PopupPesquisaFornecedores(AcaoSelecao<Fornecedor> acaoSelecao,
			FornecedorDao fornecedorDao) {
		super(acaoSelecao);
		this.fornecedorDao = fornecedorDao;
	}

	@Override
	public List<Fornecedor> realizarPesquisa(String nome) {
		return fornecedorDao.porNome(nome);
	}

}
