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
    public interface onListListCallback{
        void onArrayReturned(List<String> result, List<String> result2);
    }
    public interface onBoolCallback{
        void onBooleanResult(boolean result, boolean error);
    }
    public interface onIntCallback{
        void onIntResult(int result, boolean error);
    }
    public interface onTLCallback{
        void onTimeLapseResult(TimeLapse result);
    }
    public interface onListTLCallback{
        void onListTimeLapseResult(List<TimeLapse> result, List<String> ids);
    }


    private static FirebaseDatabase mDatabase;

    private static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
            //TODO: Afinar esto para que no tengamos una copia del DB en cada telefono
            // aunque no debe ser muy pesado
            mDatabase.getReference().keepSynced(true);
        }
        return mDatabase;
    }

    /**
    Pregunta si el jugador con Id dado ya tiene las estructuras minimas necesarias
    Inicializa al nuevo jugador si en necesario
     */
    public static void setPlayer(final String playerId, final String name){
        final DatabaseReference ref = getDatabase().getReference();
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

    /**
     * Regresa una lista de arrays en el primero los ids de los jugadores, en el segundo sus nombres
     */
    public static void getAllPlayers(final onListListCallback callback) {
        final DatabaseReference ref = getDatabase().getReference();
        ref.child("players").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> ids = new ArrayList<>();
                List<String> names = new ArrayList<String>();
                for (DataSnapshot player: dataSnapshot.getChildren()){
                    ids.add(player.getKey());
                    names.add((String) player.child("name").getValue());
                }
                callback.onArrayReturned(ids, names);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG,"Cancelada petición a FB para Obtener Jugadores");
                callback.onArrayReturned(null, null);
            }
        });
    }

    public static void isUserNameInUse(final String userName, final onBoolCallback callback){
        if (userName == null || userName.length()<3){
            callback.onBooleanResult(true, false);
        }

        getAllPlayers(new onListListCallback() {
            @Override
            public void onArrayReturned(List<String> ids, List<String> names) {
                if (names != null){
                    callback.onBooleanResult(names.contains(userName), false);
                } else {
                    callback.onBooleanResult(true,true);
                }
            }
        });
    }

    /**
    Regresa una lista que contiene todos los juegos del jugador
     */
    public static void getPlayerGamesIds(final onListCallback callbackResult){
        String playerId;
        try{
            playerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }catch (NullPointerException e){
            e.printStackTrace();
            Log.e(TAG,"User null al intentar obtener sus juegos");
            callbackResult.onListReturned(new ArrayList<String>());
            return;
        }
        final DatabaseReference ref = getDatabase().getReference();
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
                                          final boolean isBefore, final onStringCallback callback){
        final DatabaseReference ref = getDatabase().getReference();
        ref.child("timelapses").child(brotherfbId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    Log.e(TAG, "Se intentó crear un hermano a un TimeLapse null");
                    callback.onStringReturned(null);
                    return;
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
                        if (gameId == null) {
                            Log.e(TAG, "Falla al intentar Crear nuevo TimeLapse");
                            callback.onStringReturned(null);
                            return;
                        }
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

                        callback.onStringReturned(gameId);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG,"Cancelada petición al consultar el primer hermano");
                callback.onStringReturned(null);
            }
        });
    }

    /*
     * Crea un nuevo juego en una timeLapse vacia con Id parentfbId
     */
    public static void createNewTimeLapse(final TimeLapse timeLapse, final String parentfbId,
                                          final onStringCallback callback) {
        final DatabaseReference ref = getDatabase().getReference();

        ref.child("timelapses").child(parentfbId).child("timelapse")
            .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if ( (dataSnapshot.getValue() == null) ||
                        !(dataSnapshot.getValue() instanceof HashMap) ) {
                    Log.e(TAG, "Se intentó crear un sublebado a un TimeLapse nulo o dañado");
                    callback.onStringReturned(null);
                } else {
                    createTimelapse(timeLapse, new onStringCallback() {
                        @Override
                        public void onStringReturned(String gameId) {
                            if (gameId == null) {
                                callback.onStringReturned(null);
                                return;
                            }
                            HashMap dt = (HashMap) dataSnapshot.getValue();
                            TimeLapse tl = new TimeLapse(dt);
                            List<String> subEpochs = tl.getSubEpochsIds();
                            if (!subEpochs.contains(gameId)) {
                                subEpochs.add(gameId);
                                tl.setSubEpochsIds(subEpochs);
                                ref.child("timelapses").child(parentfbId).child("timelapse").setValue(tl);

                                // Actualizamos el Timelapse padre en el recien creado
                                Map<String, Object> childUpdates = new HashMap<>();
                                childUpdates.put("/raiz", parentfbId);
                                childUpdates.put("/timelapse/timeType", tl.getTimeType()+1);
                                ref.child("timelapses").child(gameId).updateChildren(childUpdates);

                            }
                                callback.onStringReturned(gameId);
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
        final DatabaseReference ref = getDatabase().getReference();
        ref.child("timelapses").push().runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                /*
                 * Agregamos el Timelapse
                 * */
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("timelapse", timeLapse);
                childUpdates.put("index", 0.0);
                mutableData.setValue(childUpdates);

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                Log.i("FBC",String.format("Error: %s, bool: %s, snapShot: %s", databaseError, b, dataSnapshot));
                if (databaseError == null && b && dataSnapshot != null) {
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
        final DatabaseReference ref = getDatabase().getReference();

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
        final DatabaseReference ref = getDatabase().getReference();

        ref.child("timelapses").child(fbId).child("timelapse").setValue(timeLapse);
    }

    public static void getPlayerGames(final onListTLCallback callback){
        final String playerId;
        try{
            playerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }catch (NullPointerException e){
            e.printStackTrace();
            Log.e(TAG,"User null al intentar obtener sus juegos");
            callback.onListTimeLapseResult(null, null);
            return;
        }
        getTLlist(null, new onListTLCallback() {
            @Override
            public void onListTimeLapseResult(List<TimeLapse> result, List<String> ids) {
                if(result == null || ids == null){
                    callback.onListTimeLapseResult(null, null);
                }

                for(int i=result.size()-1;i>=0;i--){
                    if(!result.get(i).getSubEpochsIds().contains(playerId)){
                        result.remove(i);
                        ids.remove(i);
                    }
                }
                callback.onListTimeLapseResult(result, ids);
            }
        });
    }


    public static void getTLlist(final String rootFbId, final onListTLCallback callback){
        final DatabaseReference ref = getDatabase().getReference();
        ref.child("timelapses").orderByChild("raiz").equalTo(rootFbId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<TimeLapse> ret = new ArrayList<>();
                        List<String> ids = new ArrayList<>();
                        for (DataSnapshot tlList : dataSnapshot.getChildren()) {
                            if (tlList.getValue() instanceof HashMap) {
                                ids.add(tlList.getKey());
                                ret.add(new TimeLapse((HashMap) ((HashMap) tlList.getValue()).get("timelapse")));
                            }
                        }
                        callback.onListTimeLapseResult(ret, ids);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "Cancelada Peticion a ListTimeLapse: "+ rootFbId);
                        callback.onListTimeLapseResult(null, null);
                    }
                });
    }

    /*
    Regresa el jueoa apropiado al id pasado
     */
    public static void getGame(final String gameId, final onTLCallback callback){
        final DatabaseReference ref = getDatabase().getReference();
        ref.child("timelapses").child(gameId).child("timelapse")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i(TAG, dataSnapshot.toString());
                if (dataSnapshot.getValue() instanceof HashMap){
                    HashMap dt = (HashMap) dataSnapshot.getValue();
                    TimeLapse tl = new TimeLapse(dt);
                    callback.onTimeLapseResult(tl);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Cancelada Peticion a TimeLapse: "+gameId);
                callback.onTimeLapseResult(null);
            }
        });
    }

    /*
    Regresa una lista de los ids de los TimeLapse subordinados al TimeLapse en cuestion
     */
    public static void getSubEpochs(final String gameId, final onListCallback callback){
        final DatabaseReference ref = getDatabase().getReference();
        ref.child("timelapses").child(gameId).child("timelapse").child("subEpochsIds")
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Object subEpochs = dataSnapshot.getValue();
                    if (subEpochs instanceof List){
                        callback.onListReturned( (List<String>) subEpochs);
                    }
                    else{
                        Log.e(TAG, String.format("SupEpochs de %s es null o no es tipo List", gameId));
                        callback.onListReturned(null);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Cancelada Peticion a TimeLapse: "+gameId);
                    callback.onListReturned(null);
                }
            });
    }

    @Deprecated
    public static void getResume(final String gameId, final onStringCallback callback){
        final DatabaseReference ref = getDatabase().getReference();
        ref.child("timelapses").child(gameId).child("timelapse").child("resume")
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Object resume = dataSnapshot.getValue();
                    if (resume instanceof String){
                        callback.onStringReturned( (String) resume);
                    }
                    else{
                        Log.e(TAG, String.format("resume de %s es null o no es tipo String", gameId));
                        callback.onStringReturned(null);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Cancelada Peticion a TimeLapse: "+gameId);
                    callback.onStringReturned(null);
                }
            });
    }

    @Deprecated
    public static void getLight(final String gameId, final onBoolCallback callback){
        final DatabaseReference ref = getDatabase().getReference();
        ref.child("timelapses").child(gameId).child("timelapse").child("isLight")
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Object light = dataSnapshot.getValue();
                    if (light instanceof Boolean){
                        callback.onBooleanResult( (Boolean) light, false);
                    }
                    else{
                        Log.e(TAG, String.format("light de %s es null o no es tipo Bool", gameId));
                        callback.onBooleanResult(true, true);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Cancelada Peticion a TimeLapse: "+gameId);
                    callback.onBooleanResult(true, true);
                }
            });
    }

    @Deprecated
    public static void getType(final String gameId, final onIntCallback callback){
        final DatabaseReference ref = getDatabase().getReference();
        ref.child("timelapses").child(gameId).child("timelapse").child("timeType")
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Object type = dataSnapshot.getValue();
                    if (type instanceof Integer) {
                        callback.onIntResult( (Integer) type, false);
                    }
                    else{
                        Log.e(TAG, String.format("timeType de %s es null o no es tipo Int", gameId));
                        callback.onIntResult(0, true);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Cancelada Peticion a TimeLapse: "+gameId);
                    callback.onIntResult(0, true);
                }
            });
    }

}
