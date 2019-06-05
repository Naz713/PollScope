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
import com.google.firebase.database.core.utilities.Utilities;
import com.nazdesigns.polascope.GameStructure.TimeLapse;
import com.nazdesigns.polascope.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;

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
                callback.onArrayReturned(new ArrayList<String>(), new ArrayList<String>());
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
                    callback.onBooleanResult(false,true);
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
    public static void createNewTimeLapse(final TimeLapse timeLapse, final String parentId,
                                          final String brotherfbId, final boolean isBefore,
                                          final onStringCallback callback){
        final DatabaseReference ref = getDatabase().getReference();
        ref.child("timelapses").orderByChild("raiz").equalTo(parentId)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.i(TAG, "dataSnapshot: " + dataSnapshot.getValue().toString());
                    if(dataSnapshot.getValue() instanceof HashMap){
                        TreeSet<Map<String, Object>> tree = new TreeSet<>(new Comparator<Map<String, Object>>() {
                            @Override
                            public int compare(Map<String, Object> m1, Map<String, Object> m2) {
                                double i1, i2;
                                if (m1.get("index") instanceof Long){
                                    i1 = Long.valueOf( (long) m1.get("index")).doubleValue();
                                } else {
                                    i1 = (double) m1.get("index");
                                }
                                if (m2.get("index") instanceof Long){
                                    i2 = Long.valueOf( (long) m2.get("index")).doubleValue();
                                } else {
                                    i2 = (double) m2.get("index");
                                }

                                return Double.valueOf(i1 - i2).intValue();
                            }
                        });

                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            HashMap<String, Object> hm = (HashMap) ds.getValue();
                            hm.put("key",ds.getKey());
                            tree.add(hm);
                        }

                        //Log.i(TAG, list.toString());

                        List<Map<String, Object>> list = new ArrayList<>(tree);

                        double firstIndex = 0.0;
                        int firstI = -2;
                        for (int i=0; i<list.size(); i++){
                            if (list.get(i).get("key").equals(brotherfbId)){
                                firstI = i;
                                if (list.get(i).get("index").getClass().equals(java.lang.Long.class) ){
                                    Long l = (long) list.get(i).get("index");
                                    firstIndex = l.doubleValue();
                                } else {
                                    firstIndex = (double) list.get(i).get("index");
                                }
                            }
                        }
                        if(firstI == -2){
                            Log.e(TAG, "Se intentó crear un hermano a un TimeLapse null");
                            callback.onStringReturned(null);
                            return;
                        }

                        int j = isBefore? firstI-1 : firstI +1;
                        double index;
                        if (j>=0 && j<list.size()){
                            if (list.get(j).get("index").getClass().equals(java.lang.Long.class) ){
                                Long l = (long) list.get(j).get("index");
                                index = l.doubleValue();
                            } else {
                                index = (double) list.get(j).get("index");
                            }
                            index = ( firstIndex + index )/2;
                        } else {
                            index = isBefore? firstIndex - 100 : firstIndex + 100;
                        }

                        final double newIndex = index;
                        createNewTimeLapse(timeLapse, parentId, new onStringCallback() {
                            @Override
                            public void onStringReturned(String gameId) {
                                ref.child("timelapses").child(gameId).child("index")
                                        .setValue(newIndex);
                            }
                        });

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG,"Cancelada petición al crear TL con hermano");
                }
            });
    }

    /*
     * Crea un nuevo juego en una timeLapse vacia con Id parentfbId
     */
    public static void createNewTimeLapse(final TimeLapse timeLapse, final String parentfbId,
                                          final onStringCallback callback) {
        final DatabaseReference ref = getDatabase().getReference();

        ref.child("timelapses").child(parentfbId)
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
                                ref.child("timelapses").child(parentfbId).setValue(tl);

                                // Actualizamos el Timelapse padre en el recien creado
                                Map<String, Object> childUpdates = new HashMap<>();
                                childUpdates.put("/raiz", parentfbId);
                                childUpdates.put("/timeType", tl.getTimeType()+1);
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
                timeLapse.setIndex(0.0);
                mutableData.setValue(timeLapse);

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b,
                                   @Nullable DataSnapshot dataSnapshot) {
                Log.i("FBC", String.format("Error: %s, bool: %s, snapShot: %s", databaseError,
                        b, dataSnapshot));
                if (databaseError == null && b && dataSnapshot != null) {
                    Log.d(TAG, "postTransaction:onComplete: " + databaseError);
                    callback.onStringReturned(dataSnapshot.getKey());
                } else {
                    Log.e(TAG, "postTransaction:onCompleteWithError: " + databaseError);
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
    public static void addGamePlayers(final String gameId, final List<String> playersIds){
        final DatabaseReference ref = getDatabase().getReference();

        // Actualizamos los jugadores en el juego
        ref.child("timelapses").child(gameId).child("players")
                .runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                if (mutableData.getValue() instanceof List) {
                    List<String> oldPlayers = (List<String>) mutableData.getValue();
                    for (String player : playersIds){
                        if(!oldPlayers.contains(player)){
                            oldPlayers.add(player);
                        }
                    }
                    mutableData.setValue(oldPlayers);
                } else {
                    mutableData.setValue(playersIds);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b,
                                   @Nullable DataSnapshot dataSnapshot) {}
        });

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

        ref.child("timelapses").child(fbId).setValue(timeLapse);
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
        final DatabaseReference ref = getDatabase().getReference();
        ref.child("timelapses").orderByChild("raiz").equalTo(null)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<TimeLapse> ret = new ArrayList<>();
                    List<String> ids = new ArrayList<>();
                    for (DataSnapshot tlList : dataSnapshot.getChildren()) {
                        try {
                            HashMap<String, Object> tl = ((HashMap) tlList.getValue());

                            if (((List) tl.get("players")).contains(playerId)) {
                                ids.add(tlList.getKey());
                                ret.add(new TimeLapse(tl));
                            }
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }
                    }
                    callback.onListTimeLapseResult(ret, ids);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Cancelada Peticion de Juegos de: "+ playerId);
                    callback.onListTimeLapseResult(null, null);
                }
            });
    }


    public static void getTLlist(final String rootFbId, final onListTLCallback callback){
        final DatabaseReference ref = getDatabase().getReference();
        ref.child("timelapses").orderByChild("raiz").equalTo(rootFbId)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                    List<TimeLapse> ret = new ArrayList<>();
                    List<String> ids = new ArrayList<>();

                    TreeSet<DataSnapshot> sorted = new TreeSet<>(new Comparator<DataSnapshot>() {
                        @Override
                        public int compare(DataSnapshot o1, DataSnapshot o2) {
                            HashMap<String, Object> m1 = ((HashMap) o1.getValue());
                            HashMap<String, Object> m2 = ((HashMap) o2.getValue());
                            double i1, i2;
                            if (m1.get("index") instanceof Long){
                                i1 = Long.valueOf( (long) m1.get("index")).doubleValue();
                            } else {
                                i1 = (double) m1.get("index");
                            }
                            if (m2.get("index") instanceof Long){
                                i2 = Long.valueOf( (long) m2.get("index")).doubleValue();
                            } else {
                                i2 = (double) m2.get("index");
                            }

                            return Double.valueOf(i1 - i2).intValue();
                        }
                    });

                    for (DataSnapshot tlList : dataSnapshot.getChildren()) {
                        sorted.add(tlList);
                    }

                    for (DataSnapshot tlList :  sorted){
                        if (tlList.getValue() instanceof HashMap) {
                            ids.add(tlList.getKey());
                            HashMap<String, Object> tl = ((HashMap) tlList.getValue());
                            ret.add(new TimeLapse(tl));
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
        ref.child("timelapses").child(gameId)
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
        ref.child("timelapses").child(gameId).child("subEpochsIds")
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
        ref.child("timelapses").child(gameId).child("resume")
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
        ref.child("timelapses").child(gameId).child("isLight")
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
        ref.child("timelapses").child(gameId).child("timeType")
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
