package com.padron.kinnov;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;

import com.padron.kinnov.Conexion.SocketClient;
import com.padron.kinnov.events.CollapseClass;
import com.padron.kinnov.events.ICollapse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by antonio on 12/05/16.
 */
public class Values implements ICollapse {
    public static int T_PULSO=300;
    public static final String[] MODOS={"cont.","sinc.","rec."};
    public static final List<String> ArrayModos= Arrays.asList(MODOS);
    public static int itemSelected=0;
    public static int currentItem=0;
    public static int NumItems=9;
    public static int Media=4;

    public CollapseClass collapseClass;
    private StimMode stim_mode;
    private Campo carrier;
    private Campo duracion_burst;
    private Campo frecuencia_burst;
    private Campo tiempo_aplicacion;
    private Campo rise;
    private Campo on;
    private Campo decay;
    private Campo off;
    private ArrayList<Object> Elementos;
    private Campo selected;
    private int pasos=0;
    private SocketClient socket;
    private byte[] BackButton;
    private byte[] NextButton;
    private Handler mHandler;
    private int times;
    private Context context;
    Runnable mRunNext= new Runnable() {
        @Override
        public void run() {
            if(times!=0)
            {
                times--;
                MainActivity.sendData(Constantes.NEXTPULSE, context);
                mHandler.postDelayed(mRunNext, Constantes.DELAY);
            }
        }
    };
    Runnable mRunBack= new Runnable() {
        @Override
        public void run() {
            if(times!=0)
            {
                times--;
                MainActivity.sendData(Constantes.BACKPULSE,context);
                mHandler.postDelayed(mRunBack, Constantes.DELAY);
            }
        }
    };


    public Values(StimMode stim_mode,
                  Campo carrier,
                  Campo duracion_burst,
                  Campo frecuencia_burst,
                  Campo rise,
                  Campo on,
                  Campo decay,
                  Campo off,
                  Campo tiempo_aplicacion,
                  SocketClient socket, Context context) {

        this.stim_mode=stim_mode;
        this.carrier=carrier;
        this.duracion_burst=duracion_burst;
        this.frecuencia_burst=frecuencia_burst;
        this.rise=rise;
        this.on=on;
        this.decay=decay;
        this.off=off;
        this.tiempo_aplicacion=tiempo_aplicacion;
        this.socket=socket;
        this.context=context;
        Elementos= new ArrayList<>();
        Elementos.add(stim_mode);
        Elementos.add(carrier);
        Elementos.add(duracion_burst);
        Elementos.add(frecuencia_burst);
        Elementos.add(rise);
        Elementos.add(on);
        Elementos.add(decay);
        Elementos.add(off);
        Elementos.add(tiempo_aplicacion);
        collapseClass=CollapseClass.getInstance();
        collapseClass.registerCallback(this);
        setIds();
        mHandler= new Handler();
    }

    public void setValues(String texto,String Modo, int rawCursor, int colCursor){
        int modo= ArrayModos.indexOf(Modo.toLowerCase());
        stim_mode.selectMode(modo);
        carrier.setCurrentValue1(Integer.valueOf(texto.substring(7, 8)));
        duracion_burst.setCurrentValue1(Integer.valueOf(texto.substring(10, 11)));
        frecuencia_burst.setCurrentValue(Integer.valueOf(texto.substring(13, 16).replaceAll("\\s+", "")));
        if(modo!=0){
            rise.setCurrentValue(Integer.valueOf(texto.substring(16,18).replaceAll("\\s+","")));
            on.setCurrentValue(Integer.valueOf(texto.substring(19,21).replaceAll("\\s+","")));
            decay.setCurrentValue(Integer.valueOf(texto.substring(22,24).replaceAll("\\s+","")));
            off.setCurrentValue(Integer.valueOf(texto.substring(25,27).replaceAll("\\s+","")));
        }
        tiempo_aplicacion.setCurrentValue(Integer.valueOf(texto.substring(30, 32).replaceAll("\\s+", "")));
        parserCurentPos(rawCursor, colCursor);
    }

    public void setIds(){
        stim_mode.setIdentificador(1);
        carrier.setIdentificador(2);
        duracion_burst.setIdentificador(3);
        frecuencia_burst.setIdentificador(4);
        rise.setIdentificador(5);
        on.setIdentificador(6);
        decay.setIdentificador(7);
        off.setIdentificador(8);
        tiempo_aplicacion.setIdentificador(9);
    }



    @Override
    public void callbackCollapse() {
        stim_mode.Collapse();
        posicionar();
        if(itemSelected!=1)
            if(itemSelected==2||itemSelected==3) {
                if (itemSelected == 2)
                    carrier.collapseOthers(carrier.eSelector);
                else
                    duracion_burst.collapseOthers(duracion_burst.eSelector);
            }
            else
            {
                selected=(Campo) Elementos.get(itemSelected-1);
                selected.collapseOthers(selected.eSelector);
            }
    }

    public void posicionar(){
        pasos = itemSelected - currentItem;
        System.out.println(itemSelected +" "+ currentItem);
        if(pasos!=0) {
            if (Math.abs(pasos) <= Media) {
                times=Math.abs(pasos);
                if(pasos>0) {
                    mHandler.postDelayed(mRunNext, Constantes.DELAY);
                }
                else
                    mHandler.postDelayed(mRunBack, Constantes.DELAY);
            }
            else{
                if(pasos>0) {
                    pasos=NumItems-pasos;
                    times=pasos;
                    mHandler.postDelayed(mRunBack, Constantes.DELAY);
                }
                else{
                    pasos=NumItems-pasos;
                    times=pasos;
                    mHandler.postDelayed(mRunNext, Constantes.DELAY);
                }
            }
        }
    }

    public void parserCurentPos(int raw, int col){
        if(raw==0){
            switch(col){
                case 0:
                    currentItem=1;
                    break;
                case 7:
                    currentItem=2;
                    break;
                case 10:
                    currentItem=3;
                    break;
                case 15:
                    currentItem=4;
                    break;

            }
        }else {
            if (raw == 1) {
                switch (col) {
                    case 1:
                        currentItem=5;
                        break;
                    case 4:
                        currentItem=6;
                        break;
                    case 7:
                        currentItem=7;
                        break;
                    case 10:
                        currentItem=8;
                        break;
                    case 15:
                        currentItem=9;
                        break;

                }
            }
        }
    }


}
