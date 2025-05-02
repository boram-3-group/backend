package com.boram.look.global.ex;

public class DuplicateEmailUseException extends RuntimeException {
    public DuplicateEmailUseException(String message) {
        super(message);
    }
}
