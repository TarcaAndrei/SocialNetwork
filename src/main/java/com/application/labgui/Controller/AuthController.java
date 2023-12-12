package com.application.labgui.Controller;

import com.application.labgui.AppExceptions.AppException;
import com.application.labgui.Domain.Utilizator;
import com.application.labgui.Service.Service;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AuthController {

    public TextField usernameField;
    public PasswordField passwordField;
    public Button loginButton;
    public Button cancelButton;

    private AuthType authType;

    private Service service;
    private Stage dialogStage;
    private SocialNetworkController parent;


    public void handlerLogin(ActionEvent actionEvent) {
        //colectezi datele si incerci prostia aia...
        var username = usernameField.getText();
        var parola = passwordField.getText();
        try{
            Utilizator utilizator = this.service.loginUser(username, parola);
            this.parent.authSuccesful(authType, utilizator);
            this.dialogStage.close();
        }
        catch (AppException e){
            MessageAlert.showMessage(null, Alert.AlertType.ERROR, "EROARE", e.getMessage());
        }
    }

    public void handlerCancel(ActionEvent actionEvent) {
        this.dialogStage.close();
    }

    public void initAuthController(Service serviceSocialNetwork, Stage dialogStage, SocialNetworkController socialNetworkController, AuthType authType) {
        this.service = serviceSocialNetwork;
        this.parent = socialNetworkController;
        this.dialogStage = dialogStage;
        this.authType = authType;
    }



}
