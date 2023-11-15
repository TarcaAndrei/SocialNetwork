package com.application.labgui.Domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PrietenieDTO {
    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }

    public LocalDateTime getFriendsFrom() {
        return friendsFrom;
    }

    public void setFriendsFrom(LocalDateTime friendsFrom) {
        this.friendsFrom = friendsFrom;
    }

    private String nume;
    private String prenume;
    private LocalDateTime friendsFrom;

    public PrietenieDTO(String nume, String prenume, LocalDateTime friendsFrom) {
        this.nume = nume;
        this.prenume = prenume;
        this.friendsFrom = friendsFrom;
    }

    @Override
    public String toString() {
        return nume + " " + " " + prenume + " " + friendsFrom.format(DateTimeFormatter.ISO_DATE);
    }
}
