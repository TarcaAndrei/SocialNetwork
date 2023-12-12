package com.application.labgui.Controller;

import com.application.labgui.AppExceptions.AppException;
import com.application.labgui.Domain.PrietenieDTO;
import com.application.labgui.Domain.Tuplu;
import com.application.labgui.Domain.Utilizator;
import com.application.labgui.Repository.Paging.Page;
import com.application.labgui.Repository.Paging.Pageable;
import com.application.labgui.Service.Service;
import com.application.labgui.Utils.Events.ServiceChangeEvent;
import com.application.labgui.Utils.Observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.StreamSupport;

public class SocialNetworkController implements Observer<ServiceChangeEvent> {
    public Button loginUser;
    public Button previousButton;
    public Button nextButton;
    public ChoiceBox<Integer> choiceNumberOfUserPerPage;
    public TableColumn<Utilizator, String> columnUsername;
    private Service serviceSocialNetwork;

    ObservableList<Integer> numberOfElements = FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8);
    ObservableList<Utilizator> model = FXCollections.observableArrayList();
    ObservableList<PrietenieDTO> modelPrieteni = FXCollections.observableArrayList();

    HashMap<Long, Node> listaUseriLogati;

    @FXML
    TableView<Utilizator> utilizatorTableView;
    @FXML
    TableColumn<Utilizator, Long> columnID;
    @FXML
    TableColumn<Utilizator, String> columnFirstName;
    @FXML
    TableColumn<Utilizator, String> columnLastName;

    private int currentPage=0;
    private int numberOfRecordsPerPage = 5;

    private int totalNumberOfElements;

    @FXML
    HBox hBoxTables;

    @Override
    public void update(ServiceChangeEvent eventUpdate) {
        this.currentPage = 0;
        initModel();
    }

    public void init_all() {
        listaUseriLogati = new HashMap<>();
        columnID.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));//numele din domeniu al atributului
        columnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        columnUsername.setCellValueFactory(new PropertyValueFactory<>("userName"));
        utilizatorTableView.setItems(model);
