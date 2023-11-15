package com.application.labgui.Controller;

import com.application.labgui.Domain.Utilizator;
import com.application.labgui.Service.Service;
import com.application.labgui.Utils.Events.ServiceChangeEvent;
import com.application.labgui.Utils.Observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SocialNetworkController implements Observer<ServiceChangeEvent> {

    private Service serviceSocialNetwork;

    ObservableList<Utilizator> model = FXCollections.observableArrayList();

    @FXML
    TableView<Utilizator> utilizatorTableView;
    @FXML
    TableColumn<Utilizator, Long> columnID;
    @FXML
    TableColumn<Utilizator, String> columnFirstName;
    @FXML
    TableColumn<Utilizator, String> columnLastName;


    @Override
    public void update(ServiceChangeEvent eventUpdate) {
//        initModel();
    }

    public void initialize() {
        columnID.setCellValueFactory(new PropertyValueFactory<Utilizator, Long>("id"));
        columnFirstName.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstname"));
        columnLastName.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastname"));
//        utilizatorTableView.setItems(model);
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
}
