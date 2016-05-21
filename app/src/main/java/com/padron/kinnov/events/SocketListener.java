package com.padron.kinnov.events;

/**
 * Created by Antonio on 19/05/2016.
 */
public class SocketListener {
    public static SocketListener instancia;
    public static boolean isCreated=false;
    ISocketListener CallbackSocketListener;

    public static SocketListener getInstance(){
        if(!isCreated){
            instancia=new SocketListener();
            isCreated=true;
        }
        return instancia;
    }

    public void registerCallback(ISocketListener callbackClass){
        CallbackSocketListener = callbackClass;
    }

    public void newMessage(){
        CallbackSocketListener.OnNewMessage();
    }
    public void disconnectedSocket(){
        CallbackSocketListener.OnDisconnectedSocket();
    }
    public void timeOut(){
        CallbackSocketListener.OnTimeOut();
    }

}