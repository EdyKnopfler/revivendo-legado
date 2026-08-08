package br.com.gamecursos.estoque.gui.relatorios;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.Date;

import static javax.swing.JOptionPane.*;

import br.com.gamecursos.util.Relatorio;

public class RelVendas extends RelPeriodo {

	private static final long serialVersionUID = 1L;
	private static final DecimalFormat moeda = new DecimalFormat("#,##0.00"); 
	
	private static final String query =
			"SELECT " +
			"   p.codigo, " +
			"   f.nome as fornecedor, " +
			"   p.nome, " +
			"   ( " +
			"      SELECT SUM(i.quantidade) AS qtd_adquirida " +
			"      FROM itens_compra i " +
			"         LEFT JOIN compras c ON i.id_compra = c.id_compra " +
			"      WHERE i.id_produto = p.id_produto AND c.data BETWEEN ? AND ? " +
			"   ), " +
			"   ( " +
			"      SELECT SUM(i.quantidade * i.preco_unitario) AS qto_custou " +
			"      FROM itens_compra i " +
			"         LEFT JOIN compras c ON i.id_compra = c.id_compra " +
			"      WHERE i.id_produto = p.id_produto AND c.data BETWEEN ? AND ? " +
			"   ), " +
			"   ( " +
			"      SELECT SUM(i.quantidade) AS qtd_vendida " +
			"      FROM itens_pedido i " +
			"         LEFT JOIN pedidos ped ON i.id_pedido = ped.id_pedido " +
			"      WHERE i.id_produto = p.id_produto AND ped.data BETWEEN ? AND ? " +
			"   ), " +
			"   ( " +
			"      SELECT SUM(i.quantidade * i.preco_unitario) AS total_venda " +
			"      FROM itens_pedido i " +
			"         LEFT JOIN pedidos ped ON i.id_pedido = ped.id_pedido " +
			"      WHERE i.id_produto = p.id_produto AND ped.data BETWEEN ? AND ? " +
			"   ) " +
			"FROM produtos p " +
			"   LEFT JOIN fornecedores f " +
			"      ON p.id_fornecedor = f.id_fornecedor " +
			"ORDER BY p.nome ";
	
	private Connection connection;
		
	public RelVendas(Connection connection) {
		this.connection = connection;
		setTitle("Relatório de Vendas");
	}
	
	@Override
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
			rel.carregarModelo("/vendas-cabecalho.rel");
			rel.escrever();
			
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setDate(1, deS);
			ps.setDate(2, ateS);
			ps.setDate(3, deS);
			ps.setDate(4, ateS);
			ps.setDate(5, deS);
			ps.setDate(6, ateS);
			ps.setDate(7, deS);
			ps.setDate(8, ateS);
			
			double totalCompras = 0.00;
			double totalVendas = 0.00;
			
			rel.carregarModelo("/vendas-registro.rel");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				rel.substituirTag("<#CODIGO>", rs.getString("CODIGO"));
				rel.substituirTag("<#FORNECEDOR>", rs.getString("FORNECEDOR"));
				rel.substituirTag("<#NOME>", rs.getString("NOME"));
				rel.substituirTag("<#QTD_ADQUIRIDA>", String.valueOf(rs.getInt("QTD_ADQUIRIDA")));
				rel.substituirTag("<#QTO_CUSTOU>", moeda.format(rs.getDouble("QTO_CUSTOU")));
				rel.substituirTag("<#QTD_VENDIDA>", String.valueOf(rs.getInt("QTD_VENDIDA")));
				rel.substituirTag("<#TOTAL_VENDA>", moeda.format(rs.getDouble("TOTAL_VENDA")));
				rel.escrever();
				totalCompras += rs.getDouble("QTO_CUSTOU");
				totalVendas += rs.getDouble("TOTAL_VENDA");
			}
			
			rs.close();
			ps.close();
		
			rel.carregarModelo("/vendas-rodape.rel");
			rel.substituirTag("<$total_compra>", moeda.format(totalCompras));
			rel.substituirTag("<$total_venda>", moeda.format(totalVendas));
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
