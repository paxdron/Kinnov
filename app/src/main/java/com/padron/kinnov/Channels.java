package com.padron.kinnov;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.padron.kinnov.Conexion.Socket_TLS;

import java.util.zip.Inflater;

import mehdi.sakout.fancybuttons.FancyButton;

public class Channels extends AppCompatActivity {

    FancyButton stop;
    private  Canal C1,C2,C3,C4;
    public ClaseEventos eventosListener;
    Socket_TLS socket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channels);
        socket=new Socket_TLS();
        stop=(FancyButton)findViewById(R.id.fabStop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[]pack=Socket_TLS.pack((byte)17);
                socket.Send_Socket_TLS(pack,pack.length);
                Channels.this.finish();

            }
        });

        C1= new Canal(0,120,
                (FancyButton)findViewById(R.id.btn_upCh1),
                (FancyButton)findViewById(R.id.btn_downCh1),
                (TextView)findViewById(R.id.tvCanal1Value));
        C2= new Canal(0,120,
                (FancyButton)findViewById(R.id.btn_upCh2),
                (FancyButton)findViewById(R.id.btn_downCh2),
                (TextView)findViewById(R.id.tvCanal2Value));
        C3= new Canal(0,130,
                (FancyButton)findViewById(R.id.btn_upCh3),
                (FancyButton)findViewById(R.id.btn_downCh3),
                (TextView)findViewById(R.id.tvCanal3Value));
        C4= new Canal(0,140,
                (FancyButton)findViewById(R.id.btn_upCh4),
                (FancyButton)findViewById(R.id.btn_downCh4),
                (TextView)findViewById(R.id.tvCanal4Value));
        Canal[] canales={C1,C2,C3,C4};

        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf");
        for (Canal Chanel:canales
             ) {
            Chanel.tvValue.setTypeface(custom_font);
        }

        ((TextView)findViewById(R.id.tvTituloC1)).setTypeface(custom_font);
        ((TextView)findViewById(R.id.tvTituloC2)).setTypeface(custom_font);
        ((TextView)findViewById(R.id.tvTituloC3)).setTypeface(custom_font);
        ((TextView)findViewById(R.id.tvTituloC4)).setTypeface(custom_font);
        stop.getTextViewObject().setTypeface(custom_font);
    }
    
    
    
    
    class Canal{
        int MaxVal;
        int MinVal;
        int CurrentValue;
        FancyButton Incrementar, Decrementar;
        TextView tvValue;
        
        public Canal(int minVal, int maxVal, FancyButton incrementar, FancyButton decrementar, TextView tvValue) {
            MinVal = minVal;
            MaxVal = maxVal;
            CurrentValue=0;
            Incrementar=incrementar;
            Decrementar=decrementar;
            this.tvValue=tvValue;
            setIncrementar();
            setDecrementar();
        }
        
        void Incrementar(){
            if(CurrentValue<MaxVal)
                CurrentValue++;
            tvValue.setText(Integer.toString(CurrentValue)+" mAh");
        }
        void Decrementar(){
            if(CurrentValue>MinVal)
                CurrentValue--;
            tvValue.setText(Integer.toString(CurrentValue)+" mAh");
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
    }
}
