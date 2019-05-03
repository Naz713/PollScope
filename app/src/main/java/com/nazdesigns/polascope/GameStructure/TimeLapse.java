package com.nazdesigns.polascope.GameStructure;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TimeLapse {
    public final static int resumeMaxLenght = 86;
    public final static int GAME_TYPE = 0;
    public final static int PERIOD_TYPE = 1;
    public final static int EVENT_TYPE = 2;
    public final static int SCENE_TYPE = 3;

    public int timeType;
    public boolean isLight;
    public String resume;
    public String body;
    public double orderIndex;
    public List<String> subEpochsIds;

    public TimeLapse(){
        timeType = 0;
        isLight = true;
        resume = null;
        body = null;
        orderIndex = 0.0;
        subEpochsIds = null;
    }

    public TimeLapse(int timeType, boolean isLight, String resume, String body, double orderIndex,
                     List<String> subEpochsIds){
        this.timeType = timeType;
        this.isLight = isLight;
        this.resume = resume;
        this.body = body;
        this.orderIndex = orderIndex;
        this.subEpochsIds = subEpochsIds;
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
                case "orderIndex":
                    this.orderIndex = (double) (long) data.get(dt);
                    break;
                case "resume":
                    this.resume = (String) data.get(dt);
                    break;
                case "subEpochsIds":
                    if (data.get(dt) instanceof List){
                        this.subEpochsIds = (List<String>) data.get(dt);
                    }
                    break;
            }
        }
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

    public double getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(double orderIndex) {
        this.orderIndex = orderIndex;
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
