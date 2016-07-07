package com.padron.kinnov;

import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.padron.kinnov.Conexion.SocketClient;
import com.padron.kinnov.events.ISocketListener;

import java.util.ArrayList;
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

public class ProgMenu extends AppCompatActivity implements ISocketListener{

    private static List<protocolo> ListaProtocolos;
    private FancyButton btnSeleccionar;
    private StringBuilder textoPantalla= new StringBuilder();
    private String textoLCD;
    private String modo;
    int i=0;
    byte []buffer;
    byte elemento;
    private int indice,pasos,times;
    private int current;
    private boolean isMoving;
    private Handler mhandHandler;
    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if(!isMoving) {
                ListaProtocolos.get(indice - 1).Seleccionar();
            }
        }
    };
    private List<String> PrtclsbyLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prog_menu);
        SocketClient.socketListener.registerCallback(this);
        btnSeleccionar= (FancyButton)findViewById(R.id.btnSeleccionar);
        ListaProtocolos= new ArrayList<>();
        ListaProtocolos.add(new protocolo((LinearLayout)findViewById(R.id.item1),(TextView) findViewById(R.id.tv_titulo1),(TextView) findViewById(R.id.tv_desc1)));
        ListaProtocolos.add(new protocolo((LinearLayout)findViewById(R.id.item2),(TextView) findViewById(R.id.tv_titulo2),(TextView) findViewById(R.id.tv_desc2)));
        ListaProtocolos.add(new protocolo((LinearLayout)findViewById(R.id.item3),(TextView) findViewById(R.id.tv_titulo3),(TextView) findViewById(R.id.tv_desc3)));
        ListaProtocolos.add(new protocolo((LinearLayout)findViewById(R.id.item4),(TextView) findViewById(R.id.tv_titulo4),(TextView) findViewById(R.id.tv_desc4)));
        ListaProtocolos.add(new protocolo((LinearLayout)findViewById(R.id.item5),(TextView) findViewById(R.id.tv_titulo5),(TextView) findViewById(R.id.tv_desc5)));
        ListaProtocolos.add(new protocolo((LinearLayout)findViewById(R.id.item6),(TextView) findViewById(R.id.tv_titulo6),(TextView) findViewById(R.id.tv_desc6)));
        ListaProtocolos.add(new protocolo((LinearLayout)findViewById(R.id.item7),(TextView) findViewById(R.id.tv_titulo7),(TextView) findViewById(R.id.tv_desc7)));
        setLabels();
        btnSeleccionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.sendData(Constantes.PROGMENU, getBaseContext());
            }
        });
        mhandHandler= new Handler();
        MainActivity.sendData(Constantes.UPDATETEXT, this);
    }

    private void setLabels(){
        String[] Titulos= getResources().getStringArray(R.array.titleProtocolos);
        String[] Descripciones= getResources().getStringArray(R.array.infoProtocolos);

        for (int i=0;i<ListaProtocolos.size();i++){
            ListaProtocolos.get(i).setTvText(Titulos[i],Descripciones[i]);
        }
    }

    public static void collapseOthers(protocolo protocol){
        protocolo protocolo;
        for(int i=0;i<ListaProtocolos.size();i++){
            protocolo=ListaProtocolos.get(i);
            if(!protocolo.equals(protocol))
                protocolo.deSeleccionar();
        }
    }

    @Override
    public void OnNewMessage() {
        buffer=SocketClient.BUFFER;
        i=1;
        textoPantalla.setLength(0);
        elemento=buffer[i++];
        while(elemento!=2){
            textoPantalla.append((char)elemento);
            elemento=buffer[i++];
        }
        textoLCD=textoPantalla.toString();
        modo=textoLCD.substring(0,5).replaceAll("\\s+","").toLowerCase();
        /*
            Mod dolor mec.  descendiente
            Mod dolor       mec. ascendente
            Reduccion edema dren. linfatico
            FES apos ACV
            Fort. despues deatrofia desuso
            Reeducacion     motriz
            Fort. muscular  en atletas
         */

        PrtclsbyLng=(Constantes.IDIOMA==1)?Constantes.PROTOCOLS:(Constantes.IDIOMA==2)?Constantes.PROTOCOLSEN:Constantes.PROTOCOLSPT;

        if((PrtclsbyLng.contains(modo))){
            switch(modo){
                case "modd":
                case "mod.":
                case "pain":
                    if(Constantes.IDIOMA==1) {
                        if (textoLCD.charAt(16) == 'd')
                            indice = 1;
                        else if (textoLCD.charAt(16) == 'm')
                            indice = 2;
                    }else {
                        if (Constantes.IDIOMA == 2) {
                            if (textoLCD.charAt(16) == 'd')
                                indice = 1;
                            else if (textoLCD.charAt(16) == 'a')
                                indice = 2;
                        }else {
                            if (Constantes.IDIOMA == 3) {
                                if (textoLCD.charAt(21) == 'd')
                                    indice = 1;
                                else if (textoLCD.charAt(21) == 'a')
                                    indice = 2;
                            }
                        }
                    }
                    break;
                case "reduc":
                case "oedem":
                    indice=3;
                    break;
                case "fesa":
                case "fesp":
                    indice=4;
                    break;
                case  "fort.":
                case  "stren":
                    if(Constantes.IDIOMA==2) {
                        if (textoLCD.charAt(16) == 'd')
                            indice = 5;
                        else if (textoLCD.charAt(16) == 'i')
                            indice = 7;
                    }else {
                        if (Constantes.IDIOMA == 1) {
                            if (textoLCD.charAt(6) == 'd')
                                indice = 5;
                            else if (textoLCD.charAt(6) == 'm')
                                indice = 7;
                        }
                        else{
                            if (textoLCD.charAt(16) == 'a')
                                indice = 5;
                            else if (textoLCD.charAt(16) == 'e')
                                indice = 7;
                        }
                    }
                    break;
                case "reedu":
                case "motor":
                    indice=6;
                    break;
            }
            current=indice-1;

            mhandHandler.post(mRunnable);
        }else {
            ProgMenu.this.finish();
        }

    }

    @Override
    public void OnDisconnectedSocket() {

    }

    @Override
    public void OnTimeOut() {

    }

    /**
     * Determina el puso que hay que dar para modificar el modo de estimuladion en pantalla
     * @param currentPos el valor del modo actual
     * @param newPos el valor del nuevo modo
     */
    private void setMode(int currentPos, int newPos){
        pasos = newPos-currentPos;
        if(pasos!=0) {
            isMoving=true;
            if (Math.abs(pasos) <= Constantes.MEDIAPROTOCOLS) {
                times=Math.abs(pasos);
                if(pasos>0) {
                    //mHandPulse.postDelayed(mRunDown, Constantes.DELAY+50);
                    MainActivity.sendData(SocketClient.pack(new byte[]{Constantes.COMMAND_PULSOS,(byte)times,Constantes.COMMAND_SETDOWN}),ProgMenu.this);
                    isMoving=false;
                }
                else {
                    MainActivity.sendData(SocketClient.pack(new byte[]{Constantes.COMMAND_PULSOS, (byte) times, Constantes.COMMAND_SETUP}), ProgMenu.this);
                    isMoving=false;
                }
                    //mHandPulse.postDelayed(mRunUP, Constantes.DELAY+50);

            }
            else{
                if(pasos>0) {
                    pasos=Constantes.NUMPROTOCOLS-pasos;
                    times=pasos;
                    //mHandPulse.postDelayed(mRunUP, Constantes.DELAY+50);
                    MainActivity.sendData(SocketClient.pack(new byte[]{Constantes.COMMAND_PULSOS,(byte)times,Constantes.COMMAND_SETUP}),ProgMenu.this);
                    isMoving=false;
                }
                else{
                    pasos=Constantes.NUMPROTOCOLS+pasos;
                    times=pasos;
                    //mHandPulse.postDelayed(mRunDown, Constantes.DELAY+50);
                    MainActivity.sendData(SocketClient.pack(new byte[]{Constantes.COMMAND_PULSOS,(byte)times,Constantes.COMMAND_SETDOWN}),ProgMenu.this);
                    isMoving=false;
                }
            }

        }
    }

    class protocolo{
        TextView tvTitulo;
        TextView tvDescripcion;
        LinearLayout contenedor;
        boolean isSelected;

        public protocolo(LinearLayout contenedor, TextView tvTitulo, TextView tvDescripcion) {
            this.tvTitulo = tvTitulo;
            this.tvDescripcion = tvDescripcion;
            this.contenedor=contenedor;
            this.tvTitulo.setTypeface(MainActivity.custom_font);
            tvDescripcion.setVisibility(View.GONE);
            contenedor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Seleccionar();
                }
            });
        }

        public void setTvText(String textTitle,String textDesc){
            tvTitulo.setText(textTitle);
            tvDescripcion.setText(textDesc);
        }

        public void Seleccionar(){
            isSelected=true;
            tvDescripcion.setVisibility(View.VISIBLE);
            tvTitulo.setBackgroundColor(ContextCompat.getColor(ProgMenu.this,R.color.colorPrimary));
            tvTitulo.setTextColor(ContextCompat.getColor(ProgMenu.this,R.color.colorWhite));
            collapseOthers(this);
            setMode(indice,ListaProtocolos.indexOf(this)+1);
        }
        public void deSeleccionar(){
            isSelected=false;
            tvDescripcion.setVisibility(View.GONE);
            tvTitulo.setBackgroundColor(ContextCompat.getColor(ProgMenu.this,R.color.colorWhite));
            tvTitulo.setTextColor(ContextCompat.getColor(ProgMenu.this,R.color.colorPrimary));
        }
    }
}
