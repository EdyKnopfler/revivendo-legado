package br.com.gamecursos.util;

import java.io.*;

public class Relatorio {
	
	private BufferedWriter saida;
	private String modelo;
	private String impressao;

	public Relatorio(String arquivoHtml) throws IOException {
		FileWriter fw = new FileWriter(arquivoHtml);
		saida = new BufferedWriter(fw);
	}

	public void carregarModelo(String recurso) throws IOException {
		BufferedReader entrada = new BufferedReader(new InputStreamReader(
				Relatorio.class.getResourceAsStream(recurso)));
		modelo = "";
		try {
			String linha;
			while ((linha = entrada.readLine()) != null)
				modelo += linha;
		}
		catch (EOFException e) {}
		entrada.close();
		impressao = modelo;
	}
	
	public void substituirTag(String template, String texto) {
		impressao = impressao.replace(template, escaparHtml(texto));
	}

	private String escaparHtml(String texto) {
		if (texto == null) return "";
		return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}
	
	public void escrever() throws IOException {
		saida.write(impressao);
		saida.flush();
		impressao = modelo;
	}
	
	public void finalizar() throws IOException {
		saida.close();
	}

	

}
