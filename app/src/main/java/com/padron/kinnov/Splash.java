package com.padron.kinnov;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.padron.kinnov.Conexion.Socket_TLS;


public class Splash extends AppCompatActivity {

    private Handler mHandler;
    private Runnable mRunnable;
    private int serverPort;
    private String serverAddress;
    public Socket_TLS socket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        hilo();
        mHandler=new Handler();
        mHandler.postDelayed(mRunnable,3000);
        getSharedPreferences();
        conectarServidor();
    }

    public void hilo(){
        mRunnable= new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                Splash.this.finish();
            }
        };

    }

    private void conectarServidor(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                socket = new Socket_TLS();
                socket.Init_Socket_TLS(serverPort,serverAddress,getBaseContext());
            }
        }).start();
    }
    private void getSharedPreferences(){
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        serverAddress = SP.getString("direccion_ip", "192.168.1.1");
        serverPort = Integer.parseInt(SP.getString("puerto","9999"));
    }

}
