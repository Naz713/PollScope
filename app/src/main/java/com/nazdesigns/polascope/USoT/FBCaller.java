package com.nazdesigns.polascope.USoT;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nazdesigns.polascope.GameStructure.TimeLapse;
import com.nazdesigns.polascope.R;

import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

// TODO: Llenar con llamadas verdaderas a Firebase
public abstract class FBCaller {
    private static final String TAG = "FireBaseCaller";

    /*
    Regresa una lista que contiene todos los juegos del jugador
    Desde aquí se consulta su ID
     */
    public static List<String> getPlayerGames(){
        String [] l = {"AA","BB","CC","DD","EE","FF","GG","HH","II","JJ","KK","LL","MM","NN","OO","PP","QQ","RR","SS","TT","UU","VV","WW","XX","YY","ZZ"};
        return Arrays.asList(l);
    }

    /*
    Regresa una lista de los ids de los TimeLapse subordinados al TimeLapse en cuestion
     */
    public static List<String> getSubEpochs(String gameId){
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
        boolean is = getLight(gameId);
        TimeLapse timeLapse = new TimeLapse(13,is,
                getResume(gameId),
                context.getResources().getString(R.string.large_text),0.0,getPlayerGames());
        return timeLapse;
    }

    /*
    Pregunta si el jugador con Id dado ya tiene las estructuras minimas necesarias
    Inicializa al nuevo jugador
    Crear estructuras necesarias dentro de la Base de Datos
     */
    public static boolean setPlayer(String playerId, String name){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("players").child(playerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ( dataSnapshot.getValue() == null) {
                    //TODO: incializar las estructuras del jugador
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
        return true;
    }

    public static boolean createNewTimeLapse(TimeLapse timeLapse, String parentfbId, boolean isBefore){
        return true;
    }

    /*
    *
    */
    public static String getResume(String gameId){
        return "Una aventura Épica y maravillosa. Que algún día escribiremos, nos sorprenderemos leyendola y felices.";
    }

    public static boolean getLight(String gameId){
        return new Random().nextBoolean();
    }

}
