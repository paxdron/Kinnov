package com.padron.kinnov;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.UiThread;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.padron.kinnov.Conexion.SocketClient;
import com.padron.kinnov.events.ISocketListener;

import mehdi.sakout.fancybuttons.FancyButton;

public class Channels extends AppCompatActivity implements ISocketListener{
    FancyButton stop;
    private  Canal C1,C2,C3,C4;
    SocketClient socket= new SocketClient();
    byte elemento;
    StringBuilder textoPantalla= new StringBuilder();
    byte []buffer;
    int i=0;
    private Canales canales;
    private String textoLCD;
    private String modo;
    private static Canal[] canalesUI;
    private byte leds;
    private Handler mLEDHandler;
    private Runnable mRunLeds= new Runnable() {
        @Override
        public void run() {
            for(int i=0;i<4;i++) {
                if ((leds & Constantes.MASKS[i]) == Constantes.MASKS[i]) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        canalesUI[i].tvTitle.setBackground(ContextCompat.getDrawable(getApplication(),R.drawable.rounded_corner));
                    }
                    else{
                        canalesUI[i].tvTitle.setBackgroundDrawable(ContextCompat.getDrawable(getApplication(),R.drawable.rounded_corner));
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        canalesUI[i].tvTitle.setBackground(ContextCompat.getDrawable(getApplication(), R.drawable.rounded_corner_transparent));
                    }else{
                        canalesUI[i].tvTitle.setBackgroundDrawable(ContextCompat.getDrawable(getApplication(), R.drawable.rounded_corner_transparent));
                    }
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channels);
        SocketClient.socketListener.registerCallback(this);
        stop=(FancyButton)findViewById(R.id.fabStop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.sendData(Constantes.STARTSTOP, getBaseContext());
            }
        });
        mLEDHandler= new Handler();
        canales= new Canales();

        C1= new Canal(0,
                (FancyButton)findViewById(R.id.btn_upCh1),
                (FancyButton)findViewById(R.id.btn_downCh1),
                (TextView)findViewById(R.id.tvCanal1Value),
                (TextView)findViewById(R.id.tvTituloC1)
                );
        C2= new Canal(1,
                (FancyButton)findViewById(R.id.btn_upCh2),
                (FancyButton)findViewById(R.id.btn_downCh2),
                (TextView)findViewById(R.id.tvCanal2Value),
                (TextView)findViewById(R.id.tvTituloC2)
                );
        C3= new Canal(2,
                (FancyButton)findViewById(R.id.btn_upCh3),
                (FancyButton)findViewById(R.id.btn_downCh3),
                (TextView)findViewById(R.id.tvCanal3Value),
                (TextView)findViewById(R.id.tvTituloC3)
                );
        C4= new Canal(3,
                (FancyButton)findViewById(R.id.btn_upCh4),
                (FancyButton)findViewById(R.id.btn_downCh4),
                (TextView)findViewById(R.id.tvCanal4Value),
                (TextView)findViewById(R.id.tvTituloC4)
                );
        canalesUI=new Canal[]{C1,C2,C3,C4};

        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf");
        for (Canal Chanel:canalesUI
             ) {
            Chanel.tvValue.setTypeface(custom_font);
        }

        ((TextView)findViewById(R.id.tvTituloC1)).setTypeface(custom_font);
        ((TextView)findViewById(R.id.tvTituloC2)).setTypeface(custom_font);
        ((TextView)findViewById(R.id.tvTituloC3)).setTypeface(custom_font);
        ((TextView)findViewById(R.id.tvTituloC4)).setTypeface(custom_font);
        stop.getTextViewObject().setTypeface(custom_font);
    }

    @Override
    public void OnNewMessage() {
        buffer=SocketClient.BUFFER;
        i=1;
        textoPantalla.setLength(0);
        leds=buffer[Constantes.POSLEDBYTE];
        elemento=buffer[i++];
        while(elemento!=2){
            textoPantalla.append((char)elemento);
            elemento=buffer[i++];
        }
        textoLCD=textoPantalla.toString();
        System.out.println(textoLCD);
        if(textoLCD.substring(0, 2).equals("1:")){
            canales.setValues(textoLCD);
            mLEDHandler.post(mRunLeds);
        }else {
            modo=textoLCD.substring(0,5).replaceAll("\\s+","").toLowerCase();
            if(Values.ArrayModos.contains(modo)){
                Channels.this.finish();
            }
            else{
                modo=textoLCD.substring(1,15).replaceAll("\\s+","").toLowerCase();
                Log.i("Modo: ", modo);
                if(Constantes.END.contains(modo)){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder dialogo = new AlertDialog.Builder(Channels.this);
                            dialogo.setTitle(getString(R.string.end_cycle)).setMessage(getString(R.string.t_start_stop)).setCancelable(false).setPositiveButton(getString(R.string.start_stop), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    MainActivity.sendData(Constantes.STARTSTOP,getApplicationContext());
                                }
                            }).show();
                        }
                    });

                }
                    //MainActivity.sendData(Constantes.STARTSTOP,getApplicationContext());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(SocketClient.isConnected())
            MainActivity.sendData(Constantes.UPDATETEXT,MainActivity.context);
    }

    @Override
    public void OnDisconnectedSocket() {

    }

    @Override
    public void OnTimeOut() {

    }

    public class Canal{

        private int CurrentValue;
        FancyButton Incrementar, Decrementar;
        TextView tvValue,tvTitle;
        int noChanel;
        public Canal(int noChanel,FancyButton incrementar, FancyButton decrementar, TextView tvValue,TextView tvTitle) {
            this.noChanel=noChanel;
            CurrentValue=0;
            Incrementar=incrementar;
            Decrementar=decrementar;
            this.tvValue=tvValue;
            this.tvTitle=tvTitle;
            setIncrementar();
            setDecrementar();
        }

        public void setCurrentValue(int currentValue) {
            CurrentValue = currentValue;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvValue.setText(Integer.toString(CurrentValue) + " mAh");
                }
            });

        }

        void Incrementar(){
            sendData(Constantes.COMMAND_UP[noChanel]);
        }
        void Decrementar(){
            sendData(Constantes.COMMAND_DOWN[noChanel]);
        }

        public void setIncrementar() {
            Incrementar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Incrementar();
                }
            });
        }

        public void setDecrementar() {
            Decrementar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Decrementar();
                }
            });

        }

        public void sendData(byte boton){
            byte[] pack = SocketClient.pack(boton);
            MainActivity.sendData(pack, getApplicationContext());
        }
    }

    class Canales{
        private int Canal1;
        private int Canal2;
        private int Canal3;
        private int Canal4;
        public void setCanal1(int canal1) {
            Canal1 = canal1;
            canalesUI[0].setCurrentValue(canal1);
        }

        public void setCanal2(int canal2) {
            Canal2 = canal2;
            canalesUI[1].setCurrentValue(canal2);
        }

        public void setCanal3(int canal3) {
            Canal3 = canal3;
            canalesUI[2].setCurrentValue(canal3);
        }

        public void setCanal4(int canal4) {
            Canal4 = canal4;
            canalesUI[3].setCurrentValue(canal4);
        }


        public Canales(){
            Canal1=Canal2=Canal3=Canal4=0;
        }
        public void setValues(String texto){
            setCanal1(Integer.valueOf(texto.substring(2,5).replaceAll("\\s+","")));
            setCanal2(Integer.valueOf(texto.substring(11,14).replaceAll("\\s+","")));
            setCanal3(Integer.valueOf(texto.substring(18,21).replaceAll("\\s+","")));
            setCanal4(Integer.valueOf(texto.substring(27,30).replaceAll("\\s+","")));
        }

        @Override
        public String toString() {
            StringBuilder sb= new StringBuilder();
            sb.append("Canal 1:\t"+Canal1+"\n");
            sb.append("Canal 2:\t"+Canal2+"\n");
            sb.append("Canal 3:\t"+Canal3+"\n");
            sb.append("Canal 4:\t" + Canal4 + "\n");
            return sb.toString();
        }

    }
}
