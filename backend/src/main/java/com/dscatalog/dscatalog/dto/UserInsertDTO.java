package com.dscatalog.dscatalog.dto;

public class UserInsertDTO extends UserDTO{
    private static final long serialVersionUID = 7123532062112517212L;

    private String password;

    public UserInsertDTO() {
        super();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
