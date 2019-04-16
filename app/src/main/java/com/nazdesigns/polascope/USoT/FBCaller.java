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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public abstract class FBCaller {
    private static final String TAG = "FireBaseCaller";

    public interface onListCallback{
        void onListReturned(List<String> result);
    }
    public interface onStringCallback{
        void onStringReturned(String result);
    }
    public interface onArrayCallback{
        void onArrayReturned(String[][] result);
    }
    public interface onBoolCallback{
        void onBooleanResult(boolean result);
    }
    public interface onIntCallback{
        void onIntResult(int result);
    }
    public interface onTLCallback{
        void onTimeLapseResult(TimeLapse result);
    }

    /**
    Pregunta si el jugador con Id dado ya tiene las estructuras minimas necesarias
    Inicializa al nuevo jugador si en necesario
     */
    public static void setPlayer(final String playerId, final String name){
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
    }

    public static String[][] getAllPlayers() {
        // TODO: Llenar con llamada verdadera a Firebase
        String[][] arr = {{"1","2","3"},{"Pepe","Lina","Aysa"}};
        return arr;
    }

    public static boolean isUserNameInUse(String userName){
        // TODO: Llenar con llamada verdadera a Firebase
        return true;
    }

    /**
    Regresa una lista que contiene todos los juegos del jugador
     */
    public static void getPlayerGames(final onListCallback callbackResult){
        String playerId;
        try{
            playerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }catch (NullPointerException e){
            e.printStackTrace();
            Log.e(TAG,"User null al intentar obtener sus juegos");
            callbackResult.onListReturned(new ArrayList<String>());
            return;
        }
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("players").child(playerId).child("games")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Object value = dataSnapshot.getValue();
                if ( !(value instanceof List) ) {
                    callbackResult.onListReturned(new ArrayList<String>());
                } else {
                    callbackResult.onListReturned( (List<String>) value);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG,"Cancelada petición a FB para Obtener Juegos del Usuario");
                callbackResult.onListReturned(new ArrayList<String>());
            }
        });
    }

    /*
     * Crea un nuevo juego junto al time lapse con Id parentfbId antes o despues acorde a isBefore
     */
    public static void createNewTimeLapse(TimeLapse timeLapse, String parentfbId, boolean isBefore){
        // TODO: Llenar con llamada verdadera a Firebase
    }

    /*
     * Crea un nuevo juego en una timeLapse vacia con Id parentfbId
     */
    public static void createNewTimeLapse(TimeLapse timeLapse, final String parentfbId){
        final String gameId = createTimelapse(timeLapse);

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("timelapses").child(parentfbId).child("timelapse")
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() == null) {
                        Log.e(TAG, "Se intentó crear un sublebado a un TimeLapse null");
                    } else if (dataSnapshot.getValue() instanceof TimeLapse) {
                        TimeLapse tl = (TimeLapse) dataSnapshot.getValue();
                        List<String> subEpochs = tl.getSubEpochsIds();
                        if (!subEpochs.contains(gameId)) {
                            subEpochs.add(gameId);
                            tl.setSubEpochsIds(subEpochs);
                            ref.child("timelapses").child(parentfbId).child("timelapse").setValue(tl);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG,"Error actualizando los juegos de un jugador");
                }
            });
    }

    private static String createTimelapse(TimeLapse timeLapse){
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final String gameId = ref.child("timelapses").push().getKey();

        /*
         * Agregamos el Timelapse
         * */
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/timelapse", timeLapse);
        ref.child("timelapses").child(gameId).updateChildren(childUpdates);

        return gameId;
    }

    public static String createNewGame(TimeLapse timeLapse, List<String> playersIds){
        /*
        * Agregamos el Timelapse y nos devuelve su Id
        * */
        String gameId = createTimelapse(timeLapse);

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
        ref.child("timelapses").child(gameId).updateChildren(childUpdates);

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

    public static void saveTimeLapse(String fbId, TimeLapse timeLapse){
        // TODO: Llenar con llamada verdadera a Firebase
    }

    /*
    Regresa el jueoa apropiado al id pasado
     */
    public static TimeLapse getGame(Context context, String gameId){
        // TODO: Llenar con llamada verdadera a Firebase
        boolean is = getLight(gameId);
        String [] l = {"AA","BB","CC","DD","EE","FF","GG","HH","II","JJ","KK","LL","MM","NN","OO",
                "PP","QQ","RR","SS","TT","UU","VV","WW","XX","YY","ZZ"};
        TimeLapse timeLapse = new TimeLapse(13,is,
                getResume(gameId),
                context.getResources().getString(R.string.large_text),
                0.0, Arrays.asList(l));
        return timeLapse;
    }

    /*
    Regresa una lista de los ids de los TimeLapse subordinados al TimeLapse en cuestion
     */
    public static List<String> getSubEpochs(String gameId){
        // TODO: Llenar con llamada verdadera a Firebase
        String [] l = {"AA","BB","CC","DD","EE","FF","GG","HH","II","JJ","KK","LL","MM","NN","OO",
                "PP","QQ","RR","SS","TT","UU","VV","WW","XX","YY","ZZ"};
        if ( gameId != null && gameId.equals("AA")){
            return new ArrayList<>();
        } else {
            return Arrays.asList(l);
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
