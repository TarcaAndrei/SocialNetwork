package com.application.labgui.Domain;

import java.time.LocalDateTime;
import java.util.List;

public class Mesaj {
    private Long idMesaj;
    private Utilizator from;
    private List<Utilizator> to;
    private String mesajScris;
    private LocalDateTime data;

    public Mesaj(Long idMesaj, Utilizator from, List<Utilizator> utilizators, String mesajScris, LocalDateTime data) {
        this.idMesaj = idMesaj;
        this.from = from;
        to = utilizators;
        this.mesajScris = mesajScris;
        this.data = data;
    }

    public Mesaj(Long idMesaj, Utilizator from, List<Utilizator> utilizators, String mesajScris) {
        this.idMesaj = idMesaj;
        this.from = from;
        to = utilizators;
        this.mesajScris = mesajScris;
        this.data = LocalDateTime.now();
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public Long getIdMesaj() {
        return idMesaj;
    }

    public void setIdMesaj(Long idMesaj) {
        this.idMesaj = idMesaj;
    }

    public Utilizator getFrom() {
        return from;
    }

    public void setFrom(Utilizator from) {
        this.from = from;
    }

    public List<Utilizator> getTo() {
        return to;
    }

    public void setTo(List<Utilizator> to) {
        this.to = to;
    }

    public String getMesajScris() {
        return mesajScris;
    }

    public void setMesajScris(String mesajScris) {
        this.mesajScris = mesajScris;
    }
}

