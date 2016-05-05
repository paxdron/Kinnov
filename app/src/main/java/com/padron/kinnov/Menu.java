package com.padron.kinnov;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Menu extends AppCompatActivity  {

    private RecyclerView recycler;
    private LinearLayoutManager lManager;
    private ProtocoloAdaptador adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        recycler = (RecyclerView) findViewById(R.id.reciclador);
        recycler.setHasFixedSize(true);
        lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);


        List items = new ArrayList();
        String[] titulos=getResources().getStringArray(R.array.titleProtocolos);
        String[] descripcion=getResources().getStringArray(R.array.infoProtocolos);

        items.add(new Protocolo(titulos[0],descripcion[0]));
        items.add(new Protocolo(titulos[1],descripcion[1]));
        items.add(new Protocolo(titulos[2],descripcion[2]));
        items.add(new Protocolo(titulos[3],descripcion[3]));
        items.add(new Protocolo(titulos[4],descripcion[4]));
        items.add(new Protocolo(titulos[5],descripcion[5]));
        items.add(new Protocolo(titulos[6],descripcion[6]));
        adapter = new ProtocoloAdaptador(items);
        recycler.setAdapter(adapter);

    }
}
