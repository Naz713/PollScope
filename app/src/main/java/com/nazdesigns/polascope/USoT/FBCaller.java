package com.nazdesigns.polascope.USoT;

import android.content.Context;

import com.nazdesigns.polascope.GameStructure.TimeLapse;
import com.nazdesigns.polascope.R;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

// TODO: Llenar con llamadas verdaderas a Firebase
public abstract class FBCaller {

    /*
    Regresa una lista que contiene todos los juegos del jugador
    Desde aquí se consulta su ID
     */
    public static List<String> getPlayerGames(){
        String [] l = {"AA","BB","CC","DD"};
        return Arrays.asList(l);
    }

    /*
    Regresa una lista de los ids de los TimeLapse subordinados al TimeLapse en cuestion
     */
    public static List<String> getSubEpochs(String gameId){
        String [] l = {"AA","BB","CC","DD"};
        return Arrays.asList(l);
    }

    /*
    Regresa el jueoa apropiado al id pasado
     */
    public static TimeLapse getGame(Context context, String gameId){
        boolean is = new Random().nextBoolean();
        TimeLapse timeLapse = new TimeLapse(13,is,
                "Este es un resumen generico. De una aventura Épica y maravillosa. Que algún día escribiremos",
                context.getResources().getString(R.string.large_text),0.0,getPlayerGames());
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
        return "Este es un resumen generico. De una aventura Épica y maravillosa. Que algún día escribiremos";
    }

}
