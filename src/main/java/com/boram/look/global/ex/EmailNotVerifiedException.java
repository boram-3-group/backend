package com.boram.look.global.ex;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class EmailNotVerifiedException extends RuntimeException {
    private final String email;

    public EmailNotVerifiedException(String email) {
        this.email = email;
    }

}
