package com.application.labgui.Validators;

import com.application.labgui.AppExceptions.ValidationException;
import com.application.labgui.Domain.Utilizator;

public class UtilizatorValidator implements Validator<Utilizator>{
    @Override
    public void validate(Utilizator entity) throws ValidationException {
        String mesajEroare = "";
        if(entity.getFirstName().isEmpty()){
            mesajEroare += "Prenume invalid!\n";
        }
        if(entity.getLastName().isEmpty()){
            mesajEroare += "Nume invalid!\n";
        }
        if(!mesajEroare.isEmpty()){
            throw new ValidationException(mesajEroare);
        }
    }
}
