package com.padron.kinnov.events;

/**
 * Created by Antonio on 19/05/2016.
 */
public interface ISocketListener {
    void OnNewMessage();
    void OnDisconnectedSocket();
    void OnTimeOut();
}
