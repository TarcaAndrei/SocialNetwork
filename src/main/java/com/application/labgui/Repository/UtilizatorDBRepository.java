package com.application.labgui.Repository;

import com.application.labgui.AppExceptions.RepositoryException;
import com.application.labgui.Domain.Utilizator;
import com.application.labgui.Repository.Paging.Page;
import com.application.labgui.Repository.Paging.Pageable;
import com.application.labgui.Repository.Paging.PagingRepository;
import com.application.labgui.Validators.FactoryValidator;
import com.application.labgui.Validators.Validator;
import com.application.labgui.Validators.ValidatorStrategies;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;


public class UtilizatorDBRepository implements PagingRepository<Long, Utilizator> {
    private Validator utilizatorValidator;
    private DBConnection dbConnection;

    public UtilizatorDBRepository(DBConnection dbConnection, ValidatorStrategies validatorStrategies) {
        utilizatorValidator = FactoryValidator.getFactoryInstance().createValidator(validatorStrategies);
        this.dbConnection = dbConnection;
    }

    public Optional<Utilizator>findUserAuth(String username){
        try(Connection connection = DriverManager.getConnection(dbConnection.DB_URL, dbConnection.DB_USER, dbConnection.DB_PASSWD);
            PreparedStatement statement = connection.prepareStatement("select * from Utilizatori " +
                    "where username = ?");

        ) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                String firstName = resultSet.getString("firstname");
                String lastName = resultSet.getString("lastname");
                String password = resultSet.getString("password");
                Long id = resultSet.getLong("iduser");
                Utilizator u = new Utilizator(firstName,lastName, username, password);
                u.setId(id);
                u = loadFriends(u).get();
                return Optional.ofNullable(u);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Utilizator> findOne(Long longID) {
        try(Connection connection = DriverManager.getConnection(dbConnection.DB_URL, dbConnection.DB_USER, dbConnection.DB_PASSWD);
            PreparedStatement statement = connection.prepareStatement("select iduser, firstname, lastname, username from Utilizatori " +
                    "where idUser = ?");

        ) {
            statement.setLong(1, longID);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                String firstName = resultSet.getString("firstname");
                String lastName = resultSet.getString("lastname");
                String username = resultSet.getString("username");
                Utilizator u = new Utilizator(firstName,lastName, username);
                u.setId(longID);
                u = loadFriends(u).get();
                return Optional.ofNullable(u);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Utilizator> findAll() {
        HashMap<Long, Utilizator> entities = new HashMap<>();
        try (Connection connection = DriverManager.getConnection(dbConnection.DB_URL, dbConnection.DB_USER, dbConnection.DB_PASSWD)) {
            PreparedStatement statement = connection.prepareStatement("SELECT iduser, firstname, lastname, username FROM Utilizatori");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong("iduser");
                String firstName = resultSet.getString("firstname");
                String lastName = resultSet.getString("lastname");
                String username = resultSet.getString("username");
                Utilizator utilizator = new Utilizator(firstName, username);
                utilizator.setId(id);
                utilizator = loadFriends(utilizator).get();
                entities.put(id, utilizator);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return entities.values();
    }

    @Override
    public Optional<Utilizator> save(Utilizator entity) {
        utilizatorValidator.validate(entity);
        try(Connection connection = DriverManager.getConnection(dbConnection.DB_URL, dbConnection.DB_USER, dbConnection.DB_PASSWD);
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Utilizatori(firstname, lastname, username, password) VALUES(?, ?, ?, ?);")){
            preparedStatement.setString(1, entity.getFirstName());
            preparedStatement.setString(2, entity.getLastName());
            preparedStatement.setString(3, entity.getUserName());
            preparedStatement.setString(4, entity.getPassword());
            var responseSQL =preparedStatement.executeUpdate();
            return responseSQL==0 ? Optional.of(entity) : Optional.empty();
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public Optional<Utilizator> delete(Long aLong) {
        try(Connection connection = DriverManager.getConnection(dbConnection.DB_URL, dbConnection.DB_USER, dbConnection.DB_PASSWD);
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM Utilizatori U WHERE U.iduser = ?")){
            preparedStatement.setLong(1, aLong);
            var entitatea = findOne(aLong);
            var raspuns = 0;
            if(entitatea.isPresent()){
                raspuns = preparedStatement.executeUpdate();
            }
            return raspuns==0 ? Optional.empty() : entitatea;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<Utilizator> loadFriends(Utilizator utilizator){
        String sqlStatement = "SELECT U.iduser, U.firstname, U.lastname FROM Utilizatori U, Prietenii P\n" +
                "WHERE (P.iduser1 = ? AND P.iduser2 = u.IDUser) OR (P.iduser2 = ? AND P.iduser1 = u.IDUser);";
        try(Connection connection = DriverManager.getConnection(dbConnection.DB_URL, dbConnection.DB_USER, dbConnection.DB_PASSWD);
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
        ) {
            preparedStatement.setLong(1, utilizator.getId());
            preparedStatement.setLong(2, utilizator.getId());
            var resultSet =preparedStatement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong("iduser");
                String firstName = resultSet.getString("firstname");
                String lastName = resultSet.getString("lastname");
                Utilizator utilizatorFriend = new Utilizator(firstName, lastName);
                utilizatorFriend.setId(id);
                utilizator.addFriend(utilizatorFriend);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.of(utilizator);
    }

    @Override
    public Optional<Utilizator> update(Utilizator entity) {
        utilizatorValidator.validate(entity);
        try(Connection connection = DriverManager.getConnection(dbConnection.DB_URL, dbConnection.DB_USER, dbConnection.DB_PASSWD);
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Utilizatori SET firstname = ?, lastname=?, username=?, password=? WHERE idUser = ?")
        ){
            preparedStatement.setString(1, entity.getFirstName());
            preparedStatement.setString(2, entity.getLastName());
            preparedStatement.setString(3, entity.getUserName());
            preparedStatement.setString(4, entity.getPassword());
            preparedStatement.setLong(5 ,entity.getId());
            var response = preparedStatement.executeUpdate();
            return response==0 ? Optional.of(entity) : Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int size() {
        try(Connection connection = DriverManager.getConnection(dbConnection.DB_URL, dbConnection.DB_USER, dbConnection.DB_PASSWD);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM utilizatori")
        ) {
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteALL_ALL(String estiSigur){
        if(! estiSigur.equals("DA, STERGE-LE")){
            return;
        }
        try(Connection connection = DriverManager.getConnection(dbConnection.DB_URL, dbConnection.DB_USER, dbConnection.DB_PASSWD);
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM utilizatori")
        ){
            var response = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Page<Utilizator> findAll(Pageable pageable) {
        int numberOfElements = size();
        int limit = pageable.getPageSize();
        int offset = pageable.getPageSize() * pageable.getPageNumber();
        if(offset > numberOfElements){
            return new Page<>(new ArrayList<>(), numberOfElements);
        }
        List<Utilizator> utilizatorList = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(dbConnection.DB_URL, dbConnection.DB_USER, dbConnection.DB_PASSWD);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT iduser, firstname, lastname, username FROM utilizatori LIMIT ? OFFSET ?")
        ) {
            preparedStatement.setInt(1, limit);
            preparedStatement.setInt(2, offset);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong("iduser");
                String firstName = resultSet.getString("firstname");
                String lastName = resultSet.getString("lastname");
                String username = resultSet.getString("username");
                Utilizator utilizator = new Utilizator(firstName, lastName, username);
                utilizator.setId(id);
                utilizator = loadFriends(utilizator).get();
                utilizatorList.add(utilizator);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new Page<>(utilizatorList, numberOfElements);
    }
}
