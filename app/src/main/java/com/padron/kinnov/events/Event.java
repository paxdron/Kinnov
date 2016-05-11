package com.padron.kinnov.events;

public class Event {
    public static final String MSGRCV = "mensajeTCP";

    protected String strType = "";
    protected Object params;

    public Event(String type,Object params){
        initProperties(type,params);
    }

    protected void initProperties(String type,Object params){
        strType = type;
        this.params = params;
    }

    public String getStrType(){
        return strType;
    }

    public Object getParams(){

        return this.params;
    }
}
