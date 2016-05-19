package com.padron.kinnov.Conexion;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Antonio on 17/05/2016.
 */
public class SocketClient {
    private static Socket Socket;
    private static boolean isConnected=false;
    private static String ServerAddress;
    private static int Port;

    public Socket getSocket(){
        if(!isConnected) {
            try {
                Socket = new Socket(ServerAddress, Port);
                isConnected=Socket.isConnected();
            } catch (IOException e) {
                e.printStackTrace();
                isConnected=false;
            }
        }
        return Socket;
    }

    public static void setServerArgs(String serverAddress, int port){
        ServerAddress=serverAddress;
        Port=port;
    }
    public static boolean Desconectar(){
        if(isConnected){
            try {
                Socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
}
