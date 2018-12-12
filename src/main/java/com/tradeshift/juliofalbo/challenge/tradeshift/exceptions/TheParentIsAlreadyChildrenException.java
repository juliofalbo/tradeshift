package com.tradeshift.juliofalbo.challenge.tradeshift.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TheParentIsAlreadyChildrenException extends RuntimeException {
    public TheParentIsAlreadyChildrenException(String msg) {
        super(msg);
    }
}
