package com.application.labgui.Controller;

import com.application.labgui.AppExceptions.AppException;
import com.application.labgui.Domain.CererePrietenie;
import com.application.labgui.Domain.Mesaj;
import com.application.labgui.Domain.PrietenieDTO;
import com.application.labgui.Domain.Utilizator;
import com.application.labgui.Service.Service;
import com.application.labgui.Utils.Events.ChangeEventType;
import com.application.labgui.Utils.Events.ReplyEvent;
import com.application.labgui.Utils.Events.ServiceChangeEvent;
import com.application.labgui.Utils.Observer.Observable;
import com.application.labgui.Utils.Observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.converter.IntegerStringConverter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

public class UserController implements Observer<ServiceChangeEvent> {
    public Label numeCunoscut;
    public ListView<Utilizator> listaPrieteni;
    public ListView<Utilizator> listaUtilizatoriCereri;
    public ButtonBar baraButoane;
    public Button refuzaCererea;
    public Button acceptaCererea;
    public Button trimiteCerere;
    public ButtonBar baraPrietenButoane;
    public Label numePrieten;
    public Button stergePrieten;
    public Button removeReply;
    public Label labelReply;
    public GridPane gridSendMessage;
    //    public ListView<CererePrietenie> listaCereriTrimise;
//    public ListView<CererePrietenie> listaCereriPrimite;
    private Service service;
    private Utilizator utilizatorLogat;
    public TextField idPrietenNou;
    public TextField mesajNou;
    public Button sendButton;
    public ScrollPane scrollPane;

    private Mesaj mesajLaCareSeDaReply;

    ObservableList<Utilizator> modelPrieteni = FXCollections.observableArrayList();
    ObservableList<Utilizator> modelUtilizatoriCereri = FXCollections.observableArrayList();
    public void initUserController(Service service, Utilizator utilizator){
        this.mesajLaCareSeDaReply = null;
        this.labelReply.setVisible(false);
        this.removeReply.setVisible(false);
        this.service = service;
        utilizatorLogat = utilizator;
        this.loadListe();
        this.service.addObserver(this);
        this.loadVerificariText();
        this.baraButoane.setVisible(false);
        this.baraPrietenButoane.setVisible(false);
        listaPrieteni.getSelectionModel().selectedItemProperty().addListener((observable -> {
            listaUtilizatoriCereri.getSelectionModel().clearSelection();
            this.baraButoane.setVisible(false);
            this.baraPrietenButoane.setVisible(true);
            this.gridSendMessage.setVisible(false);
            var utilizatorCunoscut = listaPrieteni.getSelectionModel().getSelectedItem();
            if(utilizatorCunoscut!=null){
                loadMesaje(utilizatorCunoscut);
            }
        }));
        listaUtilizatoriCereri.getSelectionModel().selectedItemProperty().addListener((observable -> {
            listaPrieteni.getSelectionModel().clearSelection();
            this.baraButoane.setVisible(true);
            this.gridSendMessage.setVisible(false);
            this.baraPrietenButoane.setVisible(false);
            var utilizatorCunoscut = listaUtilizatoriCereri.getSelectionModel().getSelectedItem();
            if(utilizatorCunoscut!=null){
                loadButoaneCereri(utilizatorCunoscut);
            }

        }));
    }

