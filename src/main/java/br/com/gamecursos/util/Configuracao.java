package br.com.gamecursos.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class Configuracao {
	
	private String ip, arquivo, usuario, senha, backup;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getArquivo() {
		return arquivo;
	}

	public void setArquivo(String arquivo) {
		this.arquivo = arquivo;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}
	
	public String getBackup() {
		return backup;
	}
	
	public void setBackup(String backup) {
		this.backup = backup;
	}
	
	public void carregar(String arq) throws Exception {
		Properties prop = new Properties();
		FileInputStream leitura = new FileInputStream(arq);
		prop.load(leitura);
		leitura.close();
		ip = prop.getProperty("ip");
		arquivo = prop.getProperty("arquivo");
		usuario = prop.getProperty("usuario");
		senha = decriptografar(prop.getProperty("senha"));
		backup = prop.getProperty("backup");
	}
	
	public void salvar(String arq) throws Exception {
		Properties prop = new Properties();
		prop.setProperty("ip", ip);
		prop.setProperty("arquivo", arquivo);
		prop.setProperty("usuario", usuario);
		prop.setProperty("senha", criptografar(senha));
		prop.setProperty("backup", backup);
		FileOutputStream escrita = new FileOutputStream(arq);
		prop.store(escrita, "");
		escrita.close();
	}
	
	public Connection conectar() throws Exception {
		Class.forName("org.firebirdsql.jdbc.FBDriver");
		Connection connection = DriverManager.getConnection(
			"jdbc:firebirdsql:" + ip + "/3050:" + arquivo + "?lc_ctype=UTF8",
			usuario, senha
		);
		connection.setAutoCommit(false);
		return connection;
	}
	
	private String criptografar(String texto) {
		int tam = texto.length();
		String res = "";
		
		for (int i = 0; i < tam; i++)
			res = Character.toString((char) 
					(Character.MAX_VALUE - texto.charAt(i))) + res;
			
		return res;
	}
	
	private String decriptografar(String texto) {
		return criptografar(texto);  // Mão dupla!
	}

}
