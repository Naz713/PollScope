package com.nazdesigns.polascope.USoT;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nazdesigns.polascope.GameStructure.TimeLapse;
import com.nazdesigns.polascope.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.TreeMap;

public abstract class FBCaller {
    private static final String TAG = "FireBaseCaller";

    public static String[][] getAllPlayers() {
        // TODO: Llenar con llamada verdadera a Firebase
        String[][] arr = {{"1","2","3"},{"Pepe","Lina","Aysa"}};
        return arr;
    }

    /*
    Regresa una lista que contiene todos los juegos del jugador
    Desde aquí se consulta su ID
     */
    public static List<String> getPlayerGames(){
        // TODO: Llenar con llamada verdadera a Firebase
        String [] l = {"AA","BB","CC","DD","EE","FF","GG","HH","II","JJ","KK","LL","MM","NN","OO",
                "PP","QQ","RR","SS","TT","UU","VV","WW","XX","YY","ZZ"};
        return Arrays.asList(l);
    }

    /*
    Regresa una lista de los ids de los TimeLapse subordinados al TimeLapse en cuestion
     */
    public static List<String> getSubEpochs(String gameId){
        // TODO: Llenar con llamada verdadera a Firebase
        if ( gameId != null && gameId.equals("AA")){
            String [] l = {};
            return Arrays.asList(l);
        } else {
            return getPlayerGames();
        }
    }

    /*
    Regresa el jueoa apropiado al id pasado
     */
    public static TimeLapse getGame(Context context, String gameId){
        // TODO: Llenar con llamada verdadera a Firebase
        boolean is = getLight(gameId);
        TimeLapse timeLapse = new TimeLapse(13,is,
                getResume(gameId),
                context.getResources().getString(R.string.large_text),
                0.0,getPlayerGames());
        return timeLapse;
    }

    /*
    Pregunta si el jugador con Id dado ya tiene las estructuras minimas necesarias
    Inicializa al nuevo jugador si en necesario
     */
    public static boolean setPlayer(final String playerId, final String name){
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("players").child(playerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ( dataSnapshot.getValue() == null) {
                    HashMap<String, Object> player = new HashMap<>();
                    player.put("name", name);
                    ref.child("players").child(playerId).setValue(player);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG,"Cancelada petición a FB para Crear Usuario");
            }
        });

        return true;
    }

    public static boolean saveTimeLapse(String fbId, TimeLapse timeLapse){
        // TODO: Llenar con llamada verdadera a Firebase
        return true;
    }

    public static boolean isUserNameInUse(String userName){
        // TODO: Llenar con llamada verdadera a Firebase
        return true;
    }

    /*
     * Crea un nuevo juego junto al time lapse con Id parentfbId antes o despues acorde a isBefore
     */
    public static boolean createNewTimeLapse(TimeLapse timeLapse, String parentfbId, boolean isBefore){
        // TODO: Llenar con llamada verdadera a Firebase
        return true;
    }

    /*
     * Crea un nuevo juego en una timeLapse vacia con Id parentfbId
     */
    public static boolean createNewTimeLapse(TimeLapse timeLapse, String parentfbId){
        // TODO: Llenar con llamada verdadera a Firebase
        return true;
    }

    public static String createNewGame(TimeLapse timeLapse, List<String> playersIds){
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final String gameId = ref.child("games").push().getKey();

        /*
        * Agregamos el Timelapse
        * */
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/timelapse", timeLapse);
        ref.child("games").child(gameId).updateChildren(childUpdates);

        /*
        * Actualizamos los jugadores del juego y los juegos de los jugadores
        * */
        addGamePlayers(gameId, playersIds);

        return gameId;
    }

    /*
     * Los jugaores no se sobreescriben, solo se añaden extras
     */
    public static void addGamePlayers(final String gameId, List<String> playersIds){
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        // Actualizamos los jugadores en el juego
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/players", playersIds);
        ref.child("games").child(gameId).updateChildren(childUpdates);

        // Actualizamos el juego en los players
        for (final String playerId : playersIds) {
            ref.child("players").child(playerId).child("games")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) {
                            List<String> games = new ArrayList<>();
                            games.add(gameId);
                            ref.child("players").child(playerId).child("games").setValue(games);
                        } else if (dataSnapshot.getValue() instanceof List) {
                            List<String> games = (List<String>) dataSnapshot.getValue();
                            if (!games.contains(gameId)){
                                games.add(gameId);
                                ref.child("players").child(playerId).child("games").setValue(games);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG,"Error actualizando los juegos de un jugador");
                    }
                });
        }
    }

    /*
    *
    */
    public static String getResume(String gameId){
        // TODO: Llenar con llamada verdadera a Firebase
        return "Una aventura Épica y maravillosa. Que algún día escribiremos, nos sorprenderemos leyendola y felices.";
    }

    public static boolean getLight(String gameId){
        // TODO: Llenar con llamada verdadera a Firebase
        return new Random().nextBoolean();
    }

    public static int getType(String gameId){
        // TODO: Llenar con llamada verdadera a Firebase
        return new Random().nextInt(3);
    }

}
