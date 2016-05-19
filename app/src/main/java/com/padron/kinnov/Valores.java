package com.padron.kinnov;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Antonio on 09/05/2016.
 */
public class Valores{
    public static final String[] MODOS={"Cont.","Sinc.","Rec."};
    public static final List<String> ArrayModos= Arrays.asList(MODOS);
    private int Modo; //Continuo=0,Reciproco=1, Sincronizado=2
    private int Frec_Carrier;
    private int Duracion_Burst;
    private int Frecuencia_burst;
    private int Tiempo_Ascenso;
    private int Tiempo_Encendido;
    private int Tiempo_Bajada;
    private int Tiempo_Apagado;
    private int Tiempo_Aplicacion;

    public Valores(){
        Modo=0;
        Frec_Carrier=Frecuencia_burst=Tiempo_Ascenso=Tiempo_Encendido=Tiempo_Bajada=Tiempo_Apagado=Tiempo_Aplicacion=1;
        Duracion_Burst=2;

    }

    public void setValues(String texto,int modo){
        Modo=modo;
        Frec_Carrier		=	Integer.valueOf(texto.substring(7,8));
        Duracion_Burst		=	Integer.valueOf(texto.substring(10,11));
        Frecuencia_burst	=	Integer.valueOf(texto.substring(13,16).replaceAll("\\s+",""));
        if(Modo!=0){
            Tiempo_Ascenso		=	Integer.valueOf(texto.substring(16,18).replaceAll("\\s+",""));
            Tiempo_Encendido	=	Integer.valueOf(texto.substring(19,21).replaceAll("\\s+",""));
            Tiempo_Bajada		=	Integer.valueOf(texto.substring(22,24).replaceAll("\\s+",""));
            Tiempo_Apagado		=	Integer.valueOf(texto.substring(25,27).replaceAll("\\s+",""));
        }
        Tiempo_Aplicacion	=	Integer.valueOf(texto.substring(30,32).replaceAll("\\s+",""));
    }

    @Override
    public String toString() {
        StringBuilder sb= new StringBuilder();
        sb.append("Modo:\t"		+ MODOS[Modo]+"\n");
        sb.append("Carrier:\t"	+ Frec_Carrier+"\n");
        sb.append("Duracion Burst:\t"	+ Duracion_Burst+"\n");
        sb.append("Freq Burst:\t"	+ Frecuencia_burst+"\n");
        if(Modo!=0){
            sb.append("Tiempo Ascenso:\t: "	+ Tiempo_Ascenso+"\n");
            sb.append("Tiempo ON:\t"	+ Tiempo_Encendido+"\n");
            sb.append("Tiempo Bajada:\t"	+ Tiempo_Bajada+"\n");
            sb.append("Tiempo OFF:\t"	+ Tiempo_Apagado+"\n");
        }
        sb.append("Tiempo Aplicación:"	+ Tiempo_Aplicacion+"\n");

        return sb.toString();
    }
}
