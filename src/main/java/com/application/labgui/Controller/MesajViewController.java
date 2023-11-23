package com.application.labgui.Controller;


import com.application.labgui.Domain.Mesaj;
import com.application.labgui.Domain.Utilizator;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.TextFlow;

import java.time.format.DateTimeFormatter;

public class MesajViewController  {

    public Label dataTextEu;
    public Label mesajReplyTextEu;
    public TextArea textAreaEu;
    public Label dataTextEl;
    public Label mesajReplyTextEl;
    public TextArea textAreaEl;
    private Utilizator sender;
    private Mesaj mesaj;

    public void initMesaj(Mesaj mesaj, Utilizator sender){
        this.mesaj = mesaj;
        this.sender = sender;
        configure();
    }

    private void configure(){
        if(mesaj.getFromUser().equals(sender.getId())){
            //inseamna ca suntem la cel care a trimis mesajul, deci il afisam pe partea lui
            this.textAreaEl.setVisible(false);
            this.textAreaEu.setVisible(true);
            this.textAreaEu.setText(this.mesaj.getMesajScris());
            this.dataTextEu.setText(mesaj.getData().format(DateTimeFormatter.ISO_DATE_TIME));
            if(mesaj.getReplyTo()==null){
                mesajReplyTextEu.setVisible(false);
            }
            else{
                mesajReplyTextEu.setVisible(true);
                var mesajulLaCareSeDa = mesaj.getReplyTo().getMesajScris().substring(0, 10);
                mesajulLaCareSeDa += "...";
                mesajReplyTextEu.setText(mesajulLaCareSeDa);
            }
        }
        else{
            this.textAreaEl.setEditable(false);
            this.textAreaEu.setVisible(false);
            this.textAreaEl.setVisible(true);
            this.textAreaEl.setText(this.mesaj.getMesajScris());
            this.dataTextEl.setText(mesaj.getData().format(DateTimeFormatter.ISO_DATE_TIME));
            if(mesaj.getReplyTo()==null){
                mesajReplyTextEl.setVisible(false);
            }
            else{
                mesajReplyTextEl.setVisible(true);
                var mesajulLaCareSeDa = mesaj.getReplyTo().getMesajScris().substring(0, 10);
                mesajulLaCareSeDa += "...";
                mesajReplyTextEl.setText(mesajulLaCareSeDa);
            }
        }
    }


}
