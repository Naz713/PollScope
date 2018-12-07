package com.nazdesigns.polascope;

import java.util.List;

public class TimeLapse {
    protected String myId;
    protected String resume;
    protected boolean isLight;
    protected String body;
    protected List<TimeLapse> subEpochs;

    public class Scene extends TimeLapse{
        String answer;

        public Scene(String id, boolean isLight, String question){
            super(id, isLight,question);
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

    public TimeLapse(String id, boolean isLight, String resume){
        this.myId = id;
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
