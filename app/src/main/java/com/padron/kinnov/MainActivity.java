package com.padron.kinnov;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        eSelectors= new ArrayList<>();
        initializeExpandableSelector();
        start=(FancyButton)findViewById(R.id.fabStart);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Channels.class));
            }
        });

    }

    private void initializeExpandableSelector() {
        eSelectors.add((ExpandableSelector) findViewById(R.id.es_carrier));
        Campo campCarrier=new Campo(eSelectors.get(0),getString(R.string.khz), new int[]{1, 4},eSelectors);

        eSelectors.add((ExpandableSelector) findViewById(R.id.es_duartion_burst));
        Campo campDurationBurst=new Campo(eSelectors.get(1),getString(R.string.ms), new int[]{2, 4},eSelectors);

        eSelectors.add((ExpandableSelector) findViewById(R.id.es_freq_burst));
        Campo campFreqBurst=new Campo(eSelectors.get(2),getString(R.string.hz), 1,120,eSelectors);

        eSelectors.add((ExpandableSelector) findViewById(R.id.es_tAscenso));
        Campo campRise=new Campo(eSelectors.get(3),getString(R.string.s), 1,20,eSelectors);

        eSelectors.add((ExpandableSelector) findViewById(R.id.es_tEncendido));
        Campo campOn=new Campo(eSelectors.get(4),getString(R.string.s), 1,60,eSelectors);

        eSelectors.add((ExpandableSelector) findViewById(R.id.es_tBajada));
        Campo campDecay=new Campo(eSelectors.get(5),getString(R.string.s), 1,20,eSelectors);

        eSelectors.add((ExpandableSelector) findViewById(R.id.es_tReposo));
        Campo campOff=new Campo(eSelectors.get(6),getString(R.string.s), 1,60,eSelectors);

        eSelectors.add((ExpandableSelector) findViewById(R.id.es_tAplicacion));
        Campo campAplication=new Campo(eSelectors.get(7),getString(R.string.m), 1,60,eSelectors);
        eSelectors.add((ExpandableSelector)findViewById(R.id.es_modoEstim));
        Campo campSimmMode = new Campo(eSelectors.get(8),getResources().getStringArray(R.array.stimm_mode),eSelectors);


    }





}
