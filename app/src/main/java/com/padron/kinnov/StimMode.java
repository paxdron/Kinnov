package com.padron.kinnov;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import com.padron.kinnov.events.CollapseClass;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by Antonio on 16/05/2016.
 */
public class StimMode{
    CollapseClass collapseClass= CollapseClass.getInstance();
    FancyButton modo1,modo2,modo3;
    private int modSelected;
    private boolean isExpanded;
    public static boolean keepShowing=false;
    private FancyButton[] modosBtns;
    private String[] modosLabels;
    private int identificador;
    private LinearLayout[] layouts;
    private int pasos;
    private int times;
    private int media=1;
    private int NumItems=3;

    public StimMode(FancyButton[] modosBtns, String[] modosLabels, LinearLayout[] layouts) {
        this.modosBtns = modosBtns;
        modo1=modosBtns[0];
        modo2=modosBtns[1];
        modo3=modosBtns[2];
        this.modosLabels = modosLabels;
        this.layouts=layouts;
        setListeners();
    }

    private void setListeners(){
        modo1.setVisibility(View.GONE);
        modo3.setVisibility(View.GONE);

        modo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMode(modSelected+1,1);
                keepShowing=false;
                selectMode(0);
            }
        });
        modo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isExpanded) {
                    Values.itemSelected=identificador;
                    keepShowing=true;
                    collapseClass.notifica();
                    setTexts();
                    modo1.setVisibility(View.VISIBLE);
                    modo3.setVisibility(View.VISIBLE);
                    isExpanded = true;
                } else {
                    setMode(modSelected+1,2);
                    keepShowing=false;
                    selectMode(1);
                }
            }
        });
        modo3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMode(modSelected+1,3);
                keepShowing = false;
                selectMode(2);
            }
        });
    }

    public void setTexts(){
        for (int i=0;i<3;i++) {
            modosBtns[i].setText(modosLabels[i]);
        }
    }

    public void selectMode(int indice){
        modSelected=indice;
        switch(modSelected){
            case 0:
                Values.setItemVals(true);
                hideLayouts();
                break;
            default:
                Values.setItemVals(false);
                showAllLayouts();
                break;
        }
        if(!keepShowing)
            Collapse();
    }

    private void showAllLayouts(){
        for (LinearLayout ll:layouts
             ) {
            ll.setVisibility(View.VISIBLE);
        }

    }

    private void hideLayouts(){
        for (LinearLayout ll:layouts
                ) {
            ll.setVisibility(View.GONE);
        }
    }

    public void Collapse(){
        modo2.setText(modosLabels[modSelected]);
        if(isExpanded) {
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
            isExpanded = false;
        }
    }

    public void setIdentificador(int identificador){
        this.identificador=identificador;
    }

    /**
     * obtiene el valor del identificador
     * @return
     */
    public int getIdentificador(){
        return identificador;
    }

    /**
     * Determina el puso que hay que dar para modificar el modo de estimuladion en pantalla
     * @param currentPos el valor del modo actual
     * @param newPos el valor del nuevo modo
     */
    private void setMode(int currentPos, int newPos){
        pasos = newPos-currentPos;
        if(pasos!=0) {
            if (Math.abs(pasos) <= media) {
                times=Math.abs(pasos);
                if(pasos>0) {
                    MainActivity.sendData(Constantes.UP_PULSE, MainActivity.context);
                }
                else
                    MainActivity.sendData(Constantes.DOWNPULSE, MainActivity.context);
            }
            else{
                if(pasos>0) {
                    pasos=NumItems-pasos;
                    times=pasos;
                    MainActivity.sendData(Constantes.DOWNPULSE, MainActivity.context);
                }
                else{
                    pasos=NumItems+pasos;
                    times=pasos;
                    MainActivity.sendData(Constantes.UP_PULSE, MainActivity.context);
                }
            }
        }
    }

}
