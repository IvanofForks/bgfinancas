/*
Copyright 2012-2015 Jose Robson Mariano Alves

This file is part of bgfinancas.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This package is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.

*/

package badernageral.bgfinancas.modulo.transferencia.item;

import badernageral.bgfinancas.biblioteca.ajuda.Ajuda;
import badernageral.bgfinancas.biblioteca.utilitario.Animacao;
import badernageral.bgfinancas.biblioteca.contrato.Categoria;
import badernageral.bgfinancas.biblioteca.contrato.Controlador;
import badernageral.bgfinancas.biblioteca.sistema.Kernel;
import badernageral.bgfinancas.biblioteca.sistema.Tabela;
import badernageral.bgfinancas.biblioteca.utilitario.Validar;
import badernageral.bgfinancas.biblioteca.sistema.Janela;
import badernageral.bgfinancas.biblioteca.tipo.Duracao;
import badernageral.bgfinancas.biblioteca.tipo.Posicao;
import badernageral.bgfinancas.biblioteca.tipo.Status;
import badernageral.bgfinancas.biblioteca.utilitario.Erro;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import badernageral.bgfinancas.template.cena.CenaItem;
import badernageral.bgfinancas.modelo.TransferenciaCategoria;
import badernageral.bgfinancas.modelo.TransferenciaItem;

public final class TransferenciaItemControlador implements Initializable, Controlador {
       
    private final String TITULO = idioma.getMensagem("transferencias")+" - "+idioma.getMensagem("itens");
    
    @FXML private CenaItem cenaController;
    private ObservableList<TransferenciaItem> itens;
    private final Tabela<TransferenciaItem> tabela = new Tabela<>();
    private final TableView<TransferenciaItem> tabelaLista = new TableView<>();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Kernel.setTitulo(TITULO);
        cenaController.setTitulo(TITULO);
        new TransferenciaCategoria().montarSelectCategoria(cenaController.getChoiceCategoria());
        cenaController.setTabela(tabelaLista);
        tabela.prepararTabela(tabelaLista);
        tabela.adicionarColuna(tabelaLista, idioma.getMensagem("item"), "nome");
        tabela.adicionarColuna(tabelaLista, idioma.getMensagem("categoria"), "nomeCategoria");
        acaoFiltrar(false);
    }
    
    @Override
    public void acaoFiltrar(Boolean animacao){
        String idCategoria = null;
        Categoria transferenciaCategoria = cenaController.getCategoriaSelecionada();
        if(!transferenciaCategoria.getIdCategoria().equals("todas")) {
            idCategoria = transferenciaCategoria.getIdCategoria();
        }
        tabelaLista.setItems(new TransferenciaItem().setFiltro(cenaController.getFiltro().getText()).setIdCategoria(idCategoria).listar());
        if(animacao){
            Animacao.fadeOutIn(tabelaLista);
        }
    }
    
    @Override
    public void acaoCadastrar(int botao) {
        TransferenciaItemFormularioControlador Controlador = Janela.abrir(TransferenciaItem.FXML_FORMULARIO, TITULO);
        Controlador.cadastrar(cenaController.getCategoriaSelecionada());
    }
    
    @Override
    public void acaoAlterar(int tabela) {
        itens = tabelaLista.getSelectionModel().getSelectedItems();
        if(Validar.alteracao(itens, cenaController.getBotaoAlterar())){
            TransferenciaItemFormularioControlador Controlador = Janela.abrir(TransferenciaItem.FXML_FORMULARIO, TITULO);
            Controlador.alterar(itens.get(0));
        }
    }
    
    @Override
    public void acaoExcluir(int botao) {
        itens = tabelaLista.getSelectionModel().getSelectedItems();
        if(Validar.exclusao(itens, cenaController.getBotaoExcluir())){
            try{
                for(TransferenciaItem i : itens){
                    i.excluir();
                }
                Janela.showTooltip(Status.SUCESSO, idioma.getMensagem("operacao_sucesso"), Duracao.CURTA);
                acaoFiltrar(true);
            } catch (Erro ex) {
                Janela.showTooltip(Status.ERRO, idioma.getMensagem("restricao_excluir"), Duracao.NORMAL);
            }
        }
    }
    
    @Override
    public void acaoGerenciar(int botao) {
        Kernel.principal.acaoTransferenciaCategoria();
    }

    @Override
    public void acaoVoltar() {
        Kernel.principal.acaoVoltar();
    }

    @Override
    public void acaoAjuda() {
        Ajuda.getInstance().setObjetos(cenaController,tabelaLista);
        Ajuda.getInstance().capitulo(Posicao.CENTRO, idioma.getMensagem("tuto_trans_item_1"));
        Ajuda.getInstance().capitulo(cenaController.getBotaoCadastrar(), Posicao.BAIXO, 
                idioma.getMensagem("tuto_cadastrar"));
        Ajuda.getInstance().capitulo(cenaController.getBotaoAlterar(), Posicao.BAIXO, 
                idioma.getMensagem("tuto_alterar"));
        Ajuda.getInstance().capitulo(cenaController.getBotaoExcluir(), Posicao.BAIXO, 
                idioma.getMensagem("tuto_excluir"));
        Ajuda.getInstance().capitulo(cenaController.getChoiceCategoria(), 
                Posicao.BAIXO, idioma.getMensagem("tuto_lista_categoria"));
        Ajuda.getInstance().capitulo(cenaController.getBotaoGerenciarCategoria(), 
                Posicao.BAIXO, idioma.getMensagem("tuto_categoria"));
        Ajuda.getInstance().apresentarProximo();
    }
    
}