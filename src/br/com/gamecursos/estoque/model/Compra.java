package br.com.gamecursos.estoque.model;

import java.util.List;

import br.com.gamecursos.estoque.dao.ItemCompraDao;

import java.util.ArrayList;
import java.util.Date;

public class Compra {
	
	private Long id;
	private Fornecedor fornecedor;
	private Date data;
	private String nota;
	private List<ItemCompra> itens = new ArrayList<ItemCompra>();
	private double total;
	
	private boolean lazyLoad = false;
	private ItemCompraDao itemDao = null; 
	
	public Compra() {}
	
	public Compra(ItemCompraDao itemDao) {
		this.itemDao = itemDao;
		this.lazyLoad = true;
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Fornecedor getFornecedor() {
		return fornecedor;
	}
	
	public void setFornecedor(Fornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}
	
	public Date getData() {
		return data;
	}
	
	public void setData(Date data) {
		this.data = data;
	}
	
	public String getNota() {
		return nota;
	}
	
	public void setNota(String nota) {
		this.nota = nota;
	}
	
	public List<ItemCompra> getItens() {
		if (lazyLoad) {
			itens = itemDao.pegarItens(this);
			lazyLoad = false;
		}
		
		return itens;
	}
	
	public void setItens(List<ItemCompra> itens) {
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
		for (ItemCompra i: itens)
			t += i.getValorPagar();
		return t;
	}
	
}
