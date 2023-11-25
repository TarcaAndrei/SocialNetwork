package com.application.labgui.Utils.Events;

import com.application.labgui.Domain.Mesaj;
import com.application.labgui.Domain.Utilizator;

public class ReplyEvent implements Event{
    private ChangeEventType type=ChangeEventType.REPLY;
    private Mesaj mesaj;
    private Utilizator replyer;

    public ReplyEvent(Mesaj mesaj, Utilizator replyer){
        this.mesaj = mesaj;
        this.replyer = replyer;
    }


    public ChangeEventType getType() {
        return type;
    }

    public Mesaj getMesaj() {
        return mesaj;
    }

    public void setMesaj(Mesaj mesaj) {
        this.mesaj = mesaj;
    }
}
