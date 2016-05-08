package com.padron.kinnov;

import android.util.Log;

import com.padron.kinnov.events.Event;
import com.padron.kinnov.events.EventDIspatcher;

/**
 * Created by Antonio on 06/05/2016.
 */
public class ClaseEventos extends EventDIspatcher {
    private static ClaseEventos ourInstance;
    private static boolean created=false;

    public static ClaseEventos getInstance() {
        if(!created) {
            ourInstance = new ClaseEventos();
            created=true;
        }
        return ourInstance;
    }

    private ClaseEventos() {
    }

    public void MSGCallback(){
        Event event = new Event(Event.MSGRCV,"Mensaje Recibido");
        dispatchEvent(event);

    }
}
