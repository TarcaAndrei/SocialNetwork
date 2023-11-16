package com.application.labgui.Controller;

import com.application.labgui.Domain.Utilizator;
import com.application.labgui.Service.Service;
import javafx.fxml.FXML;
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
}
