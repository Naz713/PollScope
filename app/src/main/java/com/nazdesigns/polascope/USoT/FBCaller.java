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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
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
    public static void createNewTimeLapse(final TimeLapse timeLapse, final String brotherfbId,
                                          final boolean isBefore){
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("timelapses").child(brotherfbId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    Log.e(TAG, "Se intentó crear un hermano a un TimeLapse null");
                }

                /*
                * Dentro del objeto iteramos en su contenido para encontrar la raiz index y hermanos
                * */
                String parentfbId = null;
                String other = null;
                double otherIndex = 0.0;
                for ( DataSnapshot obj : dataSnapshot.getChildren() ){
                    switch (obj.getKey()){
                        case "index":
                            otherIndex = (double) obj.getValue();
                            break;
                        case "raiz":
                            parentfbId = (String) obj.getValue();
                            break;
                        case "before_brother":
                            if (isBefore) {
                                other = (String) obj.getValue();
                            }
                            break;
                        case "after_brother":
                            if (!isBefore) {
                                other = (String) obj.getValue();
                            }
                            break;
                    }
                }

                final String secondBrother = other;
                final double brotherIndex = otherIndex;
                /*
                 * Creamos el timeLapse con la raiz conocida
                 */
                createNewTimeLapse(timeLapse, parentfbId, new onStringCallback() {
                    @Override
                    public void onStringReturned(final String gameId) {
                        /*
                        * Calculamos el index
                        * si el hermano es null es porque estamos en un extremo
                        * si no es nulo sacamos el promedio para calcular el index
                        * */
                        double index = 0;
                        if (secondBrother == null) {
                            if (isBefore) {
                                index = brotherIndex - 100;
                            } else {
                                index = brotherIndex + 100;
                            }
                        } else {
                            ref.child("timelapses").child(secondBrother).child("index")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    double index;
                                    if (dataSnapshot.getValue() == null) {
                                        Log.e(TAG,"Segundo hermano sin index");
                                        if (isBefore) {
                                            index = brotherIndex - 0.1;
                                        } else {
                                            index = brotherIndex + 0.1;
                                        }
                                    } else {
                                        index = ( (double) dataSnapshot.getValue() + brotherIndex)/2;
                                    }
                                    ref.child("timelapses").child(gameId).child("index").setValue(index);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.e(TAG,"Cancelada petición al consultar el segundo hermano");
                                }
                            });

                        }

                        Map<String, Object> childUpdates = new HashMap<>();
                        if (secondBrother == null) {
                            childUpdates.put("/index", index);
                        }
                        if (isBefore){
                            childUpdates.put("/before_brother", secondBrother);
                            childUpdates.put("/after_brother", brotherfbId);
                        } else {
                            childUpdates.put("/before_brother", brotherfbId);
                            childUpdates.put("/after_brother", secondBrother);
                        }
                        ref.child("timelapses").child(gameId).updateChildren(childUpdates);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG,"Cancelada petición al consultar el primer hermano");
            }
        });
    }

    /*
     * Crea un nuevo juego en una timeLapse vacia con Id parentfbId
     */
    public static void createNewTimeLapse(final TimeLapse timeLapse, final String parentfbId,
                                          final onStringCallback callback) {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        ref.child("timelapses").child(parentfbId).child("timelapse")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                    if ( (dataSnapshot.getValue() == null) ||
                            !(dataSnapshot.getValue() instanceof TimeLapse) ) {
                        Log.e(TAG, "Se intentó crear un sublebado a un TimeLapse nulo");
                        callback.onStringReturned(null);
                    } else {
                        createTimelapse(timeLapse, new onStringCallback() {
                            @Override
                            public void onStringReturned(String gameId) {
                                if (gameId == null) {
                                    callback.onStringReturned(null);
                                    return;
                                }
                                TimeLapse tl = (TimeLapse) dataSnapshot.getValue();
                                List<String> subEpochs = tl.getSubEpochsIds();
                                if (!subEpochs.contains(gameId)) {
                                    subEpochs.add(gameId);
                                    tl.setSubEpochsIds(subEpochs);
                                    ref.child("timelapses").child(parentfbId).child("timelapse").setValue(tl);

                                    // Actualizamos el Timelapse padre en el recien creado
                                    Map<String, Object> childUpdates = new HashMap<>();
                                    childUpdates.put("/raiz", parentfbId);
                                    ref.child("timelapses").child(gameId).updateChildren(childUpdates);
                                }

                            }
                        });

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG,"Error actualizando los juegos de un jugador");
                    callback.onStringReturned(null);
                }
            });
    }

    private static void createTimelapse(final TimeLapse timeLapse, final onStringCallback callback){
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("timelapses").push().runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                /*
                 * Agregamos el Timelapse
                 * */
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/timelapse", timeLapse);
                childUpdates.put("/index", 0.0);
                mutableData.setValue(childUpdates);

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                if (databaseError != null && b && dataSnapshot != null) {
                    Log.d(TAG, "postTransaction:onComplete:" + databaseError);
                    callback.onStringReturned(dataSnapshot.getKey());
                } else {
                    Log.e(TAG, "postTransaction:onCompleteWithError:" + databaseError);
                    callback.onStringReturned(null);
                }
            }
        });
    }

    public static void createNewGame(TimeLapse timeLapse, final List<String> playersIds,
                                     final onStringCallback callback){
        /*
        * Agregamos el Timelapse y nos devuelve su Id
        * */
        createTimelapse(timeLapse, new onStringCallback() {
            @Override
            public void onStringReturned(String gameId) {
                if (gameId == null){
                    callback.onStringReturned(null);
                    return;
                }
                /*
                * Actualizamos los jugadores del juego y los juegos de los jugadores
                * */
                addGamePlayers(gameId, playersIds);
                callback.onStringReturned(gameId);
            }
        });
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
                        if ( (dataSnapshot.getValue() == null) ||
                        !(dataSnapshot.getValue() instanceof List) ){
                            List<String> games = new ArrayList<>();
                            games.add(gameId);
                            ref.child("players").child(playerId).child("games").setValue(games);
                        } else {
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
