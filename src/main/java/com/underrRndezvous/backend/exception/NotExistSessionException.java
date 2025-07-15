package com.underrRndezvous.backend.exception;

import com.underrRndezvous.backend.exception.base.ForbiddenAccessBaseException;

public class NotExistSessionException extends ForbiddenAccessBaseException {

    public NotExistSessionException() {
        super("로그인을 해주세요");
    }

}

