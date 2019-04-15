package com.nazdesigns.polascope.GameStructure;

import java.util.List;

public class TimeLapse {
    public static int resumeMaxLenght = 86;
    public static int GAME_TYPE = 0;
    public static int PERIOD_TYPE = 1;
    public static int EVENT_TYPE = 2;
    public static int SCENE_TYPE = 3;

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

    public TimeLapse(int timeType, boolean isLight, String resume, String body, double orderIndex, List<String> subEpochsIds){
        this.timeType = timeType;
        this.isLight = isLight;
        this.resume = resume;
        this.body = body;
        this.orderIndex = orderIndex;
        this.subEpochsIds = subEpochsIds;
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
        return subEpochsIds;
    }

    public void setSubEpochsIds(List<String> subEpochsIds) {
        this.subEpochsIds = subEpochsIds;
    }
}
