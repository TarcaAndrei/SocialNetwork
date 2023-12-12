package com.application.labgui.Controller;

import com.application.labgui.AppExceptions.AppException;
import com.application.labgui.Domain.Utilizator;
import com.application.labgui.Service.Service;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditUtilizatorController {
    public TextField usernameTextField;
    public PasswordField passwordField;
    @FXML
    TextField idTextField;
    @FXML
    TextField prenumeTextField;
    @FXML
    TextField numeTextField;
    @FXML
    Button saveButton;

    @FXML
    Button cancelButton;

    private Service service;
    private Stage dialogStage;

    private Utilizator utilizator;

    void setService(Service service, Stage dialogStage, Utilizator utilizator){
        this.service = service;
        this.dialogStage = dialogStage;
        this.utilizator = utilizator;
        this.idTextField.setEditable(false);
        if(this.utilizator != null){
            loadFields(utilizator);
            this.saveButton.setText("Update");
            this.usernameTextField.setEditable(false);
        }
        else{
            this.idTextField.setText("Will be generated");
            this.prenumeTextField.setPromptText("INTRODU PRENUME");
            this.numeTextField.setPromptText("INTRODU NUME");
            this.usernameTextField.setPromptText("INTRODU USERNAME");
            this.passwordField.setPromptText("INTRODU PAROLA");
        }
    }

    private void loadFields(Utilizator utilizator) {
        this.idTextField.setText(utilizator.getId().toString());
        this.prenumeTextField.setText(utilizator.getFirstName());
        this.numeTextField.setText(utilizator.getLastName());
        this.usernameTextField.setText(utilizator.getUserName());
    }

    public void handlerCancel(ActionEvent actionEvent) {
        this.dialogStage.close();
    }

    public void handlerSave(ActionEvent actionEvent) {
        String prenume = this.prenumeTextField.getText();
        String nume = this.numeTextField.getText();
        String username = this.usernameTextField.getText();
        String password = this.passwordField.getText();
        if(utilizator==null) {
            try {
                this.service.addNewUser(nume, prenume, username, password);
                MessageAlert.showMessage(dialogStage, Alert.AlertType.CONFIRMATION, "", "A mers!");
                dialogStage.close();
            } catch (AppException e) {
                MessageAlert.showMessage(dialogStage, Alert.AlertType.ERROR, "EROARE", e.getMessage());
            }
        }
        //altfel inseamna ca e vorba de un update
        else{
            try{
                this.service.updateUser(utilizator.getId(), nume, prenume, username, password);
                MessageAlert.showMessage(dialogStage, Alert.AlertType.CONFIRMATION, "", "A mers!");
                dialogStage.close();
            }catch (AppException e) {
                MessageAlert.showMessage(dialogStage, Alert.AlertType.ERROR, "EROARE", e.getMessage());
            }
        }
    }


    //TODO aici trb sa adaug tot ce trebuie pentru butoane si pt fielduri....
}
