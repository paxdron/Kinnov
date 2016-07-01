package com.padron.kinnov;

import com.padron.kinnov.Conexion.SocketClient;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Antonio on 17/05/2016.
 */
public class Constantes {
    public static final byte INIT_FRAME     =   16;
    public static final byte FINAL_FRAME    =   17;
    public static final byte DATA_LINK      =   18;
    public static final byte COMANDO        =   31;
    public static final byte[] COMMAND_UP ={23,25,27,29};
    public static final byte[] COMMAND_DOWN ={24,26,28,30};
    public static final byte GETLCD                 =   1;
    public static final byte COMMAND_SETUP          =   19;
    public static final byte COMMAND_SETDOWN        =   20;
    public static final byte COMMAND_BACK           =   21;
    public static final byte COMMAND_NEXT           =   22;
    public static final byte COMMANDSTARTSTOP       =   17;
    public static final byte COMMANDPROGMENU        =   18;
    public static final byte COMMAND_PULSAR        =   37;
    public static final byte COMMAND_SOLTAR        =   38;

    public static final byte[] NEXTPULSE= SocketClient.pack(COMMAND_NEXT);
    public static final byte[] BACKPULSE= SocketClient.pack(COMMAND_BACK);
    public static final byte[] UP_PULSE= SocketClient.pack(COMMAND_SETUP);
    public static final byte[] DOWNPULSE= SocketClient.pack(COMMAND_SETDOWN);
    public static final byte[] STARTSTOP= SocketClient.pack(COMMANDSTARTSTOP);
    public static final byte[] PROGMENU= SocketClient.pack(COMMANDPROGMENU);
    public static final byte[] PULSARUP     = {INIT_FRAME,COMMAND_PULSAR,COMMAND_SETUP,     FINAL_FRAME};
    public static final byte[] PULSARDOWN   = {INIT_FRAME,COMMAND_PULSAR,COMMAND_SETDOWN,   FINAL_FRAME};
    public static final byte[] SOLTARUP     = {INIT_FRAME,COMMAND_SOLTAR,COMMAND_SETUP,     FINAL_FRAME};
    public static final byte[] SOLTARDOWN   = {INIT_FRAME,COMMAND_SOLTAR,COMMAND_SETDOWN,   FINAL_FRAME};


    public static final byte[] UPDATETEXT             =   {INIT_FRAME,GETLCD,FINAL_FRAME};
    public static final int DELAY                     =  300;
    private static final String[] PROTOCOLS_          =    {"fort.","reedu","fesa","reduc","modd"};
    public static final List<String> PROTOCOLS        =     Arrays.asList(PROTOCOLS_);
    public static final int NUMPROTOCOLS              =     7;
    public static final int MEDIAPROTOCOLS            =     3;
    public static final byte[] NCOMMAND_UP ={23,25,27,29};
    public static final int POSLEDBYTE                =     37;
    public static final byte[] MASKS ={1,2,4,8};

}
