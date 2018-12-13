package com.nazdesigns.polascope.USoT;

/*
Encargada de traer cosas desde FireBaseRealtimeDB
 */

import com.nazdesigns.polascope.GameStructure.TimeLapse;

public class DBCaller {

    public static TimeLapse getGame(){
        return new TimeLapse("",false,"");
    }

}
