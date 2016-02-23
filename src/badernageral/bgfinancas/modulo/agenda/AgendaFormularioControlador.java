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

package badernageral.bgfinancas.modulo.agenda;

import badernageral.bgfinancas.biblioteca.contrato.Categoria;
import badernageral.bgfinancas.biblioteca.contrato.ControladorFormulario;
import badernageral.bgfinancas.biblioteca.utilitario.Animacao;
import badernageral.bgfinancas.biblioteca.sistema.Botao;
import badernageral.bgfinancas.biblioteca.utilitario.Erro;
import badernageral.bgfinancas.biblioteca.sistema.Kernel;
import badernageral.bgfinancas.biblioteca.utilitario.Validar;
import badernageral.bgfinancas.biblioteca.contrato.Item;
import badernageral.bgfinancas.biblioteca.sistema.Janela;
import badernageral.bgfinancas.biblioteca.tipo.Acao;
import badernageral.bgfinancas.biblioteca.tipo.Duracao;
import badernageral.bgfinancas.biblioteca.tipo.Status;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import badernageral.bgfinancas.template.botao.BotaoFormulario;
import badernageral.bgfinancas.template.botao.BotaoListaCategoria;
import badernageral.bgfinancas.modelo.Agenda;
import badernageral.bgfinancas.modelo.AgendaTipo;
import badernageral.bgfinancas.modulo.agenda.tipo.AgendaTipoFormularioControlador;
import javafx.scene.control.TitledPane;

public final class AgendaFormularioControlador implements Initializable, ControladorFormulario {
       
    private Acao acao;
    
    @FXML private TitledPane formulario;
    @FXML private BotaoListaCategoria categoriaController;
    @FXML private BotaoFormulario botaoController;
    @FXML private Label labelTipo;
    @FXML private Label labelDescricao;
    @FXML private Label labelData;
    @FXML private Label labelValor;
    @FXML private TextField descricao;
    @FXML private DatePicker data;
    @FXML private TextField valor;
    
    private Agenda Modelo;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        formulario.setText(idioma.getMensagem("lembrete"));
        Botao.prepararBotaoModal(this, botaoController, categoriaController);
        labelTipo.setText(idioma.getMensagem("tipo")+":");
        labelDescricao.setText(idioma.getMensagem("descricao")+":");
        labelData.setText(idioma.getMensagem("data")+":");
        labelValor.setText(idioma.getMensagem("valor")+":");
        new AgendaTipo().montarSelectCategoria(categoriaController.getComboCategoria());
    }
    
    @Override
    public void acaoCancelar() {
        Animacao.fadeInOutClose(formulario);
    }
    
    @Override
    public void acaoCadastrar(int botao) {
        Animacao.fadeOutInvisivel(descricao, formulario);
        AgendaTipoFormularioControlador Controlador = Janela.abrir(AgendaTipo.FXML_FORMULARIO, idioma.getMensagem("lembrete"));
        Controlador.cadastrar(this);
    }
    
    @Override
    public void selecionarComboCategoria(int combo, Categoria agendaTipo) {
        new AgendaTipo().montarSelectCategoria(categoriaController.getComboCategoria());
        categoriaController.setCategoriaSelecionada(agendaTipo);
        Animacao.fadeInInvisivel(descricao, formulario);
    }
    
    public void cadastrar(Categoria agendaTipo){
        acao = Acao.CADASTRAR;
        botaoController.setTextBotaoFinalizar(idioma.getMensagem("cadastrar"));
        if(!agendaTipo.getIdCategoria().equals("todas")){
            categoriaController.setCategoriaSelecionada(agendaTipo);
        }
    }
    
    public void alterar(Agenda modelo){
        Modelo = modelo;
        acao = Acao.ALTERAR;
        botaoController.setTextBotaoFinalizar(idioma.getMensagem("alterar"));
        AgendaTipo agendaTipo = new AgendaTipo().setIdCategoria(Modelo.getIdCategoria()).consultar();
        if(agendaTipo != null){
            categoriaController.setCategoriaSelecionada(agendaTipo);
        }
        descricao.setText(Modelo.getNome());
        data.setValue(Modelo.getDataLocal());
        valor.setText(Modelo.getValor());
    }
    
    @Override
    public void acaoFinalizar(){
        if(validarFormulario()){
            if(acao == Acao.CADASTRAR){
                Agenda lembrete = new Agenda(null, categoriaController.getIdCategoria(), descricao.getText(), data.getValue(), valor.getText(), null);
                lembrete.cadastrar();
                Kernel.principal.acaoAgenda();
                Janela.showTooltip(Status.SUCESSO, idioma.getMensagem("operacao_sucesso"), Duracao.CURTA);
                Animacao.fadeInOutClose(formulario);
            }else if(acao == Acao.ALTERAR){
                Agenda lembrete = new Agenda(Modelo.getIdItem(), categoriaController.getIdCategoria(), descricao.getText(), data.getValue(), valor.getText(), null);
                lembrete.alterar();
                Kernel.controlador.acaoFiltrar(true);
                Janela.showTooltip(Status.SUCESSO, idioma.getMensagem("operacao_sucesso"), Duracao.CURTA);
                Animacao.fadeInOutClose(formulario);
            }
        }
    }
    
    private boolean validarFormulario(){
        try {
            Validar.comboBox(categoriaController.getComboCategoria());
            Validar.textField(descricao);
            Validar.datePicker(data);
            Validar.textFieldDecimal(valor);
            return true;
        } catch (Erro ex) {
            return false;
        }
    }

    @Override
    public void selecionarComboItem(int combo, Item item) {
        System.out.println(idioma.getMensagem("nao_implementado"));
    }

}
