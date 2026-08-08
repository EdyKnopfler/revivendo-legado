package br.com.gamecursos.estoque.model;

import java.util.List;

import br.com.gamecursos.estoque.dao.ItemPedidoDao;

import java.util.ArrayList;
import java.util.Date;

public class Pedido {
	
	private Long id;
	private Cliente cliente;
	private Date data;
	private List<ItemPedido> itens = new ArrayList<ItemPedido>();
	private double total;
	
	private boolean lazyLoad = false;
	private ItemPedidoDao itemDao = null; 
	
	public Pedido() {}
	
	public Pedido(ItemPedidoDao itemDao) {
		this.itemDao = itemDao;
		this.lazyLoad = true;
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Cliente getCliente() {
		return cliente;
	}
	
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	
	public Date getData() {
		return data;
	}
	
	public void setData(Date data) {
		this.data = data;
	}
	
	public List<ItemPedido> getItens() {
		if (lazyLoad) {
			itens = itemDao.pegarItens(this);
			lazyLoad = false;
		}
		
		return itens;
	}
	
	public void setItens(List<ItemPedido> itens) {
		this.itens = itens;
		lazyLoad = false;
	}
	
	/** Para guardar o total antes do lazy load! */
	public void setTotal(double total) {
		this.total = total;
	}
	
	public double getTotal() {
		if (lazyLoad) return total;
		double t = 0.00;
		for (ItemPedido i: itens)
			t += i.getValorPagar();
		return t;
	}
	
}
