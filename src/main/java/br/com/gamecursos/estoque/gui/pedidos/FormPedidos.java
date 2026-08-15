package br.com.gamecursos.estoque.gui.pedidos;

import static javax.swing.JOptionPane.*;

import java.sql.Connection;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.io.File;
import java.net.URI;
import java.net.URL;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableCellRenderer;

import java.text.ParseException;

import br.com.gamecursos.estoque.Aplicacao;
import br.com.gamecursos.estoque.dao.ConflitoConcorrenciaException;
import br.com.gamecursos.estoque.dao.EstoqueDao;
import br.com.gamecursos.estoque.dao.PedidoDao;
import br.com.gamecursos.estoque.model.Pedido;
import br.com.gamecursos.estoque.model.ItemPedido;
import br.com.gamecursos.estoque.repo.PedidoRep;
import br.com.gamecursos.swingcrud.CRUDAdapter;
import br.com.gamecursos.swingcrud.CRUDListener;
import br.com.gamecursos.swingcrud.ControladorCRUD;
import br.com.gamecursos.util.FormCadastroPadrao;
import br.com.gamecursos.util.Operacao;
import br.com.gamecursos.util.Relatorio;
import br.com.gamecursos.util.TratamentoErros;

public class FormPedidos extends FormCadastroPadrao<Pedido> {

	private static final long serialVersionUID = 1L;
	
	private PedidoRep cadastro;
	private Connection connection;
	private JComboBox<String> pesquisarPor;
	private JTextField textoPesquisa;
	private JButton pesquisar;
	private JButton imprimir;
	
	public FormPedidos(Connection connection) {
		super(new TableModelPedidos(), new PainelPedido(connection));
		
		this.connection = connection;
		EstoqueDao estoqueDao = new EstoqueDao(connection);
		PedidoDao pedidoDao = new PedidoDao(connection);
		this.cadastro = new PedidoRep(pedidoDao, estoqueDao);
		
		setTitle("Pedidos");
		setSize(950, 700);
		
		tamanhoColuna(2, 650);

		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(SwingConstants.RIGHT);
		getTabela().getColumnModel().getColumn(3).setCellRenderer(renderer);
		
		pesquisarPor = new JComboBox<String>(new String[] {"Data", "Cliente"});
		textoPesquisa = new JTextField(30);
		pesquisar = new JButton("Pesquisar");
		JPanel pesquisa = getPainelPesquisa();
		pesquisa.add(new JLabel("Procurar por:"));
		pesquisa.add(pesquisarPor);
		pesquisa.add(textoPesquisa);
		pesquisa.add(pesquisar);
		AcaoPesquisa acaoPesq = new AcaoPesquisa();
		textoPesquisa.addActionListener(acaoPesq);
		pesquisar.addActionListener(acaoPesq);
		
		imprimir = new JButton("Imprimir");
		URL urlImprimir = Aplicacao.class.getResource("/rel-pedidos.png");
		ImageIcon imgImprimir = new ImageIcon(urlImprimir);
		imprimir.setIcon(imgImprimir);
		imprimir.addActionListener(new BotaoImprimir());
		habilitaImprimir();

		getTableModel().setListaObjetos(cadastro.todos());
		getAbas().addChangeListener(new MudancaAba());
		getTabela().getSelectionModel().addListSelectionListener(new SelecaoTabela());
		getPainelBotoes().add(imprimir);
	}

	@Override
	public CRUDListener<Pedido> criaCrudListener() {
		return new PedidosListener();
	}

	private class PedidosListener extends CRUDAdapter<Pedido> {

		@Override
		public void aposBotaoIncluir() {
			((PainelPedido) getPainelCampos()).hoje();
			((PainelPedido) getPainelCampos()).focoEdicao();
			imprimir.setEnabled(false);
		}

		@Override
		public void aposBotaoAlterar() {
			((PainelPedido) getPainelCampos()).focoEdicao();
			imprimir.setEnabled(false);
		}

		@Override
		public void acaoGravarInclusao(final Pedido p) throws Exception {
			TratamentoErros.executarTransacao(connection, new Operacao() {
				public void executar() throws ConflitoConcorrenciaException {
					cadastro.incluir(p);
				}
			});
		}

		@Override
		public void acaoGravarAlteracao(final Pedido p) throws Exception {
			TratamentoErros.executarTransacao(connection, new Operacao() {
				public void executar() throws ConflitoConcorrenciaException {
					cadastro.alterar(p);
				}
			});
		}

		@Override
		public void aposBotaoGravar() {
			getControlador().getAlterar().requestFocus();
			habilitaImprimir();
		}
		
