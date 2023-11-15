package com.application.labgui.Validators;

import com.application.labgui.AppExceptions.ValidationException;

public interface Validator<T> {
    void validate(T entity) throws ValidationException;
}
