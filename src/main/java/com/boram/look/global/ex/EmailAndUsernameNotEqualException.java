package com.boram.look.global.ex;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EmailAndUsernameNotEqualException extends RuntimeException {
    public String message;
}
