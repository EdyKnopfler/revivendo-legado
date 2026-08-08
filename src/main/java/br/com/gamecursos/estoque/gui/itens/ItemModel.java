package br.com.gamecursos.estoque.gui.itens;

import br.com.gamecursos.estoque.model.Produto;

public class ItemModel {
	
	private Long id;
	private Produto produto;
	private int quantidade = 0;
	private double precoUnitario = 0.00;
	private boolean emBranco;
	
	public ItemModel() {
	   emBranco = true;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}
	
	public Produto getProduto() {
		return produto;
	}
	
	public void setProduto(Produto produto) {
		this.produto = produto;
	}
	
	public int getQuantidade() {
		return quantidade;
	}
	
	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}
	
	public double getPrecoUnitario() {
		return precoUnitario;
	}
	
	public void setPrecoUnitario(double precoUnitario) {
		this.precoUnitario = precoUnitario;
	}
	
	public double getValorPagar() {
		return quantidade * precoUnitario;
	}
	
	public void setEmBranco(boolean emBranco) {
	   this.emBranco = emBranco;
	}
	
	public boolean isEmBranco() {
	   return emBranco;
	}

}
