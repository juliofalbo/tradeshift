package com.tradeshift.juliofalbo.challenge.tradeshift.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class OnlyOneRootException extends RuntimeException {
    public OnlyOneRootException(String msg) {
        super(msg);
    }
}
