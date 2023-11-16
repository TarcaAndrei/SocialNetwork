package com.application.labgui.Controller;

import com.application.labgui.AppExceptions.AppException;
import com.application.labgui.Domain.Prietenie;
import com.application.labgui.Domain.PrietenieDTO;
import com.application.labgui.Domain.Tuplu;
import com.application.labgui.Domain.Utilizator;
import com.application.labgui.Service.Service;
import com.application.labgui.Utils.Events.ServiceChangeEvent;
import com.application.labgui.Utils.Observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.StreamSupport;

public class SocialNetworkController implements Observer<ServiceChangeEvent> {
    private Service serviceSocialNetwork;

    ObservableList<Utilizator> model = FXCollections.observableArrayList();
    ObservableList<PrietenieDTO> modelPrieteni = FXCollections.observableArrayList();

    @FXML
    TableView<Utilizator> utilizatorTableView;
    @FXML
    TableColumn<Utilizator, Long> columnID;
    @FXML
    TableColumn<Utilizator, String> columnFirstName;
    @FXML
    TableColumn<Utilizator, String> columnLastName;


    @FXML
    TableView<PrietenieDTO> prieteniTableView;
    @FXML
    TableColumn<PrietenieDTO, Long> columnID1;
    @FXML
    TableColumn<PrietenieDTO, String> columnFirstName1;
    @FXML
    TableColumn<PrietenieDTO, String> columnLastName1;
    @FXML
    TableColumn<PrietenieDTO, LocalDateTime> columnFriendsFrom;

    @FXML
    HBox hBoxTables;

    @FXML
    Button buttonDeletePrietenie;

    @Override
    public void update(ServiceChangeEvent eventUpdate) {
        initModel();
    }

    public void initialize() {
        prieteniTableView.setVisible(false);
        buttonDeletePrietenie.setVisible(false);
        columnID.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));//numele din domeniu al atributului
        columnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        columnID1.setCellValueFactory(new PropertyValueFactory<>("id2"));
        columnFirstName1.setCellValueFactory(new PropertyValueFactory<>("prenume"));//numele din domeniu al atributului
        columnLastName1.setCellValueFactory(new PropertyValueFactory<>("nume"));
        columnFriendsFrom.setCellValueFactory(new PropertyValueFactory<>("friendsFrom"));
        utilizatorTableView.setItems(model);
        prieteniTableView.setItems(modelPrieteni);
        utilizatorTableView.getSelectionModel().selectedItemProperty().addListener((observable -> {
            var utilizator = utilizatorTableView.getSelectionModel().getSelectedItem();
            if(utilizator == null) {
                prieteniTableView.setVisible(false);
                utilizatorTableView.setPrefHeight(hBoxTables.getHeight());
                utilizatorTableView.setPrefWidth(hBoxTables.getWidth());
//                reloadColumns();
            }
            else {
                prieteniTableView.setVisible(true);
                utilizatorTableView.setPrefHeight(hBoxTables.getHeight()/2);
                utilizatorTableView.setPrefWidth(hBoxTables.getWidth()/2);
                prieteniTableView.setPrefHeight(hBoxTables.getHeight()/2);
                prieteniTableView.setPrefWidth(hBoxTables.getWidth()/2);
//                reloadColumns();
                reloadFriendsModel(utilizator.getId());
            }
        }));
        prieteniTableView.getSelectionModel().selectedItemProperty().addListener((observable -> {
            var prietenie = prieteniTableView.getSelectionModel().getSelectedItem();
            if(prietenie == null){
                buttonDeletePrietenie.setVisible(false);
                return;
            }
            buttonDeletePrietenie.setVisible(true);
        }));
    }

    public void handleAddUtilizator(ActionEvent actionEvent){
        this.showUtilizatorEditDialog(null);
    }

    private void showUtilizatorEditDialog(Utilizator utilizator) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("views/edituser_view.fxml"));
            //aici la resources trb sa fie cam aceeasi chestie ca in folderul celalalt

            AnchorPane root = fxmlLoader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit User");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            EditUtilizatorController editUtilizatorController = fxmlLoader.getController();
            editUtilizatorController.setService(serviceSocialNetwork, dialogStage, utilizator);
            dialogStage.show();
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }

    }

    public void handleUpdateUtilizator(ActionEvent actionEvent){
        Utilizator utilizator = utilizatorTableView.getSelectionModel().getSelectedItem();
        if(utilizator == null){
            MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Eroare", "Nu ai selectat niciun student");
            return;
        }
        showUtilizatorEditDialog(utilizator);
    }

    public void handleDeleteUtilizator(ActionEvent actionEvent){
        Utilizator utilizator = utilizatorTableView.getSelectionModel().getSelectedItem();
        if(utilizator == null){
            MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Eroare", "Nu ai selectat niciun student");
            return;
        }
        try {
            serviceSocialNetwork.deleteUtilizator(utilizator.getId());
            MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "", "");
        }
        catch (AppException appException){
            MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "", appException.getMessage());
        }
    }

    private void reloadFriendsModel(Long idUser){
        Iterable<PrietenieDTO> listaPrieteni = serviceSocialNetwork.relatiiDePrietenie(idUser);
        List<PrietenieDTO> listaDTO = StreamSupport.stream(listaPrieteni.spliterator(), false).toList();
        modelPrieteni.setAll(listaDTO);
    }

    private void initModel(){
        Iterable<Utilizator> listaUsers = serviceSocialNetwork.getAllUtilizatori();
        List<Utilizator> utilizatorList = StreamSupport.stream(listaUsers.spliterator(), false).toList();
        model.setAll(utilizatorList);
        this.utilizatorTableView.getSelectionModel().clearSelection();
    }

    public void setServiceSocialNetwork(Service serviceSocialNetwork) {
        this.serviceSocialNetwork = serviceSocialNetwork;
        serviceSocialNetwork.addObserver(this);
        initModel();
    }
    public void clearSelectionMainTable(){
        this.utilizatorTableView.getSelectionModel().clearSelection();
    }

    private void reloadColumns(){
        columnID.setPrefWidth(utilizatorTableView.getWidth()/5);
        columnLastName.setPrefWidth(2* utilizatorTableView.getWidth()/5);
        columnFirstName.setPrefWidth(2 * utilizatorTableView.getWidth()/5);
    }

    public void handleDeletePrietenie(ActionEvent actionEvent) {
        PrietenieDTO prietenieDTO = prieteniTableView.getSelectionModel().getSelectedItem();
        if(prietenieDTO==null){
            MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Eroare", "Nu ai selectat niciun student");
            return;
        }
        try{
            serviceSocialNetwork.deletePrietenie(new Tuplu<>(prietenieDTO.getId1(), prietenieDTO.getId2()));
        }
        catch (AppException e){
            MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Eroare", e.getMessage());
        }
    }

    //TODO Trebe sa fac link intre asta si noua fereastra care o sa se deschida
}
