package br.com.gamecursos.estoque.gui.relatorios;

import static javax.swing.JOptionPane.*;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;

import br.com.gamecursos.util.Relatorio;

public class RelPedidos extends RelPeriodo {

	private static final long serialVersionUID = 1L;
	private static final DecimalFormat moeda = new DecimalFormat("#,##0.00");
	private static final DecimalFormat codigo = new DecimalFormat("00000");
	private static final String query =
		"SELECT p.*, c.nome AS cliente " +
		"FROM pedidos p " +
		"   LEFT JOIN clientes c ON p.id_cliente = c.id_cliente " +
		"WHERE p.data BETWEEN ? AND ? " +
		"ORDER BY p.data ";
	
	private Connection connection;
	
	public RelPedidos(Connection connection) {
		this.connection = connection;
		setTitle("Relatório de Pedidos");
	}
	
	protected void cliqueOk() {
		try {
			Date de = getDe();
			Date ate = getAte();
			gerarRelatorio(de, ate);
			dispose();
		}
		catch (Exception ex) {
			showMessageDialog(null, ex.getMessage(), "Atenção!", WARNING_MESSAGE);
		}
	}
	
	private void gerarRelatorio(Date de, Date ate) {
		java.sql.Date deS = new java.sql.Date(de.getTime());
		java.sql.Date ateS = new java.sql.Date(ate.getTime());
		
		Relatorio rel;
		try {
			rel = new Relatorio("relatorio.html");
			rel.carregarModelo("/pedidos-cabecalho.rel");
			rel.escrever();
			
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setDate(1, deS);
			ps.setDate(2, ateS);
			
			double total = 0.00;
			
			rel.carregarModelo("/pedidos-registro.rel");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				rel.substituirTag("<#ID_PEDIDO>", codigo.format(rs.getLong("id_pedido")));
				rel.substituirTag("<#DATA>", DateFormat.getDateInstance().format(
						rs.getDate("data")));
				rel.substituirTag("<#CLIENTE>", rs.getString("cliente"));
				rel.substituirTag("<#TOTAL>", moeda.format(rs.getDouble("total")));
				rel.escrever();
				total += rs.getDouble("total");
			}
			
			rs.close();
			ps.close();
		
			rel.carregarModelo("/pedidos-rodape.rel");
			rel.substituirTag("<$total>", moeda.format(total));
			rel.escrever();

			rel.finalizar();
			File f = new File("relatorio.html");
			Desktop.getDesktop().browse(new URI("file:///" + 
					f.getAbsolutePath().replace("\\", "/").replace(" ", "%20")));
		} 
		catch (Exception ex) {
			ex.printStackTrace();
			showMessageDialog(null, ex.getMessage(), "ERRO AO GERAR RELATÓRIO", ERROR_MESSAGE);
		}
	}

}
