package com.application.labgui.Service;

import com.application.labgui.AppExceptions.ServiceException;
import com.application.labgui.AppExceptions.ValidationException;
import com.application.labgui.Domain.Prietenie;
import com.application.labgui.Domain.PrietenieDTO;
import com.application.labgui.Domain.Tuplu;
import com.application.labgui.Domain.Utilizator;
import com.application.labgui.Repository.Repository;
import com.application.labgui.Utils.DFS;
import com.application.labgui.Utils.Events.ChangeEventType;
import com.application.labgui.Utils.Events.ServiceChangeEvent;
import com.application.labgui.Utils.Observer.Observable;
import com.application.labgui.Utils.Observer.Observer;
import com.application.labgui.Validators.FactoryValidator;
import com.application.labgui.Validators.Validator;
import com.application.labgui.Validators.ValidatorStrategies;

import java.time.Month;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Service implements Observable<ServiceChangeEvent> {
    private Repository<Long, Utilizator> repositoryUtilizatori;
    private Repository<Tuplu<Long, Long>, Prietenie> repositoryPrietenii;
    private Validator validatorUtilizator;
    private Validator validatorPrietenie;

//    private long idUtilizatorNou;
//
//    /**
//     * functia care genereaza id-uri unice incepand cu 1
//     */
//    private void loadIdGenerator(){
//        idUtilizatorNou = 0;
//        repositoryUtilizatori.findAll().forEach( x->{
//            idUtilizatorNou = Math.max(idUtilizatorNou, x.getId());
//        });
//    }

    /**
     *
     * @return un id unic
     */
//    private long getIdUtilizatorNou(){
//        idUtilizatorNou++;
//        return idUtilizatorNou;
//    }

    /**
     * constructor service
     * @param repositoryUtilizatori
     * @param repositoryPrietenii
     * @param strategies strategie de validare pentru utilizator
     * @param strategies1 strategie de validare pentru prietenie
     */
    public Service(Repository repositoryUtilizatori, Repository repositoryPrietenii, ValidatorStrategies strategies, ValidatorStrategies strategies1) {
        this.repositoryUtilizatori = repositoryUtilizatori;
        this.repositoryPrietenii = repositoryPrietenii;
        var factory = FactoryValidator.getFactoryInstance();
        this.validatorUtilizator = factory.createValidator(strategies);
        this.validatorPrietenie = factory.createValidator(strategies1);
//        loadIdGenerator();
    }

    /**
     * functie de adaugare a unui nou user
     * @param numeUtilizator numele noului utilizator, string nevid
     * @param prenumeUtilizator prenumele noului utilizator, string nevid
     * @throws ServiceException daca exista deja utilizatorul in repo
     * @throws ValidationException daca stringurile sunt vide
     */
    public void addNewUser(String numeUtilizator, String prenumeUtilizator){
        var utilizatorNou = new Utilizator(prenumeUtilizator, numeUtilizator);
//        utilizatorNou.setId(getIdUtilizatorNou());
        validatorUtilizator.validate(utilizatorNou);
        var response = repositoryUtilizatori.save(utilizatorNou);
        if(response.isPresent()){
            throw new ServiceException("Utilizator existent!");
        }
//        Predicate<Optional<Utilizator>> predicate = Optional::isPresent;
//        pre
    }

    /**
     * getter pentru toti utilzatorii
     * @return un iterabil
     */
    public Iterable<Utilizator> getAllUtilizatori(){
        Stream<Utilizator> myStream = StreamSupport.stream(repositoryUtilizatori.findAll().spliterator(), false);
        return myStream
                .sorted((x, y)->x.compareTo(y))
                .collect(Collectors.toList());
    }

    /**
     * functie de stergere a unui utilizator si a prieteniilor acestuia
     * @param idUtilizator idul utilizatorului care urmeaza a fi sters
     * @throws ServiceException daca utilizatorul nu exista
     */
    public void deleteUtilizator(Long idUtilizator){
        var response = this.repositoryUtilizatori.delete(idUtilizator);
        if(response.isEmpty()){
            throw new ServiceException("Utilizator inexistent!");
        }
        var u1 = response.get();
        if(u1.getFriends().isEmpty()){
            return;
        }
        u1.getFriends().forEach(x->{
            repositoryPrietenii.delete(new Tuplu<>(u1.getId(), x.getId()));
            x.deleteFriend(u1);
            repositoryUtilizatori.update(x);
        });
        this.notifyAllObservers(new ServiceChangeEvent());
    }

    /**
     * functie de adaugare a unei relatii de prietenie intre 2 utilziatori
     * @param idU1 id-ul primului user
     * @param idU2 id-ul celui de-al dooilea user
     * @throws ValidationException daca id-urile nu sunt valide
     * @throws ServiceException daca nu exista userii sau daca relatia de prietenie exista deja
     */
    public void addPrietenie(Long idU1, Long idU2){
        Predicate<Long> existaUser = idUser->repositoryUtilizatori.findOne(idUser).isPresent();
        Consumer<Utilizator> updateUser = repositoryUtilizatori::update;
        Prietenie prietenie = new Prietenie(idU1, idU2);
        this.validatorPrietenie.validate(prietenie);
        if(!(existaUser.test(idU1) && existaUser.test(idU2))){
            throw new ServiceException("Nu exista userii!");
        }
        Utilizator u1 = repositoryUtilizatori.findOne(idU1).get();
        Utilizator u2 = repositoryUtilizatori.findOne(idU2).get();
        var response = repositoryPrietenii.save(prietenie);
        if(response.isPresent()){
            throw new ServiceException("Relatia de prietenie exista deja!");
        }
        u1.addFriend(u2);
        u2.addFriend(u1);
        updateUser.accept(u1);
        updateUser.accept(u2);
    }

    /**
     * getter pentru toate relatiile de prietenie
     * @return toate relatiile de prietenie
     */
    public Iterable<Prietenie> getAllPrietenii(){
        return repositoryPrietenii.findAll();
    }

    /**
     * functie de gasire a unui utilizator dupa id
     * @param idUtilizator id-ul userului
     * @return un Optional cu userul...poate fi empty daca nu a fost gasit userul
     */
    public Optional<Utilizator> findOne(Long idUtilizator){
        return repositoryUtilizatori.findOne(idUtilizator);
    }

    /**
     * functie de gasire a unei prietenii dupa id
     * @param idPrietenie id-ul prieteniei ca tuplu
     * @return un Optional cu prietenia...poate fi empty daca nu a fost gasit prietenia
     */
    public Optional<Prietenie> findOne(Tuplu<Long, Long> idPrietenie){
        return repositoryPrietenii.findOne(idPrietenie);
    }

    /**0
     * functie de stergere a unei relatii de prietenie
     * @param idPrietenie id-ul relatiei
     * @throws  ServiceException daca nu exista prietenia
     */
    public void deletePrietenie(Tuplu<Long, Long> idPrietenie){
        var response = repositoryPrietenii.delete(idPrietenie);
        if(response.isEmpty()) {
            throw new ServiceException("Nu exista relatia de prietenie!");
        }
        var u1 = repositoryUtilizatori.findOne(idPrietenie.getLeft()).get();
        var u2 = repositoryUtilizatori.findOne(idPrietenie.getRight()).get();
        u1.deleteFriend(u2);
        u2.deleteFriend(u1);
        repositoryUtilizatori.update(u1);
        repositoryUtilizatori.update(u2);
    }

    /**
     * numarul de utilziatori din repo
     * @return nr de utilizatori
     */
    public int sizeRepositoryUtilizatori(){
        return repositoryUtilizatori.size();
    }

    /**
     * numarul de relatii de prieteneie din repo
     * @return nr de prietenii
     */
    public int sizeRepositoryPrietenii(){
        return repositoryPrietenii.size();
    }

    /**
     * return numarul de comunitati  - de componente conexe
     * @return numarul de comunitati
     */
    public int numarComunitati(){
        var rezultatDFS = DFS();
        return rezultatDFS.size();
    }

    /**
     * returneaza cea mai sociabila comunitate
     * @return lista cu cei mai sociabili useri
     */
    public Iterable<Utilizator> ceaMaiSociabilaComunitate(){
        var lista = cautaComunitatea();
        var listaFinala = new ArrayList<Utilizator>();
        lista.forEach(x->{
            var user = findOne(x).get();
            listaFinala.add(user);
        });
        return listaFinala;
    }

    /**
     * cauta o comunitate pe baza rezultatului DFS
     * @return
     */
    private ArrayList<Long> cautaComunitatea(){
        var rezultatDFS = DFS();
        var comunitate = new ArrayList<Long>();
        int lungimeMaxima = 0;
        for(var componentaConexa : rezultatDFS){
            int nrLegaturi = 0;
            for(var nod : componentaConexa){
                var u1 = repositoryUtilizatori.findOne(nod).get();
                nrLegaturi += u1.getFriends().size();
            }
            nrLegaturi/=2;
            if(nrLegaturi > lungimeMaxima){
                lungimeMaxima = nrLegaturi;
                comunitate = componentaConexa;
            }
        }
        return comunitate;

    }

    /**
     * algoritmul de dfs
     * @return
     */
    private ArrayList<ArrayList<Long>> DFS(){
        var listaAdiacenta = new HashMap<Long, Vector<Long>>();
        repositoryUtilizatori.findAll().forEach(x->{
            listaAdiacenta.put(x.getId(), new Vector<>());
            x.getFriends().forEach(y->{
                listaAdiacenta.get(x.getId()).add(y.getId());
            });
        });
        var dfs = new DFS(listaAdiacenta);
        return dfs.mainAlgorithm();
    }

    public List<Utilizator> relatiiDePrietenie(Long idUser){
        List<Utilizator> listaPrieteni = new ArrayList<>();
        var optionalUtilizator = findOne(idUser);
        if(optionalUtilizator.isEmpty()){
            return listaPrieteni;
        }
        return optionalUtilizator.get().getFriends();
    }

    public List<PrietenieDTO> relatiiDePrietenieLuna(Long idUser, Integer luna){
        Predicate<PrietenieDTO> prietenieDinLuna = prietenieDTO -> prietenieDTO.getFriendsFrom().getMonth().equals(Month.of(luna));
        List<PrietenieDTO> listaPrieteniDTO = new ArrayList<>();
        var listaPrieteni = relatiiDePrietenie(idUser);
        listaPrieteni.forEach(x->{
            var relatie = repositoryPrietenii.findOne(new Tuplu<>(idUser, x.getId())).get();
            listaPrieteniDTO.add(new PrietenieDTO(x.getLastName(), x.getFirstName(), relatie.getDateCreated()));
        });
        return listaPrieteniDTO.stream()
                .filter(prietenieDinLuna)
                .collect(Collectors.toList());

    }

}
