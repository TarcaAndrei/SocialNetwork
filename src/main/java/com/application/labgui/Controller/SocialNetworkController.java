package com.application.labgui.Controller;

import com.application.labgui.AppExceptions.AppException;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.StreamSupport;

public class SocialNetworkController implements Observer<ServiceChangeEvent> {
    private Service serviceSocialNetwork;

    ObservableList<Utilizator> model = FXCollections.observableArrayList();
    ObservableList<Utilizator> modelPrieteni = FXCollections.observableArrayList();

    @FXML
    TableView<Utilizator> utilizatorTableView;
    @FXML
    TableColumn<Utilizator, Long> columnID;
    @FXML
    TableColumn<Utilizator, String> columnFirstName;
    @FXML
    TableColumn<Utilizator, String> columnLastName;


    @FXML
    TableView<Utilizator> prieteniTableView;
    @FXML
    TableColumn<Utilizator, Long> columnID1;
    @FXML
    TableColumn<Utilizator, String> columnFirstName1;
    @FXML
    TableColumn<Utilizator, String> columnLastName1;

    @FXML
    HBox hBoxTables;

    @Override
    public void update(ServiceChangeEvent eventUpdate) {
        initModel();
    }

    public void initialize() {
        prieteniTableView.setVisible(false);
        columnID.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));//numele din domeniu al atributului
        columnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        columnID1.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnFirstName1.setCellValueFactory(new PropertyValueFactory<>("firstName"));//numele din domeniu al atributului
        columnLastName1.setCellValueFactory(new PropertyValueFactory<>("lastName"));
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
        Iterable<Utilizator> listaUsers = serviceSocialNetwork.relatiiDePrietenie(idUser);
        List<Utilizator> utilizatorList = StreamSupport.stream(listaUsers.spliterator(), false).toList();
        modelPrieteni.setAll(utilizatorList);
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

    //TODO Trebe sa fac link intre asta si noua fereastra care o sa se deschida
}
