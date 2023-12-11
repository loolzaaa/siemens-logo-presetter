package ru.loolzaaa.siemens.logopresetter.server;

public class BadRequestException extends RuntimeException{
    public BadRequestException(String message) {
        super(message);
    }
}
