package com.padron.kinnov;

import android.content.Context;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;

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

    private static SSLSocket SocketTLS;

    public boolean Init_Socket_TLS(int Port, String Servidor, Context context){
        try {
            KeyStore trustStore = KeyStore.getInstance("BKS");
            InputStream trustStoreStream = context.getResources().openRawResource(R.raw.inled);
            trustStore.load(trustStoreStream, "Inled2801".toCharArray());
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
            SSLSocketFactory factory = sslContext.getSocketFactory();
            SocketTLS = (SSLSocket) factory.createSocket();
            SocketTLS.connect(new InetSocketAddress(Servidor, Port), 9000);
            SocketTLS.startHandshake();
            new Thread(new Process_Read_Socket_TLS()).start();
        } catch (Exception e){
            e.printStackTrace();
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
                InputStream inServer = SocketTLS.getInputStream();
                DataInputStream in = new DataInputStream(inServer);
                while (SocketTLS.isConnected()){
                    Temp = in.readByte();
                    if (Temp > -1){

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
