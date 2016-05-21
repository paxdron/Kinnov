package com.padron.kinnov;

import com.padron.kinnov.Conexion.SocketClient;

/**
 * Created by Antonio on 17/05/2016.
 */
public class Constantes {
    public static final byte INIT_FRAME     =   16;
    public static final byte FINAL_FRAME    =   17;
    public static final byte DATA_LINK      =   18;
    public static final byte COMANDO        =   31;
    public static final byte[] COMMAND_UP ={23,25,27,29};
    public static final byte[] COMMAND_DOWN ={24,25,27,29};
    public static final byte COMMAND_SETUP          =   19;
    public static final byte COMMAND_SETDOWN        =   20;
    public static final byte COMMAND_BACK           =   21;
    public static final byte COMMAND_NEXT           =   22;
    public static final byte UPDATETEXT             =   1;
    public static final byte COMMANDSTARTSTOP       =   17;
    public static final byte[] NEXTPULSE= SocketClient.pack(COMMAND_NEXT);
    public static final byte[] BACKPULSE= SocketClient.pack(COMMAND_BACK);
    public static final byte[] UP_PULSE= SocketClient.pack(COMMAND_SETUP);
    public static final byte[] DOWNPULSE= SocketClient.pack(COMMAND_SETDOWN);
    public static final byte[] STARTSTOP= SocketClient.pack(COMMANDSTARTSTOP);

    public static final int DELAY=300;
}