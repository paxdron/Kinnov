package com.padron.kinnov;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import com.padron.kinnov.Conexion.SocketClient;
import com.padron.kinnov.events.ISocketListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

public class MenuMaquina extends AppCompatActivity  implements ISocketListener{
    private FancyButton btnAccept;
    private TextView tvWea;
    byte elemento;
    private StringBuilder textoPantalla= new StringBuilder();
    byte []buffer;
    int i=0;
    private String textoLCD;
    private String modo;
    private Handler mhandHandler,mHandPulse;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    private String[] PROTOCOLOS;
    private int indice,pasos,times;
    private boolean isMoving=false;
    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            tvWea.setText(textoLCD);
            if(!isMoving) {
                expListView.expandGroup(indice - 1);
                collapseOthers(indice - 1);
            }
        }
    };
    private int current;
    Runnable mRunCol = new Runnable() {
        @Override
        public void run() {
            expListView.collapseGroup(current);
        }
    };
    Runnable mRunUP= new Runnable() {
        @Override
        public void run() {
            if(times!=0)
            {
                times--;
                MainActivity.sendData(Constantes.UP_PULSE, getApplicationContext());
                mHandPulse.postDelayed(mRunUP, Constantes.DELAY+50);
            }
            else
                isMoving=false;
        }
    };
    Runnable mRunDown= new Runnable() {
        @Override
        public void run() {
            if(times!=0)
            {
                times--;
                MainActivity.sendData(Constantes.DOWNPULSE,getApplicationContext());
                mHandPulse.postDelayed(mRunDown, Constantes.DELAY+50);
            }
            else
                isMoving=false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        PROTOCOLOS        = getResources().getStringArray(R.array.titleProtocolos);
        btnAccept= (FancyButton)findViewById(R.id.btnRegresar);
        tvWea=(TextView)findViewById(R.id.tvwea);
        SocketClient.socketListener.registerCallback(this);
        MainActivity.sendData(Constantes.UPDATETEXT, this);
        mhandHandler= new Handler();
        mHandPulse= new Handler();

        expListView = (ExpandableListView) findViewById(R.id.lvProtocols);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader,listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);


        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.sendData(Constantes.PROGMENU, getBaseContext());
            }
        });
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                mHandPulse.post(mRunCol);
                current=groupPosition;
                setMode(indice,current+1);
                return false;
            }
        });

    }

    private void prepareListData() {
        listDataHeader = Arrays.asList(PROTOCOLOS);
        listDataChild = new HashMap<String, List<String>>();
        String[] infoProtocolos= getResources().getStringArray(R.array.infoProtocolos);

        for (int j=0;j<infoProtocolos.length;j++){
            listDataChild.put(listDataHeader.get(j),Arrays.asList(infoProtocolos[j]));
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
        if((Constantes.PROTOCOLS.contains(modo))){
            switch(modo){
                case "modd":
                    if(textoLCD.charAt(16)=='d')
                        indice=1;
                    else
                    if(textoLCD.charAt(16)=='m')
                        indice=2;
                    break;
                case "reduc":
                    indice=3;
                    break;
                case "fesa":
                    indice=4;
                    break;
                case  "fort.":
                    if(textoLCD.charAt(6)=='d')
                        indice=5;
                    else
                        if(textoLCD.charAt(6)=='m')
                            indice=7;
                    break;
                case "reedu":
                    indice=6;
                    break;
            }
            current=indice-1;

            mhandHandler.post(mRunnable);
        }else {
            MenuMaquina.this.finish();
        }
    }

    @Override
    public void OnDisconnectedSocket() {

    }

    @Override
    public void OnTimeOut() {

    }

    public void collapseOthers(int index){
        for (int j=0;j<Constantes.NUMPROTOCOLS;j++){
            if(j!=index)
                expListView.collapseGroup(j);
        }

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
                    mHandPulse.postDelayed(mRunDown, Constantes.DELAY+50);
                }
                else
                    mHandPulse.postDelayed(mRunUP, Constantes.DELAY+50);
            }
            else{
                if(pasos>0) {
                    pasos=Constantes.NUMPROTOCOLS-pasos;
                    times=pasos;
                    mHandPulse.postDelayed(mRunUP, Constantes.DELAY+50);
                }
                else{
                    pasos=Constantes.NUMPROTOCOLS+pasos;
                    times=pasos;
                    mHandPulse.postDelayed(mRunDown, Constantes.DELAY+50);
                }
            }

        }
    }

}
