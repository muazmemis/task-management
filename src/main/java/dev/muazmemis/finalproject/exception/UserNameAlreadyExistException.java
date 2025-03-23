package dev.muazmemis.finalproject.exception;

import dev.muazmemis.finalproject.constant.ErrorMessages;

public class UserNameAlreadyExistException extends RuntimeException {
    public UserNameAlreadyExistException(String message) {
        super(String.format(ErrorMessages.USER_NAME_EXISTS, message));
    }
}
