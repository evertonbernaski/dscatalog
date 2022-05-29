package com.dscatalog.dscatalog.services.exceptions;

public class DataBaseException extends RuntimeException {
    private static final long serialVersionUID = 4333061262017367368L;

    public DataBaseException(String msg) {
        super(msg);
    }
}
