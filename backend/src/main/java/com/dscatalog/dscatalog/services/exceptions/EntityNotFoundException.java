package com.dscatalog.dscatalog.services.exceptions;

public class EntityNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 4333061262017367368L;

    public EntityNotFoundException(String msg) {
        super(msg);
    }
}
