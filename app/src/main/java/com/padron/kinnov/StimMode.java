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
public class StimMode {
    CollapseClass collapseClass= CollapseClass.getInstance();
    FancyButton modo1,modo2,modo3;
    private int modSelected;
    private boolean isExpanded;
    private FancyButton[] modosBtns;
    private String[] modosLabels;
    private int identificador;
    private LinearLayout[] layouts;

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
                selectMode(0);
            }
        });
        modo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isExpanded) {
                    Values.itemSelected=identificador;
                    collapseClass.notifica();
                    setTexts();
                    modo1.setVisibility(View.VISIBLE);
                    modo3.setVisibility(View.VISIBLE);
                    isExpanded = true;
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

    public void selectMode(int indice){
        modSelected=indice;
        switch(modSelected){
            case 0:
                Values.NumItems=5;
                Values.Media=2;
                hideLayouts();
                break;
            default:
                Values.NumItems=9;
                Values.Media=4;
                showAllLayouts();
                break;
        }

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

    public int getIdentificador(){
        return identificador;
    }

}
