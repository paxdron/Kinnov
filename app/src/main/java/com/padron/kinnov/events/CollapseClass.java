package com.padron.kinnov.events;

/**
 * Created by Antonio on 14/05/2016.
 */
public class CollapseClass {
    public static CollapseClass instancia;
    public static boolean isCreated=false;
    ICollapse CallbackCollapseClass;

    public static CollapseClass getInstance(){
        if(!isCreated){
            instancia=new CollapseClass();
            isCreated=true;
        }
        return instancia;
    }

    public void registerCallback(ICollapse callbackClass){
        CallbackCollapseClass = callbackClass;
    }

    public void notifica(){
        CallbackCollapseClass.callbackCollapse();
    }

}