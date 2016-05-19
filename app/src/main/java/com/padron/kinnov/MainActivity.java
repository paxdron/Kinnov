package com.padron.kinnov;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.karumi.expandableselector.ExpandableItem;
import com.karumi.expandableselector.ExpandableSelector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.padron.kinnov.Conexion.Socket_TLS;
import com.padron.kinnov.events.Event;
import com.padron.kinnov.events.IEventHandler;

import mehdi.sakout.fancybuttons.FancyButton;

public class MainActivity extends AppCompatActivity {
    private List<ExpandableSelector> eSelectors;
    private FancyButton start;
    private Typeface custom_font;

    private FancyButton modo1,modo2,modo3;
    private String[] modosLabels;
    private FancyButton[] modosBtns;
    private LinearLayout layouttAscenso, layouttEncendido, layouttBajada, layouttReposo;
    public ClaseEventos eventosListener;
    public Socket_TLS socket;
    byte elemento;
    StringBuilder textoPantalla= new StringBuilder();
    byte []buffer;
    int i=0;
    private String textoLCD;
    private String modo;
    private int ColCursor, RawCursor;
    StimMode stimMode;
    Values values;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        eSelectors= new ArrayList<>();
        initializeUI();
        modoEstimulacion();
        initializeExpandableSelector();
        textoPantalla= new StringBuilder();
        eventosListener=ClaseEventos.getInstance();
        eventosListener.addEventListener(Event.MSGRCV, new IEventHandler() {
            @Override
            public void callback(Event event) {
                parseMessage();
            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*String texto = "Cont.  4  4  120              66";
                modo = texto.substring(0, 5).replaceAll("\\s+", "");
                if (Values.ArrayModos.contains(modo)) {
                    values.setValues(texto, modo);
                }*/
                byte[] pack = Socket_TLS.pack((byte) 17);
                socket.Send_Socket_TLS(pack, pack.length);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUI();
    }

    private void initializeExpandableSelector() {
        eSelectors.add((ExpandableSelector) findViewById(R.id.es_carrier));
        Campo campCarrier=new Campo(eSelectors.get(0),getString(R.string.khz), new int[]{1, 4},eSelectors,socket);

        eSelectors.add((ExpandableSelector) findViewById(R.id.es_duartion_burst));
        Campo campDurationBurst=new Campo(eSelectors.get(1),getString(R.string.ms), new int[]{2, 4},eSelectors,socket);

        eSelectors.add((ExpandableSelector) findViewById(R.id.es_freq_burst));
        Campo campFreqBurst = new Campo(eSelectors.get(2), getString(R.string.hz), 1,120,eSelectors,socket);

        eSelectors.add((ExpandableSelector) findViewById(R.id.es_tAscenso));
        Campo campRise = new Campo(eSelectors.get(3), getString(R.string.s), 1,20,eSelectors,socket);

        eSelectors.add((ExpandableSelector) findViewById(R.id.es_tEncendido));
        Campo campOn = new Campo(eSelectors.get(4), getString(R.string.s), 1,60,eSelectors,socket);

        eSelectors.add((ExpandableSelector) findViewById(R.id.es_tBajada));
        Campo campDecay = new Campo(eSelectors.get(5), getString(R.string.s), 1,20,eSelectors,socket);

        eSelectors.add((ExpandableSelector) findViewById(R.id.es_tReposo));
        Campo campOff = new Campo(eSelectors.get(6), getString(R.string.s), 1,60,eSelectors,socket);

        eSelectors.add((ExpandableSelector) findViewById(R.id.es_tAplicacion));
        Campo campAplication=new Campo(eSelectors.get(7),getString(R.string.m), 1,60,eSelectors,socket);
        /*eSelectors.add((ExpandableSelector) findViewById(R.id.es_modoEstim));
        Campo campSimmMode = new Campo(eSelectors.get(8),getResources().getStringArray(R.array.stimm_mode),eSelectors);*/
        values= new Values(stimMode,campCarrier,campDurationBurst,campFreqBurst,campRise,campOn,campDecay,campOff,campAplication,socket);
    }


    public void modoEstimulacion(){
        TextView tx = (TextView)findViewById(R.id.tituloModo);
        modo1=(FancyButton)findViewById(R.id.modo1);
        modo2=(FancyButton)findViewById(R.id.modo2);
        modo3=(FancyButton)findViewById(R.id.modo3);
        modosBtns = new FancyButton[]{modo1, modo2, modo3};
        modosLabels=getResources().getStringArray(R.array.stimm_mode);
        tx.setTypeface(custom_font);
        stimMode=new StimMode(modosBtns,modosLabels,new LinearLayout[]{layouttAscenso,layouttEncendido,layouttBajada,layouttReposo,});
       /* for (FancyButton fB:modosBtns
                ) {
            fB.getTextViewObject().setTypeface(custom_font);
        }*/

    }

    private void initializeUI(){
        custom_font= Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf");
        ((TextView)findViewById(R.id.tvcarrier)).setTypeface(custom_font);
        ((TextView)findViewById(R.id.tvduartion_burst)).setTypeface(custom_font);
        ((TextView)findViewById(R.id.tvfreq_burst)).setTypeface(custom_font);
        ((TextView)findViewById(R.id.tvtAscenso)).setTypeface(custom_font);
        ((TextView)findViewById(R.id.tvtEncendido)).setTypeface(custom_font);
        ((TextView)findViewById(R.id.tvtBajada)).setTypeface(custom_font);
        ((TextView)findViewById(R.id.tvtReposo)).setTypeface(custom_font);
        ((TextView)findViewById(R.id.tvtAplicacion)).setTypeface(custom_font);
        layouttAscenso=(LinearLayout)findViewById(R.id.layouttAscenso);
        layouttEncendido=(LinearLayout)findViewById(R.id.layouttEncendido);
        layouttBajada=(LinearLayout)findViewById(R.id.layouttBajada);
        layouttReposo=(LinearLayout)findViewById(R.id.layouttReposo);
        start = (FancyButton) findViewById(R.id.fabStart);
        start.getTextViewObject().setTypeface(custom_font);
        Toolbar toolbar =(Toolbar)findViewById(R.id.myToolbar);
        toolbar.setTitle("");
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setTypeface(custom_font);
        setSupportActionBar(toolbar);
    }



    public void menu(View v){
        startActivity(new Intent(this, MenuMaquina.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.network:
                //Toast.makeText(this, "ADD!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!Socket_TLS.Conectado){
            updateUI();
        }
        eventosListener.addEventListener(Event.MSGRCV, new IEventHandler() {
            @Override
            public void callback(Event event) {
                parseMessage();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        eventosListener.removeEventListener(Event.MSGRCV);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(!Socket_TLS.Conectado){
            updateUI();
        }

    }

    public void parseMessage(){
        buffer=Socket_TLS.BUFFER;
        i=1;
        textoPantalla.setLength(0);
        elemento=buffer[i++];
        System.out.println();
        while(elemento!=2){
            textoPantalla.append((char) elemento);
            System.out.print(elemento+" ");
            elemento=buffer[i++];
            if(elemento==2&&i<33)
                elemento=buffer[i];
        }
        RawCursor=(int)buffer[i++];
        ColCursor=(int)buffer[i++];
        textoLCD=textoPantalla.toString();
        System.out.println(textoLCD);
        if(textoLCD.substring(0, 2).equals("1:")){
            startActivity(new Intent(getApplicationContext(), Channels.class));
        }else {
            modo=textoLCD.substring(0,5).replaceAll("\\s+","").toLowerCase();
            System.out.println("modo: "+modo);
            if(Values.ArrayModos.contains(modo)){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        values.setValues(textoLCD, modo,RawCursor,ColCursor);
                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(Socket_TLS.Conectado){
            socket.Close_Socket_TLS();
        }
    }


    public void updateUI(){
        if(socket.Conectado)
            sendData(Socket_TLS.UPDATETEXT);
    }

    public void sendData(byte boton){
        byte[] pack = Socket_TLS.pack(boton);
        socket.Send_Socket_TLS(pack, pack.length);
    }

}
