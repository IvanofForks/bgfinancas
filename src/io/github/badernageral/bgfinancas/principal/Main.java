/*
Copyright 2012-2018 Jose Robson Mariano Alves

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

package io.github.badernageral.bgfinancas.principal;

import io.github.badernageral.bgfinancas.biblioteca.sistema.Atalho;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Kernel;
import io.github.badernageral.bgfinancas.biblioteca.banco.Conexao;
import io.github.badernageral.bgfinancas.biblioteca.banco.Database;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Janela;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Status;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Datas;
import io.github.badernageral.bgfinancas.idioma.Linguagem;
import io.github.badernageral.bgfinancas.modelo.Configuracao;
import io.github.badernageral.bgfinancas.modelo.Despesa;
import io.github.badernageral.bgfinancas.modelo.Receita;
import io.github.badernageral.bgfinancas.modelo.Usuario;
import io.github.badernageral.bgfinancas.modulo.usuario.UsuarioFormularioControlador;
import io.github.badernageral.bgfinancas.modulo.utilitario.ConfiguracaoFormularioControlador;
import java.io.IOException;
import java.time.LocalDate;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    private final Conexao banco = Conexao.getInstance();
    private final Linguagem idioma = Linguagem.getInstance();
    private ConfiguracaoFormularioControlador confControlador = null;
    private UsuarioFormularioControlador userControlador = null;

    @Override
    public void start(Stage palco) {
        Kernel.main = this;
        Kernel.palco = palco;
        Database.verificarBanco();
        idioma.carregarIdioma();
        if(new Usuario().listar().isEmpty()){
            iniciarPrimeiroAcesso();
        }else{
            if(Configuracao.getPropriedade("login").equals("1")){
                iniciarLogin();
            }else{
                try {
                    palco.setScene(criarCena(criarPainelPrincipal(),null));
                    notificarPlanejamento();
                } catch (Exception ex) {
                    Janela.showException(ex);
                }
            }
        }
        carregarIcone();
        palco.show();
    }

    private void iniciarLogin() {
        try {
            Kernel.palco.setScene(criarCena(criarPainelLogin(),null));
            Kernel.palco.initStyle(StageStyle.TRANSPARENT);
        } catch (IOException ex) {
            Janela.showException(ex);
        }
    }
    
    private void iniciarPrimeiroAcesso() {
        try {
            Kernel.palco.setScene(criarCena(null,criarPainelConfiguracoes()));
            Kernel.palco.initStyle(StageStyle.TRANSPARENT);
            confControlador.setPrimeiroAcesso();
        } catch (IOException ex) {
            Janela.showException(ex);
        }
    }
    
    public void continuarPrimeiroAcesso() {
        try {
            idioma.carregarIdioma();
            Database.popularBanco();
            Kernel.palco.setScene(criarCena(null,criarPainelCadastroUsuario()));
            userControlador.setPrimeiroAcesso();
        } catch (IOException ex) {
            Janela.showException(ex);
        }
    }

    public void reiniciar() {
        try {
            idioma.carregarIdioma();
            Kernel.palco.hide();
            Kernel.palco = new Stage();
            Kernel.palco.setScene(criarCena(criarPainelPrincipal(),null));
            Kernel.palco.show();
            carregarIcone();
            notificarPlanejamento();
        } catch (Exception ex) {
            Janela.showException(ex);
        }
    }
    
    private void notificarPlanejamento(){
        if(new Despesa().isDespesasAtrasadas() || new Receita().isReceitasAtrasadas()){
            Janela.showMensagem(Status.ADVERTENCIA, idioma.getMensagem("planejamento_atrasado"));
            Configuracao data_notificacao = new Configuracao().setNome("data_notificacao").consultar();
            data_notificacao.setValor(Datas.toSqlData(LocalDate.now()));
            data_notificacao.alterar();
        }
    }

    private Scene criarCena(Pane painel, TitledPane tpainel) {
        Scene cena = (painel!=null) ? new Scene(painel) : new Scene(tpainel);
        cena.getStylesheets().add(getClass().getResource(Kernel.CSS_PRINCIPAL).toExternalForm());
        cena.getStylesheets().add(getClass().getResource(Kernel.CSS_AJUDA).toExternalForm());
        cena.getStylesheets().add(getClass().getResource(Kernel.CSS_TOOLTIP).toExternalForm());
        Kernel.cena = cena;
        Atalho.setAtalhos();
        return cena;
    }

    private Pane criarPainelLogin() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        Pane painel = loader.load(getClass().getResourceAsStream(Kernel.FXML_LOGIN));
        return painel;
    }
    
    private TitledPane criarPainelConfiguracoes() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        TitledPane painel = loader.load(getClass().getResourceAsStream(Configuracao.FXML_FORMULARIO));
        confControlador = loader.getController();
        return painel;
    }
    
    private TitledPane criarPainelCadastroUsuario() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        TitledPane painel = loader.load(getClass().getResourceAsStream(Usuario.FXML_FORMULARIO));
        userControlador = loader.getController();
        return painel;
    }

    private Pane criarPainelPrincipal() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        Pane painel = loader.load(getClass().getResourceAsStream(Kernel.FXML_PRINCIPAL));
        Kernel.palco.setOnCloseRequest(e -> { Platform.exit(); });
        return painel;
    }
    
    private void carregarIcone(){
        Kernel.palco.getIcons().add(new Image(Kernel.RAIZ+"/recursos/imagem/layout/icone.png"));
    }

    public static void main(String[] args) {
        launch(args);
    }

}
