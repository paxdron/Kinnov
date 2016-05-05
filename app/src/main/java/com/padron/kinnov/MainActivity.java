package com.padron.kinnov;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.expandableselector.ExpandableItem;
import com.karumi.expandableselector.ExpandableSelector;
import com.karumi.expandableselector.OnExpandableItemClickListener;

import java.util.ArrayList;
import java.util.List;
import com.padron.kinnov.Campo.*;

import mehdi.sakout.fancybuttons.FancyButton;

public class MainActivity extends AppCompatActivity {
    private List<ExpandableSelector> eSelectors;
    private List<ExpandableItem> expandableItems;
    private int valuefreqBusrt=1;
    private FancyButton start;
    private Typeface custom_font;

    private FancyButton modo1,modo2,modo3;
    private boolean expanded;
    private String[] modosLabels;
    private FancyButton[] modosBtns;
    private int modSelected;
    private LinearLayout layoutcarrier,layoutduartion_burst, layoutfreq_burst, layouttAscenso, layouttEncendido, layouttBajada, layouttReposo, layouttAplicacion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        eSelectors= new ArrayList<>();
        initializeExpandableSelector();
        start = (FancyButton) findViewById(R.id.fabStart);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Channels.class));
            }
        });
        initializeUI();

        modoEstimulacion();

    }

    private void initializeExpandableSelector() {
        eSelectors.add((ExpandableSelector) findViewById(R.id.es_carrier));
        Campo campCarrier=new Campo(eSelectors.get(0),getString(R.string.khz), new int[]{1, 4},eSelectors);

        eSelectors.add((ExpandableSelector) findViewById(R.id.es_duartion_burst));
        Campo campDurationBurst=new Campo(eSelectors.get(1),getString(R.string.ms), new int[]{2, 4},eSelectors);

        eSelectors.add((ExpandableSelector) findViewById(R.id.es_freq_burst));
        Campo campFreqBurst = new Campo(eSelectors.get(2), getString(R.string.hz), 1,120,eSelectors);

        eSelectors.add((ExpandableSelector) findViewById(R.id.es_tAscenso));
        Campo campRise = new Campo(eSelectors.get(3), getString(R.string.s), 1,20,eSelectors);

        eSelectors.add((ExpandableSelector) findViewById(R.id.es_tEncendido));
        Campo campOn = new Campo(eSelectors.get(4), getString(R.string.s), 1,60,eSelectors);

        eSelectors.add((ExpandableSelector) findViewById(R.id.es_tBajada));
        Campo campDecay = new Campo(eSelectors.get(5), getString(R.string.s), 1,20,eSelectors);

        eSelectors.add((ExpandableSelector) findViewById(R.id.es_tReposo));
        Campo campOff = new Campo(eSelectors.get(6), getString(R.string.s), 1,60,eSelectors);

        eSelectors.add((ExpandableSelector) findViewById(R.id.es_tAplicacion));
        Campo campAplication=new Campo(eSelectors.get(7),getString(R.string.m), 1,60,eSelectors);
        /*eSelectors.add((ExpandableSelector) findViewById(R.id.es_modoEstim));
        Campo campSimmMode = new Campo(eSelectors.get(8),getResources().getStringArray(R.array.stimm_mode),eSelectors);*/

    }


    public void modoEstimulacion(){
        expanded=false;
        TextView tx = (TextView)findViewById(R.id.tituloModo);
        modo1=(FancyButton)findViewById(R.id.modo1);
        modo2=(FancyButton)findViewById(R.id.modo2);
        modo3=(FancyButton)findViewById(R.id.modo3);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf");
        modosBtns = new FancyButton[]{modo1, modo2, modo3};
        modosLabels=getResources().getStringArray(R.array.stimm_mode);
        tx.setTypeface(custom_font);

       /* for (FancyButton fB:modosBtns
                ) {
            fB.getTextViewObject().setTypeface(custom_font);
        }*/
        modo1.setVisibility(View.GONE);
        modo3.setVisibility(View.GONE);

        modo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMode(0);
            }
        });
        modo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!expanded) {
                    setTexts();
                    modo1.setVisibility(View.VISIBLE);
                    modo3.setVisibility(View.VISIBLE);
                    expanded = true;
                } else {
                    selectMode(1);
                }
            }
        });
        modo3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMode(2);
            }
        });
    }

    public void setTexts(){
        for (int i=0;i<3;i++) {
            modosBtns[i].setText(modosLabels[i]);
        }
    }

    private void selectMode(int indice){
        modSelected=indice;
        switch(modSelected){
            case 0:
                hideLayouts();
                break;
            default:
                showAllLayouts();
                break;
        }

        Collapse();
    }

    private void Collapse(){
        modo2.setText(modosLabels[modSelected]);
        AlphaAnimation fade_out = new AlphaAnimation(1.0f, 0.0f);
        fade_out.setDuration(200);
        fade_out.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation arg0) {
            }

            public void onAnimationRepeat(Animation arg0) {
            }

            public void onAnimationEnd(Animation arg0) {
                modo1.setVisibility(View.GONE);
                modo3.setVisibility(View.GONE);
                modo2.setText(modosLabels[modSelected]);
            }
        });
        modo3.startAnimation(fade_out);
        modo1.startAnimation(fade_out);
        expanded=false;
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
        layoutcarrier=(LinearLayout)findViewById(R.id.layoutcarrier);
        layoutduartion_burst=(LinearLayout)findViewById(R.id.layoutduartion_burst);
        layoutfreq_burst=(LinearLayout)findViewById(R.id.layoutfreq_burst);
        layouttAscenso=(LinearLayout)findViewById(R.id.layouttAscenso);
        layouttEncendido=(LinearLayout)findViewById(R.id.layouttEncendido);
        layouttBajada=(LinearLayout)findViewById(R.id.layouttBajada);
        layouttReposo=(LinearLayout)findViewById(R.id.layouttReposo);
        layouttAplicacion=(LinearLayout)findViewById(R.id.layouttAplicacion);
        start.getTextViewObject().setTypeface(custom_font);
        Toolbar toolbar =(Toolbar)findViewById(R.id.myToolbar);
        toolbar.setTitle("");
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setTypeface(custom_font);
        setSupportActionBar(toolbar);
    }

    private void showAllLayouts(){
        layoutcarrier.setVisibility(View.VISIBLE);
        layoutduartion_burst.setVisibility(View.VISIBLE);
        layoutfreq_burst.setVisibility(View.VISIBLE);
        layouttAscenso.setVisibility(View.VISIBLE);
        layouttEncendido.setVisibility(View.VISIBLE);
        layouttBajada.setVisibility(View.VISIBLE);
        layouttReposo.setVisibility(View.VISIBLE);
        layouttAplicacion.setVisibility(View.VISIBLE);
    }

    private void hideLayouts(){
        layouttAscenso.setVisibility(View.GONE);
        layouttEncendido.setVisibility(View.GONE);
        layouttBajada.setVisibility(View.GONE);
        layouttReposo.setVisibility(View.GONE);
    }


    public void menu(View v){
        startActivity(new Intent(this,Menu.class));
    }





}
