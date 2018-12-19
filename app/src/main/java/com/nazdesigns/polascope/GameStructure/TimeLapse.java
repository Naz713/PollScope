package com.nazdesigns.polascope.GameStructure;

import java.util.List;

public class TimeLapse {
    protected String myId;
    protected int timeType;
    protected String resume;
    protected boolean isLight;
    protected String body;
    protected List<TimeLapse> subEpochs;

    public class Scene extends TimeLapse{
        String answer;

        public Scene(String id, int timeType, boolean isLight, String question){
            super(id, timeType, isLight,question);
        }

        public void answerScene(String answer, String body){
            this.answer = answer;
            this.body = body;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }
    }

    public TimeLapse(String id, int timeType, boolean isLight, String resume){
        this.myId = id;
        this.timeType = timeType;
        this.isLight = isLight;
        this.resume = resume;
        this.body = null;
        this.subEpochs = null;
    }

    public String getMyId() {
        return myId;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }

    public int getTimeType() {
        return timeType;
    }

    public void setTimeType(int timeType) {
        this.timeType = timeType;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public boolean getLight() {
        return isLight;
    }

    public void setLight(boolean light) {
        isLight = light;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<TimeLapse> getSubEpochs() {
        return subEpochs;
    }

    public void setSubEpochs(List<TimeLapse> subEpochs) {
        this.subEpochs = subEpochs;
    }
}
