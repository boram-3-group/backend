package com.boram.look.global.ex;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
public class EmailAndUsernameNotEqualException extends RuntimeException {
    public String message;
}
