package com.nazdesigns.polascope.GameStructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TimeLapse {
    public final static int resumeMaxLenght = 86;
    public final static int GAME_TYPE = 0;
    public final static int PERIOD_TYPE = 1;
    public final static int EVENT_TYPE = 2;
    public final static int SCENE_TYPE = 3;

    private int timeType;
    private boolean isLight;
    private String resume;
    private String body;
    private double index;
    private String raiz;
    private List<String> players;
    private List<String> subEpochsIds;

    public TimeLapse(){
        timeType = 0;
        isLight = true;
        resume = null;
        body = null;
        index = 0.0;
        subEpochsIds = null;
    }

    public TimeLapse(HashMap<String, Object> data){
        for (String dt : data.keySet()){
            switch (dt){
                case "timeType":
                    this.timeType = (int) (long) data.get(dt);
                    break;
                case "body":
                    this.body = (String) data.get(dt);
                    break;
                case "isLight":
                    this.isLight = (boolean) data.get(dt);
                    break;
                case "index":
                    if (data.get(dt) instanceof Long){
                        this.index = (double) (long) data.get(dt);
                    } else {
                        this.index = (double) data.get(dt);
                    }
                    break;
                case "resume":
                    this.resume = (String) data.get(dt);
                    break;
                case "raiz":
                    this.raiz = (String) data.get(dt);
                    break;
                case "players":
                    if (data.get(dt) instanceof List){
                        this.players = (List<String>) data.get(dt);
                    }
                    break;
                case "subEpochsIds":
                    if (data.get(dt) instanceof List){
                        this.subEpochsIds = (List<String>) data.get(dt);
                    }
                    break;
            }
        }
    }

    public String getRaiz() {
        return raiz;
    }

    public void setRaiz(String raiz) {
        this.raiz = raiz;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }

    public int getTimeType() {
        return timeType;
    }

    public void setTimeType(int timeType) {
        this.timeType = timeType;
    }

    public boolean getIsLight() {
        return isLight;
    }

    public void setIsLight(boolean light) {
        isLight = light;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        if (resume.length() <= resumeMaxLenght){
            this.resume = resume;
        } else {
            this.resume = resume.substring(0, resumeMaxLenght);
        }
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public double getIndex() {
        return index;
    }

    public void setIndex(double index) {
        this.index = index;
    }

    public List<String> getSubEpochsIds() {
        if (subEpochsIds == null){
            this.subEpochsIds = new ArrayList<>();
        }
        return subEpochsIds;
    }

    public void setSubEpochsIds(List<String> subEpochsIds) {
        this.subEpochsIds = subEpochsIds;
    }
}