		@Override
		public boolean antesBotaoCancelar() {
			((PainelPedido) getPainelCampos()).cancelaEdicao();
			return true;
		}
		
		@Override
		public void aposBotaoCancelar() {
			// A exibição dos itens fica a cargo da mudança de aba, não do "exibir".
			// Logo, os originais não aparecem automaticamente ao se cancelar uma edição.
			int linha = getControlador().getTabela().getSelectedRow();
			if (linha == -1) return;
			Pedido p = getControlador().getTableModel().get(linha);
			((PainelPedido) getPainelCampos()).carregaItens(p.getItens());
			habilitaImprimir();
		}
		
		@Override
		public boolean antesBotaoExcluir() {
			return (showConfirmDialog(null, "Tem certeza?", "Excluir", YES_NO_OPTION) == YES_OPTION);
		}
		
		@Override
		public void acaoExcluir(final Pedido p) throws Exception {
			TratamentoErros.executarTransacao(connection, new Operacao() {
				public void executar() throws ConflitoConcorrenciaException {
					cadastro.excluir(p);
				}
			});
		}
		
		@Override
		public void aposBotaoExcluir() {
			getControlador().getIncluir().requestFocus();
			habilitaImprimir();
		}

	}
	
	private void habilitaImprimir() {
		imprimir.setEnabled(getTabela().getSelectedRow() >= 0 &&
				getControlador().getEstado() == ControladorCRUD.Estado.NAVEGANDO);
	}
	
	private class AcaoPesquisa implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (pesquisarPor.getSelectedIndex() == 0) {
					Date dt = DateFormat.getDateInstance().parse(textoPesquisa.getText());
					getTableModel().setListaObjetos(cadastro.porData(dt));
				}
				else
					getTableModel().setListaObjetos(cadastro.porNome(textoPesquisa.getText()));
			}
			catch (ParseException ex) {
				showMessageDialog(null, "Data inválida: " + textoPesquisa.getText(), 
						"Atenção!", WARNING_MESSAGE);
			}
		}
	}
	
	private class MudancaAba implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			if (getAbas().getSelectedIndex() == 1 && 
					getControlador().getEstado() == ControladorCRUD.Estado.NAVEGANDO) {
				int linha = getControlador().getTabela().getSelectedRow();
				if (linha == -1) {
					((PainelPedido) getPainelCampos()).carregaItens(null);
				}
				else {
					Pedido p = getTableModel().get(linha);
					((PainelPedido) getPainelCampos()).carregaItens(p.getItens());
				}
			}
		}
	}
	
	private class BotaoImprimir implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Relatorio rel;
			try {
				DecimalFormat moeda = new DecimalFormat("#,##0.00");
				DateFormat data = DateFormat.getDateInstance();
				
				int linha = getControlador().getTabela().getSelectedRow();
				Pedido p = getTableModel().get(linha);
				List<ItemPedido> itens = getTableModel().get(linha).getItens();
				
				rel = new Relatorio("relatorio.html");
				rel.carregarModelo("/pedido-cabecalho.rel");
				rel.substituirTag("<$num_pedido>", p.getId().toString());
				rel.substituirTag("<$cliente>", p.getCliente().getNome());
				rel.substituirTag("<$data>", data.format(p.getData()));
				rel.escrever();
				
				rel.carregarModelo("/pedido-registro.rel");
				for (ItemPedido i: itens) {
					rel.substituirTag("<#CODIGO>", i.getProduto().getCodigo());
					rel.substituirTag("<#PRODUTO>", i.getProduto().getNome());
					rel.substituirTag("<#PRECO_UNITARIO>", moeda.format(i.getPrecoUnitario()));
					rel.substituirTag("<#QUANTIDADE>", String.valueOf(i.getQuantidade()));
					rel.substituirTag("<#TOTAL>", moeda.format(i.getValorPagar()));
					rel.escrever();
				}
				
				rel.carregarModelo("/pedido-rodape.rel");
				rel.substituirTag("<$total>", moeda.format(p.getTotal()));
				rel.escrever();
				
				rel.finalizar();
				File f = new File("relatorio.html");  // Tem que ser html para abrir no browser
				Desktop.getDesktop().browse(new URI("file:///" + 
						f.getAbsolutePath().replace("\\", "/").replace(" ", "%20")));
			} 
			catch (Exception ex) {
				showMessageDialog(null, ex.getMessage(), "ERRO AO IMPRIMIR", ERROR_MESSAGE);
				ex.printStackTrace();
			}
			
		}
	}
	
	private class SelecaoTabela implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			habilitaImprimir();
		}
	}

}
