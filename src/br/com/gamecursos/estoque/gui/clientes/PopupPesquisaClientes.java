package br.com.gamecursos.estoque.gui.clientes;

import java.util.List;
import br.com.gamecursos.estoque.dao.ClienteDao;
import br.com.gamecursos.estoque.model.Cliente;
import br.com.gamecursos.util.pesquisa.AcaoSelecao;
import br.com.gamecursos.util.pesquisa.PopupPesquisa;

public class PopupPesquisaClientes extends PopupPesquisa<Cliente> {

	private static final long serialVersionUID = 1L;
	
	private ClienteDao clienteDao;
	
	public PopupPesquisaClientes(AcaoSelecao<Cliente> acaoSelecao,
			ClienteDao clienteDao) {
		super(acaoSelecao);
		this.clienteDao = clienteDao;
	}

	@Override
	public List<Cliente> realizarPesquisa(String nome) {
		return clienteDao.porNome(nome);
	}

}
