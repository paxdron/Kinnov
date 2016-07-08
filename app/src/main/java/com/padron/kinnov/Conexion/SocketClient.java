package com.padron.kinnov.Conexion;

import android.util.Log;

import com.padron.kinnov.Constantes;
import com.padron.kinnov.events.SocketListener;
import com.padron.kinnov.exceptions.ServerNotFound;
import com.padron.kinnov.exceptions.SocketClosed;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Se encarga de la conexion del socket, el empaquetado y desempaquetado de los mensajes
 */
public class SocketClient {
    private static Socket Socket;
    private static boolean isConnected=false;
    private static String ServerAddress;
    private static int Port;
    private static int indice=0;
    private static byte[] receive;
    public static byte[] BUFFER;
    public static boolean Scape=false;
    public static SocketListener socketListener= SocketListener.getInstance();
    private static DataOutputStream out;

    public Socket getSocket() throws ServerNotFound {
        if(!isConnected) {
            try {
                Socket = new Socket(ServerAddress, Port);
                new Thread(new Read_Socket()).start();
                isConnected=Socket.isConnected();
            }catch( UnknownHostException e ) {
                Log.i("Error",
                        "Debes estar conectado para que esto funcione bien." );
            }
            catch (IOException e) {
                e.printStackTrace();
                isConnected=false;
                throw new ServerNotFound();
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
                isConnected=false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public static boolean Send_Socket_TLS(byte[] Buff, int len) throws SocketClosed {
        if(isConnected){
            try {
                out.write(Buff, 0, len);
            }catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        else{
            throw new SocketClosed();
        }
    }



    class Read_Socket implements Runnable{

        @Override
        public void run() {
            byte Temp;
            try {
                out = new DataOutputStream(Socket.getOutputStream());
                InputStream inServer = Socket.getInputStream();
                DataInputStream in = new DataInputStream(inServer);
                receive= new byte[50];
                boolean receiving=false;

                while (Socket.isConnected()){
                    Temp = in.readByte();
                    if (Temp > -1){
                        if(unpack(Temp)){
                            socketListener.newMessage();
                        }
                            //eventos.MSGCallback();
                    }
                }
            }
            catch (EOFException e){
                isConnected=false;
                socketListener.disconnectedSocket();
            } catch (SocketException e){
                isConnected=false;
                try {
                    getSocket();
                } catch (ServerNotFound serverNotFound) {
                    serverNotFound.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean unpack(byte data){
        switch (data){
            case Constantes.INIT_FRAME:
                if(!Scape) {
                    indice=0;
                    return false;
                }
            case Constantes.FINAL_FRAME:
                if(!Scape) {
                    BUFFER=receive;
                    return true;
                }
                break;
            case Constantes.DATA_LINK:
                if(!Scape) {
                    Scape = true;
                    return false;
                }
                break;
            default:
                break;
        }
        receive[indice++]=data;
        Scape=false;
        return false;
    }

    public static byte[] pack(byte Boton){
        byte[] paquete = new byte[5];
        int i=0;
        paquete[i++]=Constantes.INIT_FRAME;
        paquete[i++]=Constantes.COMANDO;
        if(Boton==Constantes.INIT_FRAME||Boton==Constantes.FINAL_FRAME||Boton==Constantes.DATA_LINK){
            paquete[i++]=Constantes.DATA_LINK;
        }
        paquete[i++]=Boton;
        paquete[i]=Constantes.FINAL_FRAME;
        return paquete;
    }

    public static byte[] pack(byte[] comandos){
        byte[] paquete = new byte[7];
        int i=0;
        paquete[i++]=Constantes.INIT_FRAME;
        for(int j=0;j<comandos.length;j++) {
            if (comandos[j] == Constantes.INIT_FRAME || comandos[j] == Constantes.FINAL_FRAME || comandos[j] == Constantes.DATA_LINK) {
                paquete[i++] = Constantes.DATA_LINK;
            }
            paquete[i++] = comandos[j];
        }
        paquete[i]=Constantes.FINAL_FRAME;
        for(int j=0;j<paquete.length;j++)
        System.out.print(paquete[j++] +" ");
        return paquete;
    }

    public static boolean isConnected() {
        return isConnected;
    }
}
