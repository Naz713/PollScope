package com.nazdesigns.polascope;

import android.app.Application;

import com.nazdesigns.polascope.GameStructure.TimeLapse;

import java.util.List;

public class PolApp extends Application {
    private List<TimeLapse> mGames;

    public void setGames(List<TimeLapse> mGames) {
        this.mGames = mGames;
    }

    public void setBranch(int[] place, TimeLapse branch){
        List<TimeLapse> games = mGames;
        for (int index : place) {
            // TODO: Actualizar juegos
        }
    }

    public List<TimeLapse> getGames() {
        return mGames;
    }
}
