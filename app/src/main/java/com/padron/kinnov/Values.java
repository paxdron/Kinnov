package com.padron.kinnov;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.Log;

import com.padron.kinnov.Conexion.SocketClient;
import com.padron.kinnov.events.CollapseClass;
import com.padron.kinnov.events.ICollapse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * La clase se encarga de procesar los mensajes, expandir los botones, colapsarlos y realizar los saltos necesario
 */
public class Values implements ICollapse {
    public static int T_PULSO=300;
    public static final String[] MODOS={"cont.","sinc.","rec.","sync"};

    public static final List<String> ArrayModos= Arrays.asList(MODOS);

    private static boolean isCont=false;
    public static int itemSelected=0;
    public static int currentItem=0;
    public static int NumItems=9;
    public static int Media=4;
    private final SocketClient socket;
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
    private int times;
    private Context context;
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
    }

    public void setValues(String texto,String Modo, int rawCursor, int colCursor){
        int modo= ArrayModos.indexOf(Modo.toLowerCase());
        if(modo==3)
            modo=1;
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
        Log.i("item Selected", String.valueOf(itemSelected));
        if(itemSelected>0) {
            if (itemSelected != 1)
                stim_mode.Collapse();
                posicionar();
            if (itemSelected != 1) {
                if (itemSelected == 2 || itemSelected == 3) {
                    if (itemSelected == 2)
                        carrier.collapseOthers(carrier.eSelector);
                    else
                        duracion_burst.collapseOthers(duracion_burst.eSelector);
                } else {
                    selected = (Campo) Elementos.get(itemSelected - 1);
                    selected.collapseOthers(selected.eSelector);
                }
            } else {
                for (int i = 1; i < Elementos.size(); i++) {
                    ((Campo) Elementos.get(i)).eSelector.collapse();
                }
            }
        }
    }



    public void posicionar(){
        int iSelected=(itemSelected==9&&isCont)?5:itemSelected;
        pasos = iSelected - currentItem;
        System.out.println(iSelected + " " + currentItem);
        if(pasos!=0) {
            if (Math.abs(pasos) <= Media) {
                times=Math.abs(pasos);
                if(pasos>0) {
                    MainActivity.sendData(SocketClient.pack(new byte[]{Constantes.COMMAND_PULSOS,(byte)times,Constantes.COMMAND_NEXT}),MainActivity.context);
                    //mHandler.post(mRunNext);
                }
                else
                    MainActivity.sendData(SocketClient.pack(new byte[]{Constantes.COMMAND_PULSOS,(byte)times,Constantes.COMMAND_BACK}),MainActivity.context);
                    //mHandler.post(mRunBack);
            }
            else{
                if(pasos>0) {
                    pasos=NumItems-pasos;
                    times=pasos;
                    MainActivity.sendData(SocketClient.pack(new byte[]{Constantes.COMMAND_PULSOS,(byte)times,Constantes.COMMAND_BACK}),MainActivity.context);
                    //mHandler.post(mRunBack);
                }
                else{
                    pasos=NumItems+pasos;
                    times=pasos;
                    MainActivity.sendData(SocketClient.pack(new byte[]{Constantes.COMMAND_PULSOS,(byte)times,Constantes.COMMAND_NEXT}),MainActivity.context);
                    //mHandler.post(mRunNext);
                }
            }
        }
    }

    public void parserCurentPos(int raw, int col){
        int anterior=currentItem;
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
                        if(NumItems==9)
                            currentItem=9;
                        else
                            currentItem=5;
                        break;

                }
            }
        }
        if(currentItem!=anterior)
            if(Constantes.IsManual)
                collapseAll();
    }

    private void collapseAll(){
        stim_mode.Collapse();
        for(int i=1;i<Elementos.size();i++)
            ((Campo) Elementos.get(i)).eSelector.collapse();
    }

    /**
     * modifica los valores del numero de elementos y la media depedindo si el modo de estimulacion es continuo o no
     * @param isCon Si el modo es continuo esta seleccionado o no
     */
    public static void setItemVals(boolean isCon){
        if(isCon){
            NumItems = 5;
            Media = 2;
        }else {
            NumItems=9;
            Media=4;
        }
        isCont=isCon;
    }


}
