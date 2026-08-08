package br.com.gamecursos.swingcrud;

public class CRUDException extends Exception {

	private static final long serialVersionUID = 1L;

	private String titulo;

	public CRUDException(String mensagem) {
		this(mensagem, "Dados Incorretos");
	}

	public CRUDException(String mensagem, String titulo) {
		super(mensagem);
		this.titulo = titulo;
	}

	public String getTitulo() {
		return titulo;
	}

}
