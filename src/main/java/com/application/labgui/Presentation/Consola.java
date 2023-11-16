package com.application.labgui.Presentation;


import com.application.labgui.AppExceptions.AppException;
import com.application.labgui.Domain.Tuplu;
import com.application.labgui.Service.Service;

import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Consola {
    private Service serviceApp;

    public Consola(Service serviceApp) {
        this.serviceApp = serviceApp;
    }
    public void run(){
        System.out.println("Bine ai venit la aplicatia!");
        while (true){
            System.out.println("Alege una dintre optiunile de mai jos:");
            System.out.println("1. Afiseaza toti utilizatorii");
            System.out.println("2. Adauga un user nou");
            System.out.println("3. Sterge un user");
            System.out.println("4. Afiseaza toate relatiile de prietenie");
            System.out.println("5. Adauga o relatie de prietenie");
            System.out.println("6. Sterge o relatie de prietenie");
            System.out.println("7. Afla numarul de comunitati");
            System.out.println("8. Afla cea mai sociabila comunitate");
            System.out.println("9. Relatii de prietenie create intr-o luna a anului");
            System.out.println("0. Inchide aplicatia");
            System.out.print(">>>");
            Scanner scanner = new Scanner(System.in);
            var optiune = scanner.next().charAt(0);
            HelperConsola helperConsola = null;
            switch (optiune){
                case '1'->{
                    helperConsola = this::afisareUtilizatori;
                }
                case '2'->{
                    helperConsola = this::adaugaUtilizator;
                }
                case '3'->{
                    helperConsola = this::stergeUtilizator;
                }
                case '4'->{
                    helperConsola = this::afisarePrietenii;
                }
                case '5'->{
                    helperConsola = this::adaugaPrietenie;
                }
                case '6'->{
                    helperConsola = this::stergePrietenie;
                }
                case '7'->{
                    helperConsola = this::numarulDeComunitati;
                }
                case '8'->{
                    helperConsola = this::ceaMaiSociabilaComunitate;
                }
                case '9'->{
                    helperConsola = this::prieteniiDinLuna;
                }
                case '0', 'q', 'Q' ->{
                    System.out.println("La revedere!");
                    return;
                }
                case 'a', 'A'-> {
                    helperConsola = this::autopopulare;
                }
                default -> {
                    helperConsola = this::invalidOption;
                }
            }
            helperConsola.userInteraction();
        }
    }

    private void prieteniiDinLuna() {
        System.out.println("Introdu un utilizator:");
        System.out.print(">>>");
        Scanner scanner = new Scanner(System.in);
        var idUserString = scanner.nextLine();
        System.out.println("Introdu o luna:");
        System.out.print(">>>");
        var lunaString = scanner.nextLine();
        long idUser = 0;
        try{
            idUser = Long.parseLong(idUserString);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return;
        }
        int luna = 0;
        try{
            luna = Integer.parseInt(lunaString);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return;
        }
        var result = serviceApp.relatiiDePrietenieLuna(idUser, luna);
        Month month = Month.of(luna);
        if(result.isEmpty()){
            System.out.println("Nu exista nicio relatie de prietenie din luna " + month.toString().toLowerCase());
            return;
        }
        System.out.println("Relatiile de prietenie:");
        result.forEach(System.out::println);
    }

    private void autopopulare() {
        serviceApp.addNewUser("Marinache", "Valentin");
        serviceApp.addNewUser("Albu", "Sorin");
        serviceApp.addNewUser("Valeriu ", "Sergiu");
        serviceApp.addNewUser("Pop", "Madalina");
        serviceApp.addNewUser("Slăboiu", "Bianca");
        serviceApp.addNewUser("Tomulescu", "Petruța");
        System.out.println("A fost autopopulat repository-ul cu niste utilizatori");
    }

    private void ceaMaiSociabilaComunitate() {
        var nrComunitati = serviceApp.numarComunitati();
        if(nrComunitati == 0){
            System.out.println("Inca nu exista comunitati!");
            return;
        }
        System.out.println("Cea mai sociabila comunitate este formata din:");
        serviceApp.ceaMaiSociabilaComunitate().forEach(System.out::println);
    }

    private void numarulDeComunitati() {
        var nrComunitati = serviceApp.numarComunitati();
        if(nrComunitati == 0){
            System.out.println("Inca nu exista comunitati!");
            return;
        }
        System.out.println("Numarul de comunitati este:" + nrComunitati);
    }

    private void adaugaUtilizator(){
        System.out.println("Introdu un nume:");
        System.out.print(">>>");
        Scanner scanner = new Scanner(System.in);
        var nume = scanner.nextLine();
        System.out.println("Introdu un prenume:");
        System.out.print(">>>");
        var prenume = scanner.nextLine();
        try{
            serviceApp.addNewUser(nume, prenume);
            System.out.println("Utilizator creat cu succes!");
        }
        catch (AppException e){
            System.out.println(e.getMessage());
        }
    }

    private void afisareUtilizatori(){
        serviceApp.getAllUtilizatori().forEach(System.out::println);
    }

    private void stergeUtilizator(){
        System.out.println("Introdu ID-ul utilizatorului pe care sa-l stergi");
        System.out.print(">>>");
        Scanner scanner = new Scanner(System.in);
        var buffer = scanner.nextLine();
        Long idUser;
        try{
            idUser = Long.parseLong(buffer);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return;
        }
        try{
            serviceApp.deleteUtilizator(idUser);
            System.out.println("Utilizator sters cu succes!");
        }
        catch (AppException e){
            System.out.println(e.getMessage());
        }
    }


    private void stergePrietenie() {
        System.out.println("Introdu ID-ul utilizatorilor a caror prietenie vrei s-o inchei");
        System.out.print(">>>");
        Scanner scanner = new Scanner(System.in);
        var buffer = scanner.nextLine();
        Long idUser1;
        Long idUser2;
        try{
            idUser1 = Long.parseLong(buffer);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return;
        }
        System.out.print(">>>");
        buffer = scanner.nextLine();
        try{
            idUser2 = Long.parseLong(buffer);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return;
        }
        try{
            serviceApp.deletePrietenie(new Tuplu<>(idUser1, idUser2));
            System.out.println("Utilizator sters cu succes!");
        }
        catch (AppException e){
            System.out.println(e.getMessage());
        }
    }

    private void adaugaPrietenie() {
        System.out.println("Introdu ID-ul utilizatorilor pe care vrei sa-i faci prieteni");
        System.out.print(">>>");
        Scanner scanner = new Scanner(System.in);
        var buffer = scanner.nextLine();
        Long idUser1;
        Long idUser2;
        try{
            idUser1 = Long.parseLong(buffer);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return;
        }
        System.out.print(">>>");
        buffer = scanner.nextLine();
        try{
            idUser2 = Long.parseLong(buffer);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return;
        }
        try{
            serviceApp.addPrietenie(idUser1, idUser2);
            System.out.println("Prietenie creata cu succes!");
        }
        catch (AppException e){
            System.out.println(e.getMessage());
        }
    }

    private void afisarePrietenii() {
        serviceApp.getAllPrietenii().forEach(prietenie->{
            var u1 = serviceApp.findOne(prietenie.getId().getLeft()).get();
            var u2 = serviceApp.findOne(prietenie.getId().getRight()).get();
            System.out.println(u1 + " <-> " + u2 + "\tFriendsFrom\t" + prietenie.getDateCreated().format(DateTimeFormatter.ISO_DATE));
        });
    }

    private void invalidOption(){
        System.out.println("Nu ai introdus o optiune valida!");
    }
}
