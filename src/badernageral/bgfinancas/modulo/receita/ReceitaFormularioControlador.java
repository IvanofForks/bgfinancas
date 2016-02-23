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

package badernageral.bgfinancas.modulo.receita;

import badernageral.bgfinancas.biblioteca.contrato.Categoria;
import badernageral.bgfinancas.biblioteca.contrato.ControladorFormulario;
import badernageral.bgfinancas.biblioteca.utilitario.Animacao;
import badernageral.bgfinancas.biblioteca.sistema.Botao;
import badernageral.bgfinancas.biblioteca.utilitario.Erro;
import badernageral.bgfinancas.biblioteca.utilitario.Validar;
import badernageral.bgfinancas.biblioteca.contrato.Item;
import badernageral.bgfinancas.biblioteca.sistema.Janela;
import badernageral.bgfinancas.biblioteca.sistema.Kernel;
import badernageral.bgfinancas.biblioteca.tipo.Acao;
import badernageral.bgfinancas.biblioteca.tipo.Duracao;
import badernageral.bgfinancas.biblioteca.tipo.Operacao;
import badernageral.bgfinancas.biblioteca.tipo.Status;
import badernageral.bgfinancas.biblioteca.utilitario.Datas;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import badernageral.bgfinancas.template.botao.BotaoFormulario;
import badernageral.bgfinancas.template.botao.BotaoListaCategoria;
import badernageral.bgfinancas.modelo.Conta;
import badernageral.bgfinancas.modelo.Receita;
import badernageral.bgfinancas.modelo.ReceitaItem;
import badernageral.bgfinancas.modulo.conta.ContaFormularioControlador;
import badernageral.bgfinancas.modulo.receita.item.ReceitaItemFormularioControlador;
import badernageral.bgfinancas.template.botao.BotaoListaItem;
import java.time.LocalDate;
import javafx.scene.control.TitledPane;

public final class ReceitaFormularioControlador implements Initializable, ControladorFormulario {
       
    @FXML private TitledPane formulario;
    @FXML private Label labelItem;
    @FXML private Label labelConta;
    @FXML private Label labelDescricao;
    @FXML private Label labelValor;
    @FXML private Label labelData;
    @FXML private BotaoListaItem itemController;
    @FXML private BotaoListaCategoria contaController;
    @FXML private TextField descricao;
    @FXML private TextField valor;
    @FXML private DatePicker data;
    @FXML private BotaoFormulario botaoController;
    
    private Receita Modelo;
    
