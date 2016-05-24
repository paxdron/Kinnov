package com.padron.kinnov;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidadvance.topsnackbar.TSnackbar;
import com.karumi.expandableselector.ExpandableSelector;
import java.util.ArrayList;
import java.util.List;

import com.padron.kinnov.Conexion.SocketClient;
import com.padron.kinnov.events.ISocketListener;
import com.padron.kinnov.exceptions.ServerNotFound;
import com.padron.kinnov.exceptions.SocketClosed;

import mehdi.sakout.fancybuttons.FancyButton;

public class MainActivity extends AppCompatActivity implements ISocketListener {
    private List<ExpandableSelector> eSelectors;
    private FancyButton start;
    private Typeface custom_font;
    public static Context context;
    private FancyButton modo1,modo2,modo3;
    private String[] modosLabels;
    private FancyButton[] modosBtns;
    private LinearLayout layouttAscenso, layouttEncendido, layouttBajada, layouttReposo;
    final SocketClient socketClient= new SocketClient();
    byte elemento;
    StringBuilder textoPantalla= new StringBuilder();
    byte []buffer;
    int i=0;
    private String textoLCD;
    private String modo;
    private int ColCursor, RawCursor;
    private StimMode stimMode;
    private Values values;
    private TSnackbar TSBFail;
    private TSnackbar TSBConnect;
    private String serverAddress;
    private int serverPort;
    private Handler mhandlerConn;
    private Handler mhandlerSendData;
    private boolean secondActivity;
    Runnable mRunnableFailed= new Runnable() {
        @Override
        public void run() {
            TSBFail.show();
        }
    };
    Runnable mRunnableAuto= new Runnable() {
        @Override
        public void run() {
            TSBConnect.show();
            ConnectServer();
        }
    };
    Runnable mRunnableConn= new Runnable() {
        @Override
        public void run() {
            TSBConnect.dismiss();TSBFail.dismiss();
            sendData(Constantes.UPDATETEXT,MainActivity.context);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=getApplicationContext();
        Snackbar();
        getSharedPreferences();
        eSelectors= new ArrayList<>();
        initializeUI();
        modoEstimulacion();
        initializeExpandableSelector();
        textoPantalla= new StringBuilder();
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*String texto = "Cont.  4  4  120              66";
                modo = texto.substring(0, 5).replaceAll("\\s+", "");
                if (Values.ArrayModos.contains(modo)) {
                    values.setValues(texto, modo);
                }*/
                sendData(Constantes.STARTSTOP, getApplicationContext());
            }
        });
        mhandlerConn= new Handler();
        mhandlerSendData= new Handler();
        TSBConnect.show();
        ConnectServer();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUI();
    }

    private void initializeExpandableSelector() {
        eSelectors.add((ExpandableSelector) findViewById(R.id.es_carrier));
        Campo campCarrier=new Campo(eSelectors.get(0),getString(R.string.khz), new int[]{1, 4},eSelectors,socketClient);

        eSelectors.add((ExpandableSelector) findViewById(R.id.es_duartion_burst));
        Campo campDurationBurst=new Campo(eSelectors.get(1),getString(R.string.ms), new int[]{2, 4},eSelectors,socketClient);

        eSelectors.add((ExpandableSelector) findViewById(R.id.es_freq_burst));
        Campo campFreqBurst = new Campo(eSelectors.get(2), getString(R.string.hz), 1,120,eSelectors,socketClient);

        eSelectors.add((ExpandableSelector) findViewById(R.id.es_tAscenso));
        Campo campRise = new Campo(eSelectors.get(3), getString(R.string.s), 1,20,eSelectors,socketClient);

        eSelectors.add((ExpandableSelector) findViewById(R.id.es_tEncendido));
        Campo campOn = new Campo(eSelectors.get(4), getString(R.string.s), 1,60,eSelectors,socketClient);

        eSelectors.add((ExpandableSelector) findViewById(R.id.es_tBajada));
        Campo campDecay = new Campo(eSelectors.get(5), getString(R.string.s), 1,20,eSelectors,socketClient);

        eSelectors.add((ExpandableSelector) findViewById(R.id.es_tReposo));
        Campo campOff = new Campo(eSelectors.get(6), getString(R.string.s), 1,60,eSelectors,socketClient);

        eSelectors.add((ExpandableSelector) findViewById(R.id.es_tAplicacion));
        Campo campAplication=new Campo(eSelectors.get(7),getString(R.string.m), 1,60,eSelectors,socketClient);
        /*eSelectors.add((ExpandableSelector) findViewById(R.id.es_modoEstim));
        Campo campSimmMode = new Campo(eSelectors.get(8),getResources().getStringArray(R.array.stimm_mode),eSelectors);*/
        values= new Values(stimMode,campCarrier,campDurationBurst,campFreqBurst,campRise,campOn,campDecay,campOff,campAplication,socketClient,getBaseContext());
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

    public void Snackbar(){
        TSBFail= TSnackbar
                .make(findViewById(android.R.id.content), "No se pudo establecer la conexión con el servidor", TSnackbar.LENGTH_INDEFINITE)
                .setAction("Reintenta", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ConnectServer();
                    }
                });
        TSBFail.setActionTextColor(Color.WHITE);
        TSBConnect=TSnackbar.make(findViewById(android.R.id.content), "Connectando...", TSnackbar.LENGTH_INDEFINITE);
        //snackbar.addIcon(R.mipmap.ic_core, 200);
        View snackbarView = TSBFail.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        View snackbarView2 = TSBConnect.getView();
        snackbarView2.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        TextView textView2 = (TextView) snackbarView2.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView2.setTextColor(Color.WHITE);
    }

    private void getSharedPreferences(){
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        serverAddress = SP.getString("direccion_ip", "192.168.1.1");
        serverPort = Integer.parseInt(SP.getString("puerto", "9999"));
        SocketClient.setServerArgs(serverAddress,serverPort);
    }
    private void ConnectServer(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socketClient.getSocket();
                    mhandlerConn.post(mRunnableConn);
                } catch (ServerNotFound serverNotFound) {
                    serverNotFound.printStackTrace();
                    mhandlerConn.post(mRunnableFailed);
                }

            }
        }).start();
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
        secondActivity=false;
        SocketClient.socketListener.registerCallback(this);
        //if(!SocketClient.isConnected()){

          //  updateUI();
        //}

    }

    @Override
    protected void onPause() {
        super.onPause();
        //eventosListener.removeEventListener(Event.MSGRCV);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(!SocketClient.isConnected()){
            updateUI();
        }

    }

    public void parseMessage(){
        buffer=SocketClient.BUFFER;
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
            secondActivity=true;
            startActivity(new Intent(getApplicationContext(), Channels.class));
        }else {
            modo=textoLCD.substring(0,5).replaceAll("\\s+","").toLowerCase();
            if(Values.ArrayModos.contains(modo)){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        values.setValues(textoLCD, modo,RawCursor,ColCursor);
                    }
                });
            }
            else{

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(SocketClient.isConnected()){
            socketClient.Desconectar();
        }
    }


    public void updateUI(){
        //if(SocketClient.isConnected())
            //sendData();
    }


    @Override
    public void OnNewMessage() {
            parseMessage();
    }

    @Override
    public void OnDisconnectedSocket() {
        mhandlerConn.post(mRunnableAuto);
    }

    @Override
    public void OnTimeOut() {

    }



    public static void sendData(byte[] pack,Context context){
        try {
            SocketClient.Send_Socket_TLS(pack, pack.length);
        } catch (SocketClosed socketClosed) {
            Toast.makeText(context, "Socket no iniciado", Toast.LENGTH_SHORT).show();
        }
    }
}
