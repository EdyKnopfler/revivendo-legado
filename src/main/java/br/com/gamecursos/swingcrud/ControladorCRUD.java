package br.com.gamecursos.swingcrud;

import java.awt.event.*;
import javax.swing.*;

import static javax.swing.JOptionPane.*;

public class ControladorCRUD<T> {

   public enum Estado { NAVEGANDO, INCLUINDO, ALTERANDO };
   
   private TableModel<T> tableModel;
   private PainelCampos<T> painelCampos;
   private CRUDListener<T> crudListener;
   
   private JTable tabela;
   private JButton incluir = new JButton("Incluir");
   private JButton alterar = new JButton("Alterar");
   private JButton gravar = new JButton("Gravar");
   private JButton cancelar = new JButton("Cancelar");
   private JButton excluir = new JButton("Excluir");
   
   private Estado estadoAtual;   
   
   public ControladorCRUD(TableModel<T> tableModel, 
                          PainelCampos<T> painelCampos,
                          CRUDListener<T> crudListener) {
      
      this.tableModel = tableModel;
      this.painelCampos = painelCampos;
      this.crudListener = crudListener;
      
      tableModel.setControlador(this);
      
      tabela = new JTable(tableModel);
      tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      tabela.addMouseListener(new CliqueTabela());
      
      incluir.addActionListener(new AcaoIncluir());
      alterar.addActionListener(new AcaoAlterar());
      gravar.addActionListener(new AcaoGravar());
      cancelar.addActionListener(new AcaoCancelar());
      excluir.addActionListener(new AcaoExcluir());
      
      habilitarEdicao(false);
      estadoAtual = Estado.NAVEGANDO;
   }
   
   public void disparaIncluir() {
       if (!crudListener.antesBotaoIncluir())
           return;
        
       tabela.getSelectionModel().clearSelection();;
       painelCampos.limpar();
       habilitarEdicao(true);
       estadoAtual = Estado.INCLUINDO;
        
       crudListener.aposBotaoIncluir();
   }
   
   public void disparaAlterar() {
       if (!crudListener.antesBotaoAlterar())
           return;
        
       habilitarEdicao(true);
       estadoAtual = Estado.ALTERANDO;
       
       crudListener.aposBotaoAlterar();
   }
   
   public void disparaGravar() throws Exception {
		if (!crudListener.antesBotaoGravar())
			return;

		if (estadoAtual == Estado.INCLUINDO) {
			T objeto = painelCampos.novoObjeto();

			crudListener.acaoGravarInclusao(objeto);
			tableModel.incluir(objeto);
			int indice = tableModel.getRowCount() - 1;
			tabela.getSelectionModel().setSelectionInterval(indice, indice);
		} 
		else {
			int selecionado = tabela.getSelectedRow();
			T objeto = painelCampos.objetoSendoAlterado();

			crudListener.acaoGravarAlteracao(objeto);
			tableModel.alterou(selecionado, objeto);
		}
		
        int selecionado = tabela.getSelectedRow();
        T objeto = tableModel.get(selecionado);
        painelCampos.exibir(objeto);
        habilitarEdicao(false);
        crudListener.aposBotaoGravar();
        estadoAtual = Estado.NAVEGANDO;
   }
   
   public void disparaCancelar() {
       if (!crudListener.antesBotaoCancelar())
           return;
        
        int selecionado = tabela.getSelectedRow();
        
        if (selecionado != -1) 
           painelCampos.exibir(tableModel.get(selecionado));
        else
           painelCampos.limpar();
        
        habilitarEdicao(false);
        estadoAtual = Estado.NAVEGANDO;
        
        crudListener.aposBotaoCancelar();
   }
   
   public void disparaExcluir() throws Exception {
  	 if (!crudListener.antesBotaoExcluir())
		 return;
	 
	 int selecionado = tabela.getSelectedRow();
	 T objeto = tableModel.get(selecionado);
	 
     crudListener.acaoExcluir(objeto);
     tableModel.excluir(selecionado);
     painelCampos.limpar();
     habilitarEdicao(false);
     crudListener.aposBotaoExcluir();
   }
   
   private void habilitarEdicao(boolean habilitar) {
      boolean temLinhaSelecionada = (tabela.getSelectedRow() != -1);
      
      tabela.setEnabled(!habilitar);
      
      incluir.setEnabled(!habilitar);
      alterar.setEnabled(!habilitar && temLinhaSelecionada);
      gravar.setEnabled(habilitar);
      cancelar.setEnabled(habilitar);
      excluir.setEnabled(!habilitar && temLinhaSelecionada);
      
      painelCampos.habilitarCampos(habilitar);
   }
   
   private class AcaoIncluir implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
    	  disparaIncluir();
      }
   }
   
   private class AcaoAlterar implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
    	  disparaAlterar();
      }
   }
   
   private class AcaoGravar implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
         try {
        	 disparaGravar();
         }
         catch (CRUDException crex) {
            showMessageDialog(null, crex.getMessage(), crex.getTitulo(),
                  WARNING_MESSAGE);;
         }
         catch (Exception ex) {
            showMessageDialog(null, ex.getMessage(), "ERRO AO GRAVAR", ERROR_MESSAGE);
            ex.printStackTrace();
         }
      }
   }
   
   private class AcaoCancelar implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
    	  disparaCancelar();
      }
   }
   
   private class AcaoExcluir implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
         try {
        	 disparaExcluir();
         } 
         catch (Exception ex) {
            showMessageDialog(null, ex.getMessage(), "ERRO AO EXCLUIR", 
                  ERROR_MESSAGE);
            ex.printStackTrace();
         }
         
      }
   }
   
   private class CliqueTabela extends MouseAdapter {
      
      @Override
      public void mouseClicked(MouseEvent e) {
         if (!tabela.isEnabled())
            return;
         
         if (e.getClickCount() == 2) {
            alterar.doClick();
         }
         else {
            habilitarEdicao(false);
            int selecionado = tabela.getSelectedRow();
            
            if (selecionado != -1) {
               T objeto = tableModel.get(selecionado);
               painelCampos.exibir(objeto);
            }
            else {
               painelCampos.limpar();
            }
         }
      }
      
   }
   
   public void limpouTela() {
      habilitarEdicao(false);
      painelCampos.limpar();
   }
   
   public TableModel<T> getTableModel() {
	   return tableModel;
   }
   
   public PainelCampos<T> getPainelCampos() {
	   return painelCampos;
   }
   
   public CRUDListener<T> getCRUDListener() {
	   return crudListener;
   }
   
   public JTable getTabela() {
      return tabela;
   }
   
   public JButton getIncluir() {
      return incluir;
   }

   public JButton getAlterar() {
      return alterar;
   }

   public JButton getGravar() {
      return gravar;
   }

   public JButton getCancelar() {
      return cancelar;
   }

   public JButton getExcluir() {
      return excluir;
   }
   
   public Estado getEstado() {
	   return estadoAtual;
   }
   
}
