package com.padron.kinnov;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.karumi.expandableselector.ExpandableItem;
import com.karumi.expandableselector.ExpandableSelector;
import com.karumi.expandableselector.ExpandableSelectorListener;
import com.karumi.expandableselector.OnExpandableItemClickListener;
import com.padron.kinnov.Conexion.SocketClient;
import com.padron.kinnov.events.CollapseClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by antonio on 19/04/16.
 */
public class Campo {
    private GestureDetector gestureDetector;
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
    private int btn=1;
    SocketClient socket;
    int Tipo;
    private boolean LongTap=false;

    public Campo(ExpandableSelector eSelector, String unidad, int[] values, List<ExpandableSelector> lista, SocketClient socket) {
        this.eSelector=eSelector;
        otros=lista;
        setValues(unidad,values);
        expandido=false;
        this.socket=socket;
        tipo=BISELECECCION;
    }

    public Campo(ExpandableSelector eSelector, String unidad, int Min, int Max, List<ExpandableSelector> lista, SocketClient socket) {
        this.eSelector=eSelector;
        gestureDetector = new GestureDetector(MainActivity.context, new MyGestureDetector());
        otros=lista;
        Unidad=unidad;
        CurrentValue=0;
        eSelector.showExpandableItems(getExpandableItems());
        setListener();
        this.socket=socket;
        tipo=RANGO;
    }

    /*
    public Campo(ExpandableSelector eSelector, String[] values, List<ExpandableSelector> lista, SocketClient socket) {
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
        /*eSelector.setOnExpandableItemClickListener(new OnExpandableItemClickListener() {
            @Override
            public void onExpandableItemClickListener(int index, View view) {
                switch (index) {
                    case 0:
                        MainActivity.sendData(Constantes.DOWNPULSE,MainActivity.context);
                        break;
                    case 1:
                        eSelector.collapse();
                        break;
                    case 2:
                        MainActivity.sendData(Constantes.UP_PULSE,MainActivity.context);
                        break;
                    default:
                }
            }
        });*/
        eSelector.getChildAt(0).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                btn=0;
                if(event.getAction()==MotionEvent.ACTION_UP&&LongTap) {
                    LongTap=false;
                    //MainActivity.sendData(Constantes.SOLTARUP,MainActivity.context);
                    //Toast.makeText(MainActivity.context, "Se Solto", Toast.LENGTH_SHORT).show();
                }
                return gestureDetector.onTouchEvent(event);
            }
        });
        eSelector.getChildAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn=1;
                eSelector.collapse();
            }
        });
        eSelector.getChildAt(2).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(expandido) {
                    btn=2;
                    if (event.getAction() == MotionEvent.ACTION_UP && LongTap) {
                        LongTap = false;
                        //MainActivity.sendData(Constantes.SOLTARDOWN,MainActivity.context);
                        //Toast.makeText(MainActivity.context, "Se Solto", Toast.LENGTH_SHORT).show();
                    }
                    return gestureDetector.onTouchEvent(event);
                }
                return false;
            }
        });
        /*
        eSelector.getChildAt(0).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.i("Evento +: ",event.toString());
                return false;
            }
        });

        eSelector.getChildAt(2).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.i("Evento -: ",event.toString());
                return false;
            }
        });
        */
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
                        MainActivity.sendData(Constantes.DOWNPULSE,MainActivity.context);
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
        if(!expandido)
            eSelector.updateExpandableItem(0, new ExpandableItem(Integer.toString(CurrentValue) + " " + Unidad));
        else
            eSelector.updateExpandableItem(1, new ExpandableItem(Integer.toString(CurrentValue) + " " + Unidad));
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

    private class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            //Toast.makeText(MainActivity.context, "Single Tap", Toast.LENGTH_SHORT).show();
            if(btn==0)
                MainActivity.sendData(Constantes.UP_PULSE,MainActivity.context);
            else if(btn==2)
                MainActivity.sendData(Constantes.DOWNPULSE,MainActivity.context);
            LongTap=false;
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if(btn==0) {
                MainActivity.sendData(Constantes.PULSARUP, MainActivity.context);
                Toast.makeText(MainActivity.context, "Incrementando", Toast.LENGTH_SHORT).show();
            }
            else if(btn==2) {
                MainActivity.sendData(Constantes.PULSARDOWN, MainActivity.context);
                Toast.makeText(MainActivity.context, "Decrementando", Toast.LENGTH_SHORT).show();
            }
            LongTap = true;

        }



    }

}


