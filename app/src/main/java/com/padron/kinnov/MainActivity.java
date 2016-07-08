package com.padron.kinnov;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidadvance.topsnackbar.TSnackbar;
import com.karumi.expandableselector.ExpandableSelector;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.padron.kinnov.Conexion.SocketClient;
import com.padron.kinnov.events.ISocketListener;
import com.padron.kinnov.exceptions.ServerNotFound;
import com.padron.kinnov.exceptions.SocketClosed;

import mehdi.sakout.fancybuttons.FancyButton;

public class MainActivity extends AppCompatActivity implements ISocketListener {
    private List<ExpandableSelector> eSelectors;
    private FancyButton start;
    public static Typeface custom_font;
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
    private TSNCKBR SnackBar;
    private String serverAddress;
    private int serverPort;
    private Handler mhandlerConn;
    private Handler mhandlerSendData;
    private boolean secondActivity;
    Runnable mRunnableFailed= new Runnable() {
        @Override
        public void run() {
            SnackBar.show();
            SnackBar.Retry();
        }
    };
    Runnable mRunnableAuto= new Runnable() {
        @Override
        public void run() {
            SnackBar.show();
            SnackBar.Conectando();
            ConnectServer();
        }
    };
    Runnable mRunnableConn= new Runnable() {
        @Override
        public void run() {
            SnackBar.dismiss();
            sendData(Constantes.UPDATETEXT,MainActivity.context);
        }
    };
    private byte[] pack;
    /*private Handler handlerMensajes;
    Runnable mRunColaMensajes= new Runnable() {
        @Override
        public void run() {
            if(!Constantes.colaMensajes.vacia()){
                pack=(byte[])Constantes.colaMensajes.frente();
                Constantes.colaMensajes.desencolar();
                try {
                    SocketClient.Send_Socket_TLS(pack, pack.length);
                } catch (SocketClosed socketClosed) {
                    Toast.makeText(getApplicationContext(),"Socket no inicado",Toast.LENGTH_SHORT).show();
                }
            }
            handlerMensajes.post(mRunColaMensajes);
        }
    };*/
    private FancyButton progmenu;
    private AlertDialog alertdialog;
    private AlertDialog.Builder builder;
    private boolean isChIdiomaOpen=false;
    private int idiomaSelect;
    private ProgressDialog pdEspera;
    private Configuration config;
    private List<String> PrtclsbyLng;
    private int idiomaAnterior;
    private boolean chLanOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=getApplicationContext();
        config = new Configuration();
        SnackBar=new TSNCKBR((TextView) findViewById(R.id.message),(FancyButton) findViewById(R.id.retry),(LinearLayout)findViewById(R.id.Tsnackbar));
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

        progmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData(Constantes.PROGMENU, getApplicationContext());
            }
        });
        mhandlerConn= new Handler();
        mhandlerSendData= new Handler();
        SnackBar.show();
        SnackBar.Conectando();
        ConnectServer();
        //handlerMensajes= new Handler();
        //handlerMensajes.post(mRunColaMensajes);
    }

    private boolean updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();
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
/*
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
    }*/

    private void getSharedPreferences(){
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        serverAddress = SP.getString("direccion_ip", "192.168.1.1");
        serverPort = Integer.parseInt(SP.getString("puerto", "5001"));
        SocketClient.setServerArgs(serverAddress, serverPort);
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
        progmenu = (FancyButton) findViewById(R.id.fabProgMenu);
        start.getTextViewObject().setTypeface(custom_font);
        progmenu.getTextViewObject().setTypeface(custom_font);
        Toolbar toolbar =(Toolbar)findViewById(R.id.myToolbar);
        toolbar.setTitle("");
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setTypeface(custom_font);
        setSupportActionBar(toolbar);
        setIdiomAlertDialog();
        setProgressDialog();
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
            case R.id.ch_idiom:
                pdEspera.show();
                sendData(Constantes.CH_IDIOM,this);
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
        if(SocketClient.isConnected())
            sendData(Constantes.UPDATETEXT,MainActivity.context);
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
            ConnectServer();
        }

    }

    public void setProgressDialog(){
        pdEspera = new ProgressDialog(this);
        pdEspera.setMessage(getString(R.string.wait));
        pdEspera.setCanceledOnTouchOutside(false);
    }

    public void parseMessage(){
        buffer=SocketClient.BUFFER;
        i=1;
        textoPantalla.setLength(0);
        idiomaAnterior=Constantes.IDIOMA;
        Constantes.IDIOMA=buffer[Constantes.POSIDIOMA];
        updateResources(MainActivity.this,Constantes.IDIOMASLOCALE[Constantes.IDIOMA-1]);
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
        alertdialog.dismiss();
        isChIdiomaOpen=false;
        if(textoLCD.substring(0, 2).equals("1:")){
            secondActivity=true;
            startActivity(new Intent(getApplicationContext(), Channels.class));
            Constantes.IsManual=true;
        }else {
            modo=textoLCD.substring(0,5).replaceAll("\\s+","").toLowerCase();
            System.out.println(modo);
            if(Values.ArrayModos.contains(modo)){
                if(idiomaAnterior!=Constantes.IDIOMA||chLanOpen) {
                    chLanOpen=false;
                    restartApp();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pdEspera.dismiss();
                        values.setValues(textoLCD, modo,RawCursor,ColCursor);
                        Constantes.IsManual=true;
                    }
                });
            }
            else{
                PrtclsbyLng=(Constantes.IDIOMA==1)?Constantes.PROTOCOLS:(Constantes.IDIOMA==2)?Constantes.PROTOCOLSEN:Constantes.PROTOCOLSPT;
                if((PrtclsbyLng.contains(modo))){
                    startActivity(new Intent(getApplicationContext(), ProgMenu.class));
                    Constantes.IsManual=true;
                }
                else {
                    String strMenu=textoLCD.substring(0,5).replaceAll("\\s+","").toLowerCase();
                    if(strMenu.equals(Constantes.STRMENU)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                idiomaSelect= Constantes.IDIOMAS.indexOf(textoLCD.substring(16,32).replaceAll("\\s+","").toLowerCase());
                                setIdiomAlertDialog();
                                if(!isChIdiomaOpen) {
                                    isChIdiomaOpen=true;
                                    if(idiomaSelect>=0) {
                                        pdEspera.dismiss();
                                        alertdialog.show();
                                        chLanOpen=true;
                                    }
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    public void restartApp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recreate();
            }
        });
    }

    public void setIdiomAlertDialog(){
        final CharSequence[] idiomas=  getResources().getTextArray(R.array.idiomas);
        builder = new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.ch_idiom))
                .setSingleChoiceItems(idiomas, idiomaSelect, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(Constantes.IDIOMA){
                            case 1:
                                if(which==1){
                                    sendData(Constantes.UP_PULSE,getApplication());
                                }else{
                                    if(which==2)
                                        sendData(Constantes.DOWNPULSE,getApplication());
                                }
                                break;
                            case 2:
                                if(which==2){
                                    sendData(Constantes.UP_PULSE,getApplication());
                                }else{
                                    if(which==0)
                                        sendData(Constantes.DOWNPULSE,getApplication());
                                }
                                break;
                            case 3:
                                if(which==0){
                                    sendData(Constantes.UP_PULSE,getApplication());
                                }else{
                                    if(which==1)
                                        sendData(Constantes.DOWNPULSE,getApplication());
                                }
                                break;
                        }
                    }
                }).setCancelable(false).setPositiveButton(getString(R.string.select), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pdEspera.show();
                        sendData(Constantes.PROGMENU, getApplicationContext());
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.selectedLan) + idiomas[idiomaSelect], Toast.LENGTH_LONG).show();
                        alertdialog.dismiss();
                    }
                });
        alertdialog = builder.create();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(SocketClient.isConnected()){
            socketClient.Desconectar();
        }
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
            //Constantes.IsManual=false;
            //Constantes.colaMensajes.encolar(pack);
        try {
            SocketClient.Send_Socket_TLS(pack, pack.length);
        } catch (SocketClosed socketClosed) {
            socketClosed.printStackTrace();
        }
    }

    public class TSNCKBR{
        private TextView message;
        private FancyButton retry;
        private LinearLayout llSB;
        public TSNCKBR(TextView message, FancyButton retry, LinearLayout llSB) {
            this.message = message;
            this.retry = retry;
            this.llSB=llSB;
            message.setTextColor(Color.WHITE);
            retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConnectServer();
                }
            });
        }
        public void show(){
            llSB.setVisibility(View.VISIBLE);
        }
        public void dismiss(){
            llSB.setVisibility(View.GONE);
        }
        public void Retry(){
            retry.setVisibility(View.VISIBLE);
            message.setText(getString(R.string.noConn));
            ConnectServer();
        }

        public void Conectando(){
            message.setText(getString(R.string.conectando));
            retry.setVisibility(View.GONE);
        }
    }
}
