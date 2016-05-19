package com.padron.kinnov;

import android.content.Context;
import android.view.View;

import com.karumi.expandableselector.ExpandableItem;
import com.karumi.expandableselector.ExpandableSelector;
import com.karumi.expandableselector.ExpandableSelectorListener;
import com.karumi.expandableselector.OnExpandableItemClickListener;
import com.padron.kinnov.Conexion.Socket_TLS;
import com.padron.kinnov.events.CollapseClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by antonio on 19/04/16.
 */
public class Campo {
    CollapseClass collapseClass= CollapseClass.getInstance();
    public static final int BISELECECCION=1;
    public static final int RANGO=2;
    String Unidad;
    List<ExpandableItem> expandableItems;
    List<ExpandableSelector> otros;
    ExpandableSelector eSelector;
    boolean expandido;
    int CurrentValue;
    int tipo;
    int[] values;
    private int identificador;
    ExpandableItem chageItem;

    Socket_TLS socket;
    int Tipo;
    public Campo(ExpandableSelector eSelector, String unidad, int[] values, List<ExpandableSelector> lista, Socket_TLS socket) {
        this.eSelector=eSelector;
        otros=lista;
        setValues(unidad,values);
        expandido=false;
        this.socket=socket;
        tipo=BISELECECCION;
    }

    public Campo(ExpandableSelector eSelector, String unidad, int Min, int Max, List<ExpandableSelector> lista, Socket_TLS socket) {
        this.eSelector=eSelector;
        otros=lista;
        Unidad=unidad;
        CurrentValue=0;
        eSelector.showExpandableItems(getExpandableItems());
        setListener();
        this.socket=socket;
        tipo=RANGO;
    }

    /*
    public Campo(ExpandableSelector eSelector, String[] values, List<ExpandableSelector> lista, Socket_TLS socket) {
        this.eSelector=eSelector;
        otros=lista;
        expandableItems= new ArrayList<>();
        ExpandableItem eI;
        for (String mode:values
             ) {
            eI=new ExpandableItem(mode);
            expandableItems.add(eI);
        }
        eSelector.showExpandableItems(expandableItems);
        setListener1();
        this.socket=socket;
    }*/

    void setValues (String unidad,int[]values){
        this.values=values;
        CurrentValue=values[0];
        this.Unidad=unidad;
        eSelector.showExpandableItems(getExpandableItems(values));
        setListener1();
    }
    List<ExpandableItem>  getExpandableItems(int[] values){
        expandableItems= new ArrayList<>();
        for (int i:values
                ) {
            expandableItems.add(new ExpandableItem(Integer.toString(i)+" "+ Unidad));
        }

        return expandableItems;
    }


    void setListener() {
        eSelector.setOnExpandableItemClickListener(new OnExpandableItemClickListener() {
            @Override
            public void onExpandableItemClickListener(int index, View view) {
                switch (index) {
                    case 0:
                        //TODO Enviar mensaje flecha menos
                        break;
                    case 1:
                        eSelector.collapse();
                        break;
                    case 2:
                        //TODO Enviar mensaje flecha más
                        break;
                    default:
                }
            }
        });

        eSelector.setExpandableSelectorListener(new ExpandableSelectorListener() {
            @Override
            public void onCollapse() {
                //Do something here
                if (expandido) {
                    ExpandableItem segundo = eSelector.getExpandableItem(1);
                    eSelector.updateExpandableItem(1, eSelector.getExpandableItem(0));
                    eSelector.updateExpandableItem(0, segundo);
                    expandido = false;
                }
            }

            @Override
            public void onExpand() {
                //Do something here
                if (!expandido) {
                    Values.itemSelected=identificador;
                    collapseClass.notifica();
                    ExpandableItem segundo = eSelector.getExpandableItem(1);
                    eSelector.updateExpandableItem(1, eSelector.getExpandableItem(0));
                    eSelector.updateExpandableItem(0, segundo);
                    collapseOthers(eSelector);
                    expandido = true;
                }
            }

            @Override
            public void onCollapsed() {
                //Do something here
            }

            @Override
            public void onExpanded() {
                //Do something here
            }

        });
    }

    private void swipeFirstItem(int position, ExpandableItem clickedItem) {
        ExpandableItem firstItem = eSelector.getExpandableItem(0);
        eSelector.updateExpandableItem(0, clickedItem);
        eSelector.updateExpandableItem(position, firstItem);
    }
    void setListener1(){
        eSelector.setOnExpandableItemClickListener(new OnExpandableItemClickListener() {
            @Override public void onExpandableItemClickListener(int index, View view) {
                switch (index) {
                    case 1:
                        ExpandableItem firstItem = eSelector.getExpandableItem(1);
                        swipeFirstItem(1, firstItem);
                        break;
                    case 2:
                        ExpandableItem secondItem = eSelector.getExpandableItem(2);
                        swipeFirstItem(2, secondItem);
                        break;

                    default:
                }
                eSelector.collapse();
            }


        });


        eSelector.setExpandableSelectorListener(new ExpandableSelectorListener() {
            @Override
            public void onCollapse() {
                //Do something here
            }

            @Override
            public void onExpand() {
                Values.itemSelected=identificador;
                collapseClass.notifica();
                //Do something here
            }

            @Override
            public void onCollapsed() {
                //Do something here
            }

            @Override
            public void onExpanded() {
                //Do something here
            }

        });
    }

    public void setCurrentValue(int newValue){
        CurrentValue=newValue;
        eSelector.updateExpandableItem(0, new ExpandableItem(Integer.toString(CurrentValue) + " " + Unidad));
    }
    public void setCurrentValue1(int newValue){
        if(newValue!=CurrentValue){
                CurrentValue=newValue;
                chageItem= eSelector.getExpandableItem(1);
                swipeFirstItem(1, chageItem);
        }
    }

    public void collapseOthers(ExpandableSelector eSeleccionado){
        for (ExpandableSelector eS:otros
                ) {
            if(eS!=eSeleccionado)
                eS.collapse();
        }
    }

    public List<ExpandableItem> getExpandableItems() {
        expandableItems = new ArrayList<>();
        expandableItems.add(new ExpandableItem(Integer.toString(CurrentValue)+" "+Unidad));
        expandableItems.add(new ExpandableItem("-"));
        expandableItems.add(new ExpandableItem("+"));
        return expandableItems;
    }

    public int getTipo() {
        return Tipo;
    }

    public int getIdentificador() {
        return identificador;
    }

    public void setIdentificador(int identificador) {
        this.identificador = identificador;
    }
}


