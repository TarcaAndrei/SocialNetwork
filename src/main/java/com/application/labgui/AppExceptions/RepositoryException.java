package com.application.labgui.AppExceptions;

public class RepositoryException extends AppException{
    public RepositoryException(){
        super();
    }
    public RepositoryException(String message){
        super(message);
    }
    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public RepositoryException(Throwable cause) {
        super(cause);
    }

    public RepositoryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
