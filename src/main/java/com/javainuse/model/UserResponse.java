package com.javainuse.model;

import java.io.Serial;
import java.io.Serializable;


public record UserResponse(String jwttoken) implements Serializable {

    @Serial
    private static final long serialVersionUID = -8091879091924046844L;
}