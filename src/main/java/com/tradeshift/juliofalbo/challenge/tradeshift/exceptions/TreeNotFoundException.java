package com.tradeshift.juliofalbo.challenge.tradeshift.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TreeNotFoundException extends RuntimeException {
    public TreeNotFoundException(String msg) {
        super(msg);
    }
}
