package com.nazdesigns.polascope.USoT;

import com.nazdesigns.polascope.GameStructure.TimeLapse;

import java.lang.ref.SoftReference;
import java.sql.Time;
import java.util.List;

// TODO: Llenar
public abstract class FBCaller {

    /*
    Regresa una lista que contiene todos los juegos del jugador
    Desde aquí se consulta su ID
     */
    public static List<String> getPlayerGames(){
        return null;
    }

    /*
    Regresa una lista de los ids de los TimeLapse subordinados al TimeLapse en cuestion
     */
    public static List<String> getSubEpochs(String gameId){
        return null;
    }

    /*
    Regresa el jueoa apropiado al id pasado
     */
    public static TimeLapse getGame(String gameId){
        return null;
    }

    /*
    Pregunta si el jugador con Id dado ya tiene las estructuras minimas necesarias
    Inicializa al nuevo jugador
    Crear estructuras necesarias dentro de la Base de Datos
     */
    public static boolean setPlayer(String PlayerId){
        return true;
    }

    public static boolean saveTimeLapse(String fbId, TimeLapse timeLapse){
        return true;
    }

    /*
    *
    */
    public static String getResume(String gameId){
        return "";
    }

}
