package com.padron.kinnov.Conexion;

import android.content.Context;

import com.padron.kinnov.ClaseEventos;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import java.net.Socket;
import java.security.KeyStore;
import java.util.ArrayList;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by jcpineda on 21/04/2016.
 */
public class Socket_TLS {

    /*
    Metodos:
    Usamos está función para conectarnos con algún servidor, solo debemos esocger el puerto y el servidor como dominio o IP
    Init_Socket_TLS(Puerto,Servidor,Context);

    Para cerrar el socket de comunnicación utilizamos está función, previamente abierto el socket
    Close_Socket_TLS();

    Para enviar un buffer lo enviamos con está función pasando el apuntador del buffer y el tamaño que deseamos enviar
    Send_Socket_TLS(Buffer, Tamaño);

    Procesos:
    Este proceso se inicia cuando se llama la función Init_Socket_TLS, se crea un nuevo hilo para escuchar lo que resiba el socket
    Process_Read_Socket_TLS;
     */

    //private static SSLSocket SocketTLS;

    private static Socket SocketTLS;
    public static boolean Conectado=false;
    public static byte[] BUFFER;
    public static boolean Scape=false;
    public static final byte INIT_FRAME  =   16;
    public static final byte FINAL_FRAME =   17;
    public static final byte DATA_LINK   =   18;
    public static final byte COMANDO     =   31;
    public static final byte[] COMMAND_UP ={23,25,27,29};
    public static final byte[] COMMAND_DOWN ={24,25,27,29};
    private static int indice=0;
    private static byte[] receive;
    public boolean Init_Socket_TLS(int Port, String Servidor, Context context){
        try {
            /*KeyStore trustStore = KeyStore.getInstance("BKS");
            InputStream trustStoreStream = context.getResources().openRawResource(R.raw.inled);
            trustStore.load(trustStoreStream, "Inled2801".toCharArray());
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
            SSLSocketFactory factory = sslContext.getSocketFactory();
            SocketTLS = (SSLSocket) factory.createSocket();
            SocketTLS.connect(new InetSocketAddress(Servidor, Port), 9000);
            SocketTLS.startHandshake();*/
            InetAddress Server=InetAddress.getByName(Servidor);
            Conectado=true;
            SocketTLS = new Socket(Server,Port);
            new Thread(new Process_Read_Socket_TLS()).start();
        } catch (Exception e){
            e.printStackTrace();
            Conectado=false;
            return false;
        }
        return true;
    }

    public  boolean Close_Socket_TLS(){
        if(SocketTLS.isConnected()){
            try {
                SocketTLS.close();
            } catch (IOException e) {
                e.printStackTrace();
                Conectado=false;
                return false;
            }
        }
        return true;
    }

    public  boolean Send_Socket_TLS(byte[] Buff, int len){
        if(SocketTLS.isConnected()){
            try {
                DataOutputStream out = new DataOutputStream(SocketTLS.getOutputStream());
                out.write(Buff,0,len);
            }catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }

    class Process_Read_Socket_TLS implements Runnable {

        @Override
        public void run() {
            byte Temp;
            try {
                ClaseEventos eventos= ClaseEventos.getInstance();
                InputStream inServer = SocketTLS.getInputStream();
                DataInputStream in = new DataInputStream(inServer);
                receive= new byte[50];
                boolean receiving=false;
                while (SocketTLS.isConnected()){
                    Temp = in.readByte();
                    if (Temp > -1){
                        if(unpack(Temp))
                            eventos.MSGCallback();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Empaqueta y devuelve si termina de leer
     * @param data
     * @return
     */
    public static boolean unpack(byte data){
        switch (data){
            case INIT_FRAME:
                if(!Scape) {
                    indice=0;
                    return false;
                }
            case FINAL_FRAME:
                if(!Scape) {
                    BUFFER=receive;
                    return true;
                }
                break;
            case DATA_LINK:
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
        paquete[i++]=INIT_FRAME;
        paquete[i++]=COMANDO;
        if(Boton==INIT_FRAME||Boton==FINAL_FRAME||Boton==DATA_LINK){
            paquete[i++]=DATA_LINK;
        }
        paquete[i++]=Boton;
        paquete[i++]=FINAL_FRAME;
        return paquete;
    }
}

