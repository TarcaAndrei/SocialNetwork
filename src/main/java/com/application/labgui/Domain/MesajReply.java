package com.application.labgui.Domain;

import java.time.LocalDateTime;
import java.util.List;

public class MesajReply extends Mesaj{
    private Mesaj mesajLaCareSeDaReply;


    public MesajReply(Long idMesaj, Utilizator from, List<Utilizator> utilizators, String mesajScris, LocalDateTime data, Mesaj mesajLaCareSeDaReply) {
        super(idMesaj, from, utilizators, mesajScris, data);
        this.mesajLaCareSeDaReply = mesajLaCareSeDaReply;
    }

    public MesajReply(Long idMesaj, Utilizator from, List<Utilizator> utilizators, String mesajScris, Mesaj mesajLaCareSeDaReply) {
        super(idMesaj, from, utilizators, mesajScris);
        this.mesajLaCareSeDaReply = mesajLaCareSeDaReply;
    }

    public Mesaj getMesajLaCareSeDaReply() {
        return mesajLaCareSeDaReply;
    }

    public void setMesajLaCareSeDaReply(Mesaj mesajLaCareSeDaReply) {
        this.mesajLaCareSeDaReply = mesajLaCareSeDaReply;
    }
}
