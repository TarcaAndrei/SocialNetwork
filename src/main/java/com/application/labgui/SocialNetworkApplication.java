package com.application.labgui;

import com.application.labgui.Controller.SocialNetworkController;
import com.application.labgui.Domain.Prietenie;
import com.application.labgui.Domain.Tuplu;
import com.application.labgui.Domain.Utilizator;
import com.application.labgui.Repository.*;
import com.application.labgui.Service.Service;
import com.application.labgui.Validators.ValidatorStrategies;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableRow;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;

public class SocialNetworkApplication extends Application{
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("views/socialnetwork-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 450);
        intialize(fxmlLoader);
        clearSelectionClickOutside(fxmlLoader, scene);
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
        DBConnection dbConnection = new DBConnection();
        UtilizatorDBRepository userDBRepository = new UtilizatorDBRepository(dbConnection, ValidatorStrategies.UTILIZATOR);
        Repository<Tuplu<Long, Long>, Prietenie> prietenieDBRepository = new PrietenieDBRepository(dbConnection, ValidatorStrategies.PRIETENIE);
        CereriPrieteniiDBRepository repositoryCereriPrietenii = new CereriPrieteniiDBRepository(dbConnection, ValidatorStrategies.CEREREPRIETENIE);
        MesajeDBRepository mesajDBRepository = new MesajeDBRepository(dbConnection);
        Service serviceApp = new Service(userDBRepository, repositoryCereriPrietenii, prietenieDBRepository, mesajDBRepository, ValidatorStrategies.UTILIZATOR, ValidatorStrategies.PRIETENIE);
        SocialNetworkController socialNetworkController = fxmlLoader.getController();
        socialNetworkController.setServiceSocialNetwork(serviceApp);
//        Consola consola = new Consola(serviceApp);
//        consola.run();
    }
    public static void main(String[] args) {
//        Utilizator ceva = null;
//        ceva.getId();
//        tests();
        launch();
    }

    public static void tests(){
        ValidatorStrategies validatorStrategies = ValidatorStrategies.UTILIZATOR;
        ValidatorStrategies validatorPrietenieStrategie = ValidatorStrategies.PRIETENIE;
        DBConnection dbConnection = new DBConnection();
        UtilizatorDBRepository userDBRepository = new UtilizatorDBRepository(dbConnection, ValidatorStrategies.UTILIZATOR);
        Repository<Tuplu<Long, Long>, Prietenie> prietenieDBRepository = new PrietenieDBRepository(dbConnection, ValidatorStrategies.PRIETENIE);
        MesajeDBRepository mesajDBRepository = new MesajeDBRepository(dbConnection);
        CereriPrieteniiDBRepository repositoryCereriPrietenii = new CereriPrieteniiDBRepository(dbConnection, ValidatorStrategies.CEREREPRIETENIE);
        Service serviceApp = new Service(userDBRepository, repositoryCereriPrietenii, prietenieDBRepository, mesajDBRepository, validatorStrategies, validatorPrietenieStrategie);
        System.out.println(serviceApp.getAllMessages());
//        serviceApp.sentNewMessage(10L, Collections.singletonList(14L), "Te rog vreau sa plec!", LocalDateTime.now());
//        var mesaj = serviceApp.findOneMessage(10L).get();
//        serviceApp.sentNewMessage(14L, 10L, "SI EU VREAU SA PLEC", LocalDateTime.now());
        System.out.println(serviceApp.getAllMessages());
    }


}