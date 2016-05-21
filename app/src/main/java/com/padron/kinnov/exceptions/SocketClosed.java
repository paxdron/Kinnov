package com.padron.kinnov.exceptions;

/**
 * Created by Antonio on 19/05/2016.
 */
public class SocketClosed extends Exception{
    public SocketClosed() {
        super("El Socket Está Desconectado");
    }
}