    private Acao acao;
    private ReceitaControlador controlador = null;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        formulario.setText(idioma.getMensagem("receita"));
        Botao.prepararBotaoModal(this, botaoController, itemController, contaController);
        labelItem.setText(idioma.getMensagem("item")+":");
        labelConta.setText(idioma.getMensagem("conta")+":");
        labelData.setText(idioma.getMensagem("data")+":");
        labelDescricao.setText(idioma.getMensagem("descricao")+":");
        labelValor.setText(idioma.getMensagem("valor")+":");
        new ReceitaItem().montarSelectItem(itemController.getComboItem());
        new Conta().montarSelectCategoria(contaController.getComboCategoria());
    }
    
    @Override
    public void acaoCancelar() {
        Animacao.fadeInOutClose(formulario);
    }
    
    @Override
    public void acaoCadastrar(int botao) {
        if(botao==1){
            Animacao.fadeOutInvisivel(itemController.getComboItem(), formulario);
            ReceitaItemFormularioControlador controladorItem = Janela.abrir(ReceitaItem.FXML_FORMULARIO, idioma.getMensagem("receita"));
            controladorItem.cadastrar(this, itemController.getComboItem().getEditor().getText());
        }else{
            Animacao.fadeOutInvisivel(contaController.getComboCategoria(), formulario);
            ContaFormularioControlador controladorConta = Janela.abrir(Conta.FXML_FORMULARIO, idioma.getMensagem("receita"));
            controladorConta.cadastrar(this);
        }
    }
    
    @Override
    public void selecionarComboItem(int combo, Item item) {
        if(item!=null){
            new ReceitaItem().montarSelectItem(itemController.getComboItem());
            itemController.setItemSelecionado(item);
        }
        Animacao.fadeInInvisivel(itemController.getComboItem(), formulario);
    }
    
    @Override
    public void selecionarComboCategoria(int combo, Categoria categoria) {
        if(categoria!=null){
            new Conta().montarSelectCategoria(contaController.getComboCategoria());
            contaController.setCategoriaSelecionada(categoria);
        }
        Animacao.fadeInInvisivel(contaController.getComboCategoria(), formulario);
    }
    
    public void cadastrar(ReceitaControlador controlador){
        acao = Acao.CADASTRAR;
        this.controlador = controlador;
        data.setValue(LocalDate.now());
        botaoController.setTextBotaoFinalizar(idioma.getMensagem("cadastrar"));
    }
    
    public void alterar(Receita modelo){
        Modelo = modelo;
        botaoController.setTextBotaoFinalizar(idioma.getMensagem("alterar"));
        ReceitaItem item = new ReceitaItem().setIdItem(Modelo.getIdItem()).consultar();
        if(item != null){
            itemController.setItemSelecionado(item);
        }
        Conta conta = new Conta().setIdCategoria(Modelo.getIdConta()).consultar();
        if(conta != null){
            contaController.setCategoriaSelecionada(conta);
        }
        itemController.getComboItem().setDisable(true);
        itemController.getBotaoCadastrar().setDisable(true);
        data.setValue(Modelo.getDataLocal());
        descricao.setText(Modelo.getDescricao());
        valor.setText(Modelo.getValor());
    }

    @Override
    public void acaoFinalizar(){
        if(validarFormulario()){
            if(acao == Acao.CADASTRAR){
                Receita item = new Receita(null, contaController.getIdCategoria(), itemController.getIdItem(), descricao.getText(), valor.getText(), data.getValue(), Datas.getHoraAtual());
                item.cadastrar();
                new Conta().alterarSaldo(Operacao.INCREMENTAR, contaController.getIdCategoria(), valor.getText());
                Kernel.principal.acaoReceita();
                Janela.showTooltip(Status.SUCESSO, idioma.getMensagem("operacao_sucesso"), Duracao.CURTA);
                Animacao.fadeInOutClose(formulario);
            }else{
                Boolean contaMudou = !(Modelo.getIdConta().equals(contaController.getIdCategoria()));
                if(contaMudou){
                    new Conta().alterarSaldo(Operacao.DECREMENTAR, Modelo.getIdConta(), Modelo.getValor());
                    new Conta().alterarSaldo(Operacao.INCREMENTAR, contaController.getIdCategoria(), Modelo.getValor());
                }
                Modelo.setIdConta(contaController.getComboCategoria().getValue());
                Boolean valorMudou = !(Modelo.getValor().equals(valor.getText()));
                if(valorMudou){
                    BigDecimal valorDiferenca = new BigDecimal(Modelo.getValor());
                    valorDiferenca = valorDiferenca.subtract(new BigDecimal(valor.getText()));
                    new Conta().alterarSaldo(Operacao.DECREMENTAR, Modelo.getIdConta(), valorDiferenca.toString());
                }
                Modelo.setValor(valor.getText());
                Modelo.setDescricao(descricao.getText());
                Modelo.setData(data.getValue());
                Modelo.alterar();
                Kernel.principal.acaoReceita();
                Janela.showTooltip(Status.SUCESSO, idioma.getMensagem("operacao_sucesso"), Duracao.CURTA);
                Animacao.fadeInOutClose(formulario);
            }
        }
    }
    
    private boolean validarFormulario(){
        try {
            Validar.autoFiltro(itemController.getAutoFiltro(), itemController.getComboItem());
            Validar.comboBox(contaController.getComboCategoria());
            Validar.datePicker(data);
            Validar.textFieldDecimal(valor);
            return true;
        } catch (Erro ex) {
            return false;
        }
    }
    
    public void lancamentoGuiado(Item item){
        itemController.setItemSelecionado(item);
        itemController.getComboItem().setDisable(true);
        itemController.getBotaoCadastrar().setDisable(true);
        itemController.getBotaoCadastrar().setVisible(false);
    }

}
