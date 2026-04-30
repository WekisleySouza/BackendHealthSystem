package com.project.healthsystem.exceptions;

public class CantDeleteException extends RuntimeException {
    public CantDeleteException(String message) {
        super(message);
    }
}
