package com.nazdesigns.polascope.USoT;

/*
Encargada de traer cosas desde FireBaseRealtimeDB
 */

import com.nazdesigns.polascope.GameStructure.TimeLapse;

import java.util.List;

public interface DBCaller {

    /*
    Regresa una lista que contiene todos los juegos del jugador
     */
    public List<TimeLapse> getAllGames();

    /*
    Regresa el jueoa apropiado al id pasado
     */
    public TimeLapse getGame(String gameId);

    /*
    Inicializa al nuevo jugador
    Crear estructuras necesarias dentro de la Base de Datos
     */
    public void setPlayer();

}