//        prieteniTableView.setItems(modelPrieteni);
        utilizatorTableView.getSelectionModel().selectedItemProperty().addListener((observable -> {
            var utilizator = utilizatorTableView.getSelectionModel().getSelectedItem();
            if(utilizator == null) {
//                prieteniTableView.setVisible(false);
                utilizatorTableView.setPrefHeight(hBoxTables.getHeight());
                utilizatorTableView.setPrefWidth(hBoxTables.getWidth());
            }
            else {
//                prieteniTableView.setVisible(true);
                utilizatorTableView.setPrefHeight(hBoxTables.getHeight()/2);
                utilizatorTableView.setPrefWidth(hBoxTables.getWidth()/2);
//                prieteniTableView.setPrefHeight(hBoxTables.getHeight()/2);
//                prieteniTableView.setPrefWidth(hBoxTables.getWidth()/2);
//                reloadColumns();
                reloadFriendsModel(utilizator.getId());
            }
        }));
        choiceNumberOfUserPerPage.setItems(numberOfElements);
        currentPage = 0;
        numberOfRecordsPerPage = 5;
        choiceNumberOfUserPerPage.setValue(5);
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

    private void showPrieteniAddDialog(Utilizator utilizator) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("views/editprieteni_view.fxml"));
            //aici la resources trb sa fie cam aceeasi chestie ca in folderul celalalt
            AnchorPane root = fxmlLoader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add prietenie");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            EditPrieteniController editPrieteniController = fxmlLoader.getController();
            editPrieteniController.setService(serviceSocialNetwork, dialogStage, utilizator);
            dialogStage.show();
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    public void handleUpdateUtilizator(ActionEvent actionEvent){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("views/authuser_view.fxml"));
            AnchorPane root = fxmlLoader.load();
            Stage loginStage = new Stage();
            loginStage.setTitle("AuthType Page");
            Scene scene = new Scene(root);
            loginStage.setScene(scene);
            AuthController userController = fxmlLoader.getController();
            userController.initAuthController(serviceSocialNetwork, loginStage,this, AuthType.UPDATE);
            loginStage.show();
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
//        showUtilizatorEditDialog(utilizator);
    }

    public void handleDeleteUtilizator(ActionEvent actionEvent){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("views/authuser_view.fxml"));
            AnchorPane root = fxmlLoader.load();
            Stage loginStage = new Stage();
            loginStage.setTitle("AuthType Page");
            Scene scene = new Scene(root);
            loginStage.setScene(scene);
            AuthController userController = fxmlLoader.getController();
            userController.initAuthController(serviceSocialNetwork, loginStage,this, AuthType.DELETE);
            loginStage.show();
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
//        Utilizator utilizator = utilizatorTableView.getSelectionModel().getSelectedItem();
//        if(utilizator == null){
//            MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Eroare", "Nu ai selectat niciun student");
//            return;
//        }
//        try {
//            serviceSocialNetwork.deleteUtilizator(utilizator.getId());
//            MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "", "");
//        }
//        catch (AppException appException){
//            MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "", appException.getMessage());
//        }
    }

    private void reloadFriendsModel(Long idUser){
        Iterable<PrietenieDTO> listaPrieteni = serviceSocialNetwork.relatiiDePrietenie(idUser);
        List<PrietenieDTO> listaDTO = StreamSupport.stream(listaPrieteni.spliterator(), false).toList();
        modelPrieteni.setAll(listaDTO);
    }

    private void initModel(){
        Page<Utilizator> utilizatoriOnCurrentPage = serviceSocialNetwork.getUtilizatoriOnPage(new Pageable(currentPage, numberOfRecordsPerPage));
        totalNumberOfElements = utilizatoriOnCurrentPage.getTotalNumberOfElements();
        List<Utilizator> utilizatorList = StreamSupport.stream(utilizatoriOnCurrentPage.getElementsOnPage().spliterator(), false).toList();
        model.setAll(utilizatorList);
        this.utilizatorTableView.getSelectionModel().clearSelection();
        this.handlePageNavigationChecks();
    }

    public void setServiceSocialNetwork(Service serviceSocialNetwork) {
        this.serviceSocialNetwork = serviceSocialNetwork;
        serviceSocialNetwork.addObserver(this);
        init_all();
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
//        PrietenieDTO prietenieDTO = prieteniTableView.getSelectionModel().getSelectedItem();
        PrietenieDTO prietenieDTO = null;
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

    public void handleAddPrietenie(ActionEvent actionEvent){
        var utilizator = utilizatorTableView.getSelectionModel().getSelectedItem();
        if(utilizator==null){
            MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Eroare", "Nu ai selectat niciun student");
            return;
        }
        showPrieteniAddDialog(utilizator);
    }

    public void handleLoginUser(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("views/authuser_view.fxml"));
            AnchorPane root = fxmlLoader.load();
            Stage loginStage = new Stage();
            loginStage.setTitle("AuthType Page");
            Scene scene = new Scene(root);
            loginStage.setScene(scene);
            AuthController userController = fxmlLoader.getController();
            userController.initAuthController(serviceSocialNetwork, loginStage,this, AuthType.LOGIN);
            loginStage.show();
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
    public void authSuccesful(AuthType authType, Utilizator utilizator) {
//        if(utilizator == null){
//            MessageAlert.showMessage(null, Alert.AlertType.ERROR, "", "Nu ai selectat niciun utilizator");
//            return;
//        }
        var gasit = listaUseriLogati.get(utilizator.getId());
        if(gasit != null){
            MessageAlert.showMessage(null, Alert.AlertType.ERROR, "", "Userul e deja logat!");
            return;
        }
        switch (authType){
            case LOGIN -> loginSuccesful(utilizator);
            case UPDATE -> showUtilizatorEditDialog(utilizator);
            case DELETE -> serviceSocialNetwork.deleteUtilizator(utilizator.getId());
        }
    }

    private void loginSuccesful(Utilizator utilizator){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("views/user_view.fxml"));
            AnchorPane root = fxmlLoader.load();
            Stage userStage = new Stage();
            userStage.setTitle(utilizator.getFirstName());
            this.listaUseriLogati.put(utilizator.getId(), root);
            Scene scene = new Scene(root);
            userStage.setScene(scene);
            userStage.setOnCloseRequest(event -> {
//                System.out.println("Close button was clicked!");
                listaUseriLogati.remove(utilizator.getId());
            });
            UserController userController = fxmlLoader.getController();
            userController.initUserController(serviceSocialNetwork, utilizator);
            userStage.show();
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    private void handlePageNavigationChecks(){
        previousButton.setDisable(currentPage == 0);
        nextButton.setDisable((currentPage+1)*numberOfRecordsPerPage >= totalNumberOfElements);
    }

    public void prevPage(ActionEvent actionEvent) {
        currentPage--;
        initModel();
    }

    public void nextPage(ActionEvent actionEvent) {
        currentPage++;
        initModel();
    }

    public void changePagination(ActionEvent actionEvent) {
        this.numberOfRecordsPerPage = choiceNumberOfUserPerPage.getValue();
        this.currentPage = 0;
        this.initModel();
    }


    //TODO Trebe sa fac link intre asta si noua fereastra care o sa se deschida
}
