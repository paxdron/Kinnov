package com.padron.kinnov;

import android.content.Context;
import android.view.View;

import com.karumi.expandableselector.ExpandableItem;
import com.karumi.expandableselector.ExpandableSelector;
import com.karumi.expandableselector.ExpandableSelectorListener;
import com.karumi.expandableselector.OnExpandableItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by antonio on 19/04/16.
 */
public class Campo {
    String Unidad;
    List<ExpandableItem> expandableItems;
    List<ExpandableSelector> otros;
    ExpandableSelector eSelector;
    boolean expandido;
    int MinValue;
    int MaxValue;
    int CurrentValue;

    int Tipo;
    public Campo(ExpandableSelector eSelector, String unidad, int[] values, List<ExpandableSelector> lista) {
        this.eSelector=eSelector;
        otros=lista;
        setValues(unidad,values);
        expandido=false;
    }

    public Campo(ExpandableSelector eSelector, String unidad, int Min, int Max, List<ExpandableSelector> lista) {
        this.eSelector=eSelector;
        otros=lista;
        setMinMaxValues(unidad, Min, Max);
    }

    public Campo(ExpandableSelector eSelector, String[] values, List<ExpandableSelector> lista) {
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
    }

    void setValues (String unidad,int[]values){
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

    void setMinMaxValues(String unidad,int Min, int Max){
        Unidad=unidad;
        CurrentValue=MinValue=Min;
        MaxValue=Max;
        eSelector.showExpandableItems(getExpandableItems());
        setListener();
    }


    void setListener(){
        eSelector.setOnExpandableItemClickListener(new OnExpandableItemClickListener() {
            @Override
            public void onExpandableItemClickListener(int index, View view) {
                switch (index) {
                    case 0:
                        if (CurrentValue > MinValue)
                            CurrentValue--;
                        eSelector.updateExpandableItem(1, new ExpandableItem(Integer.toString(CurrentValue)+" "+Unidad));
                        break;
                    case 1:
                        eSelector.collapse();
                        break;
                    case 2:
                        if (CurrentValue < MaxValue)
                            CurrentValue++;
                        eSelector.updateExpandableItem(1, new ExpandableItem(Integer.toString(CurrentValue)+" "+Unidad));
                        break;
                    default:
                }

                System.out.println(Integer.toString(CurrentValue));
            }
        });

        eSelector.setExpandableSelectorListener(new ExpandableSelectorListener() {
            @Override
            public void onCollapse() {
                //Do something here
                if(expandido) {
                    ExpandableItem segundo = eSelector.getExpandableItem(1);
                    eSelector.updateExpandableItem(1, eSelector.getExpandableItem(0));
                    eSelector.updateExpandableItem(0, segundo);
                    expandido=false;
                }
            }

            @Override
            public void onExpand() {
                //Do something here
                if(!expandido) {
                    ExpandableItem segundo = eSelector.getExpandableItem(1);
                    eSelector.updateExpandableItem(1, eSelector.getExpandableItem(0));
                    eSelector.updateExpandableItem(0, segundo);
                    collapseOthers(eSelector);
                    expandido=true;
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
                    case 3:
                        ExpandableItem fourthItem = eSelector.getExpandableItem(3);
                        swipeFirstItem(3, fourthItem);
                        break;
                    default:
                }
                eSelector.collapse();
            }

            private void swipeFirstItem(int position, ExpandableItem clickedItem) {
                ExpandableItem firstItem = eSelector.getExpandableItem(0);
                eSelector.updateExpandableItem(0, clickedItem);
                eSelector.updateExpandableItem(position, firstItem);
            }
        });


        eSelector.setExpandableSelectorListener(new ExpandableSelectorListener() {
            @Override
            public void onCollapse() {
                //Do something here
            }

            @Override
            public void onExpand() {
                collapseOthers(eSelector);
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
}


