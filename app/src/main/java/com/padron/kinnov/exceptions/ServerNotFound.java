package com.padron.kinnov.exceptions;

/**
 * Created by Antonio on 17/05/2016.
 */
public class ServerNotFound extends Exception{
    public ServerNotFound() {
        super("El servidor no está disponible");
    }
}
