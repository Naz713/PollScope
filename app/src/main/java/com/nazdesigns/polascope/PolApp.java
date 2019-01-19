package com.nazdesigns.polascope;

import android.app.Application;
import android.support.annotation.Nullable;

import com.nazdesigns.polascope.GameStructure.TimeLapse;

import java.util.List;

import static java.lang.Boolean.FALSE;

public class PolApp extends Application {
    private List<TimeLapse> mGames;

    public void setGames(List<TimeLapse> mGames) {
        this.mGames = mGames;
    }

    public void setBranch(int[] place, TimeLapse branch){

//        if (mTL.getResume() != null) {
//            mResume.setText(mTL.getResume());
//        } else
//            mResume.setText(getString(R.string.games_list_msg));

        List<TimeLapse> games = mGames;
        for (int index : place) {
            // TODO: Actualizar juegos
        }
    }

    public TimeLapse getBranch(@Nullable int[] place){
        // Si es null regresamos todos los juegos

        // update from data base first
        // TODO: regresar el TimeLapse correspondiente
        return new TimeLapse(null,0, FALSE, null);
    }

    public List<TimeLapse> getGames() {
        return mGames;
    }

}



// TODO: Crear toda la logica para crear TimeLapse Nuevos