    private void loadVerificariText() {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (Pattern.matches("[0-9]*", newText)) {
                return change;
            }
            return null;
        };
        TextFormatter<Integer> textFormatter = new TextFormatter<>(new IntegerStringConverter(), 0, filter);
        idPrietenNou.setTextFormatter(textFormatter);
//        idUtilizator2.setText("1");
    }

    private void loadButoaneCereri(Utilizator cunoscut){
        numeCunoscut.setText(cunoscut.getFirstName() + " " + cunoscut.getLastName());
        this.acceptaCererea.setDisable(false);
        this.refuzaCererea.setDisable(false);
        var relatie = service.getRelatieBetween(utilizatorLogat.getId() , cunoscut.getId()).get();
        if(relatie.getId().getLeft().equals(utilizatorLogat.getId())){
            //astepti momentan
            this.baraButoane.setVisible(false);
            if(relatie.getStatus() == CererePrietenie.REFUSED){
                this.numeCunoscut.setText("Cererea ta a fost refuzata");
            }
            else{
                this.numeCunoscut.setText("Cererea ta e in asteptare");
            }
        }
        else{
            if(relatie.getStatus() == CererePrietenie.REFUSED){
                //i-ai refuzat cererea
//                this.acceptaCererea.setDisable(true);
                this.refuzaCererea.setDisable(true);
                this.numeCunoscut.setText("I-ai refuzat cererea lui " + cunoscut.getFirstName() + " " + cunoscut.getLastName());
            }
            else{
//                this.acceptaCererea.setText("Accepta cererea");
//                this.refuzaCererea.setText("Refuza cererea");
            }
        }
//        this.baraButoane.setVisible(false);
        var lblNou = new Label("Nu poti trimite mesaj cat timpu nu esti prieten cu el!");
        this.scrollPane.setContent(lblNou);
    }


    private void loadListe(){
        listaPrieteni.setItems(modelPrieteni);
        listaUtilizatoriCereri.setItems(modelUtilizatoriCereri);
        this.reloadListe();
    }

    private void reloadListe(){
        var toateListele = this.service.cereriDePrietenie(utilizatorLogat.getId());
        if(toateListele.isEmpty()){
            return;
        }
        modelPrieteni.setAll(toateListele.get(1));
        modelUtilizatoriCereri.setAll(toateListele.get(2));
//        var listaPrietenii = toataLista.get("prieteni");
//        var listaCereriTrimisePending = toataLista.get("trimisePending");
//        var listaCereriTrimiseRespinse = toataLista.get("trimiseRespinse");
//        var listaCereriPrimitePending = toataLista.get("primitePending");
//        var listaCereriPrimiteRespinse = toataLista.get("primiteRespinse");
//        List<CererePrietenie> utilizatorList = StreamSupport.stream(listaPrietenii.spliterator(), false).toList();
//        modelPrieteni.setAll(utilizatorList);
//        List<CererePrietenie> utilizatorList1 = StreamSupport.stream(listaCereriTrimisePending.spliterator(), false).toList();
//        modelTrimise.setAll(utilizatorList1);
//        List<CererePrietenie> utilizatorList2 = StreamSupport.stream(listaCereriPrimitePending.spliterator(), false).toList();
//        modelPrimite.setAll(utilizatorList2);

    }

    private void loadMesaje(Utilizator cunoscut){
        this.gridSendMessage.setVisible(true);
        this.numePrieten.setText(cunoscut.getFirstName() + " " + cunoscut.getLastName());
        var listaToateMesajele = this.service.getAllMessagesBetween(utilizatorLogat.getId(), cunoscut.getId());
        VBox vBox = new VBox();
        this.scrollPane.setContent(vBox);
        vBox.setPrefWidth(scrollPane.getPrefWidth());
        listaToateMesajele.forEach(mesaj->{
            try{
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("views/single_mesaj_view.fxml"));
                AnchorPane root = fxmlLoader.load();
                vBox.getChildren().add(root);
                MesajViewController controllerMesaj = fxmlLoader.getController();
                controllerMesaj.initMesaj(this,mesaj, utilizatorLogat);
                root.setPrefWidth(vBox.getPrefWidth());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        scrollPane.setVvalue(1.0);
    }

    public void sendMesssage(ActionEvent actionEvent) {
        var cunoscut = listaPrieteni.getSelectionModel().getSelectedItem();
        var text_scris = this.mesajNou.getText();
        try{
            if(mesajLaCareSeDaReply == null) {
                this.service.sentNewMessage(utilizatorLogat.getId(), Collections.singletonList(cunoscut.getId()), text_scris, LocalDateTime.now());
            }
            else{
                this.service.sentNewMessage(utilizatorLogat.getId(), cunoscut.getId(), mesajLaCareSeDaReply.getId(), text_scris, LocalDateTime.now());
            }
            this.mesajNou.clear();
            this.mesajLaCareSeDaReply = null;
            this.labelReply.setVisible(false);
            this.removeReply.setVisible(false);
        }
        catch (AppException e){
            MessageAlert.showMessage(null, Alert.AlertType.ERROR, "", e.getMessage());
        }
    }

    @Override
    public void update(ServiceChangeEvent eventUpdate) {
        Long idCelalaltUser;
        if(Objects.equals(eventUpdate.getUser1(), utilizatorLogat.getId())){
            idCelalaltUser = eventUpdate.getUser2();
        }
        else if(Objects.equals(eventUpdate.getUser2(), utilizatorLogat.getId())){
            idCelalaltUser = eventUpdate.getUser1();
        }
        else {
            return;
        }
        var user = service.findOne(idCelalaltUser).get();
        this.baraButoane.setVisible(false);
        this.baraPrietenButoane.setVisible(false);
        if (eventUpdate.getType().equals(ChangeEventType.MESSAGES)) {
            var cunoscut = listaPrieteni.getSelectionModel().getSelectedItem();
            listaPrieteni.getSelectionModel().select(user);
            this.loadMesaje(user);
        } else if (eventUpdate.getType().equals(ChangeEventType.FRIENDS)) {
            this.reloadListe();
        }
    }

    public void handlerRefuzaCererea(ActionEvent actionEvent) {
        var cunoscut = listaUtilizatoriCereri.getSelectionModel().getSelectedItem();
//        System.out.println("refuza");
        this.service.refuseCererePrietenie(cunoscut.getId(), utilizatorLogat.getId());
    }

    public void handlerAcceptaCererea(ActionEvent actionEvent) {
        var cunoscut = listaUtilizatoriCereri.getSelectionModel().getSelectedItem();
//        System.out.println("Accepta");
        this.service.acceptCererePrietenie(cunoscut.getId(), utilizatorLogat.getId());
    }

    public void handlerTrimiteCerere(ActionEvent actionEvent) {
        var idViitorPrieten = Long.parseLong(this.idPrietenNou.getText());
        try{
            service.trimiteCererePrietenie(utilizatorLogat.getId(), idViitorPrieten);
            this.idPrietenNou.clear();
        }
        catch (AppException e){
            MessageAlert.showMessage(null, Alert.AlertType.ERROR, "", e.getMessage());
        }
    }

    public void setReply(Mesaj mesaj){
        this.labelReply.setVisible(true);
        this.removeReply.setVisible(true);
        this.labelReply.setText(mesaj.getMesajScris().substring(0, Math.min(10, mesaj.getMesajScris().length()-1)) + "...");
        mesajLaCareSeDaReply = mesaj;
    }

    public void removeReply(ActionEvent actionEvent) {
        this.labelReply.setVisible(false);
        this.removeReply.setVisible(false);
        mesajLaCareSeDaReply = null;
    }
}
