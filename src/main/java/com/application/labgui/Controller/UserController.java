package com.application.labgui.Controller;

import com.application.labgui.AppExceptions.AppException;
import com.application.labgui.Domain.PrietenieDTO;
import com.application.labgui.Domain.Utilizator;
import com.application.labgui.Service.Service;
import com.application.labgui.Utils.Events.ServiceChangeEvent;
import com.application.labgui.Utils.Observer.Observable;
import com.application.labgui.Utils.Observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.StreamSupport;

public class UserController implements Observer<ServiceChangeEvent> {
    public Button cevaButton;
    private Service service;
    private Utilizator utilizatorLogat;
    public TextField idPrietenNou;
    public ListView<Utilizator> listaCunoscuti;
    public TextField mesajNou;
    public Button sendButton;
    public ScrollPane scrollPane;

    ObservableList<Utilizator> modelPrieteni = FXCollections.observableArrayList();

    public void initUserController(Service service, Utilizator utilizator){
        this.service = service;
        utilizatorLogat = utilizator;
        this.loadListaCunoscuti();
        this.service.addObserver(this);
        listaCunoscuti.getSelectionModel().selectedItemProperty().addListener((observable -> {
            var cunoscut = listaCunoscuti.getSelectionModel().getSelectedItem();
            loadMesaje(cunoscut);
        }));
    }

    private void loadListaCunoscuti(){
        this.service.relatiiDePrietenie(utilizatorLogat.getId());
        listaCunoscuti.setItems(modelPrieteni);
        this.reloadListaCunoscuti();
    }

    private void reloadListaCunoscuti(){
        var listaUsers = this.service.getAllUtilizatori();
        List<Utilizator> utilizatorList = StreamSupport.stream(listaUsers.spliterator(), false).toList();
        modelPrieteni.setAll(utilizatorList);
    }

    private void loadMesaje(Utilizator cunoscut){
        this.cevaButton.setText(cunoscut.getFirstName());
        var listaToateMesajele = this.service.getAllMessagesBetween(utilizatorLogat.getId(), cunoscut.getId());
        VBox vBox = new VBox();
        this.scrollPane.setContent(vBox);
        vBox.setPrefWidth(scrollPane.getPrefWidth() * 2);
        listaToateMesajele.forEach(mesaj->{
            try{
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("views/single_mesaj_view.fxml"));
                AnchorPane root = fxmlLoader.load();
                vBox.getChildren().add(root);
                MesajViewController controllerMesaj = fxmlLoader.getController();
                controllerMesaj.initMesaj(mesaj, utilizatorLogat);
                root.setPrefWidth(vBox.getPrefWidth());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
//        scrollPane.setFitToWidth(true);
//        scrollPane.vvalueProperty().bind(vBox.heightProperty());
    }

    public void sendMesssage(ActionEvent actionEvent) {
        var cunoscut = this.listaCunoscuti.getSelectionModel().getSelectedItem();
        var text_scris = this.mesajNou.getText();
        try{
            this.service.sentNewMessage(utilizatorLogat.getId(), Collections.singletonList(cunoscut.getId()), text_scris, LocalDateTime.now());
            this.mesajNou.clear();
        }
        catch (AppException e){
            MessageAlert.showMessage(null, Alert.AlertType.ERROR, "", e.getMessage());
        }
    }

    @Override
    public void update(ServiceChangeEvent eventUpdate) {
        System.out.println("Ceva");
        var cunoscut = this.listaCunoscuti.getSelectionModel().getSelectedItem();
        this.loadMesaje(cunoscut);
    }
}
