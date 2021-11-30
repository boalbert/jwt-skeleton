package com.javainuse.model;

import java.io.Serial;
import java.io.Serializable;

public record UserRequest(String username, String password) implements Serializable {
    @Serial
    private static final long serialVersionUID = 5926468583005150707L;

}