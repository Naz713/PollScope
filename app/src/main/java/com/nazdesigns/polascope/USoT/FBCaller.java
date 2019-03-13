package com.nazdesigns.polascope.USoT;

import com.nazdesigns.polascope.GameStructure.TimeLapse;

import java.lang.ref.SoftReference;
import java.util.List;


public abstract class FBCaller {

    /*
    Regresa una lista que contiene todos los juegos del jugador
     */
    public List<TimeLapse> getAllGames(){
        return null;
    }

    /*
    Regresa el jueoa apropiado al id pasado
     */
    public TimeLapse getGame(String gameId){
        return null;
    }

    /*
    Pregunta si el jugador con Id dado ya tiene las estructuras minimas necesarias
    Inicializa al nuevo jugador
    Crear estructuras necesarias dentro de la Base de Datos
     */
    public static void setPlayer(String PlayerId){

    }
}
