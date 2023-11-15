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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

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
            if(utilizator == null)
                prieteniTableView.setVisible(false);
            else {
                prieteniTableView.setVisible(true);
                reloadFriendsModel(utilizator.getId());
            }
        }));
    }

    public void handleDeleteUtilizator(ActionEvent actionEvent){
        Utilizator utilizator = utilizatorTableView.getSelectionModel().getSelectedItem();
        if(utilizator == null){
            System.out.println("Eroare aici");
            return;
        }
        try {
            serviceSocialNetwork.deleteUtilizator(utilizator.getId());
            System.out.println("A mers!");
        }
        catch (AppException appException){
            System.out.println(appException);
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
    }

    public void setServiceSocialNetwork(Service serviceSocialNetwork) {
        this.serviceSocialNetwork = serviceSocialNetwork;
        serviceSocialNetwork.addObserver(this);
        initModel();
    }
    public void clearSelectionMainTable(){
        this.utilizatorTableView.getSelectionModel().clearSelection();
    }
}
