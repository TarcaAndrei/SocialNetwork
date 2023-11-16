package com.application.labgui;

import com.application.labgui.Controller.SocialNetworkController;
import com.application.labgui.Domain.Prietenie;
import com.application.labgui.Domain.Tuplu;
import com.application.labgui.Domain.Utilizator;
import com.application.labgui.Presentation.Consola;
import com.application.labgui.Repository.DBConnection;
import com.application.labgui.Repository.PrietenieDBRepository;
import com.application.labgui.Repository.Repository;
import com.application.labgui.Repository.UtilizatorDBRepository;
import com.application.labgui.Service.Service;
import com.application.labgui.Validators.ValidatorStrategies;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableRow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.Console;
import java.io.IOException;

public class SocialNetworkApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("views/socialnetwork-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 450);
        intialize(fxmlLoader);
//        clearSelectionClickOutside(fxmlLoader, scene);
        stage.setTitle("Social Network");
        stage.setScene(scene);
        stage.show();
        //TODO: asta de integrat undeva idk unde

    }

    private void clearSelectionClickOutside(FXMLLoader fxmlLoader, Scene scene){
        SocialNetworkController controller = fxmlLoader.getController();
        scene.addEventFilter(MouseEvent.MOUSE_CLICKED, evt -> {
            Node source = evt.getPickResult().getIntersectedNode();
            while (source != null && !(source instanceof TableRow)) {
                source = source.getParent();
            }
            if (source == null || (source instanceof TableRow && ((TableRow) source).isEmpty())) {
                controller.clearSelectionMainTable();
            }
        });
    }


    private void intialize(FXMLLoader fxmlLoader){
        ValidatorStrategies validatorStrategies = ValidatorStrategies.UTILIZATOR;
        ValidatorStrategies validatorPrietenieStrategie = ValidatorStrategies.PRIETENIE;
        DBConnection dbConnection = new DBConnection();
        Repository<Long, Utilizator> userDBRepository = new UtilizatorDBRepository(dbConnection, ValidatorStrategies.UTILIZATOR);
        Repository<Tuplu<Long, Long>, Prietenie> prietenieDBRepository = new PrietenieDBRepository(dbConnection, ValidatorStrategies.PRIETENIE);
        Service serviceApp = new Service(userDBRepository, prietenieDBRepository, validatorStrategies, validatorPrietenieStrategie);
        SocialNetworkController socialNetworkController = fxmlLoader.getController();
        socialNetworkController.setServiceSocialNetwork(serviceApp);
//        Consola consola = new Consola(serviceApp);
//        consola.run();
    }
    public static void main(String[] args) {
        launch();
    }
}