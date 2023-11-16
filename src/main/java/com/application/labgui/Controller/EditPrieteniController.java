package com.application.labgui.Controller;

import com.application.labgui.AppExceptions.AppException;
import com.application.labgui.Domain.Utilizator;
import com.application.labgui.Service.Service;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditPrieteniController {

    @FXML
    TextField idUtilizator1;
    @FXML
    TextField idUtilizator2;
    @FXML
    Button saveButton;
    @FXML
    Button cancelButton;
    @FXML
    ComboBox utilizationComboBox;

    private Service service;
    private Stage dialogStage;
    private Utilizator utilizator;

    public void setService(Service service, Stage dialogStage, Utilizator utilizator){
        this.service = service;
        this.dialogStage = dialogStage;
        this.utilizator = utilizator;
        this.idUtilizator1.setEditable(false);
        loadPossibleFriends();
    }
    private void loadPossibleFriends(){
        this.idUtilizator1.setText(utilizator.getId().toString());
        var listaNeprieteni = "IDK";
    }

    public void handlerCancel(ActionEvent actionEvent) {
        this.dialogStage.close();
    }

    public void handlerSave(ActionEvent actionEvent) {
        //idk
        this.dialogStage.close();
    }
}
