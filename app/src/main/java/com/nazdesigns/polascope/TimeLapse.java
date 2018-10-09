package com.nazdesigns.polascope;

import java.util.List;

public class TimeLapse {
    private String resume;
    private boolean isLight;
    private String extenDescription;
    private List<TimeLapse> subEpocs;

    public class Scene extends TimeLapse{
        String question;
        String answer;
        String[] requiredCharacters;
        String[] banedCharacters;

        public Scene(boolean isLight, String resume, String extenDescription){
            super(isLight,resume,extenDescription);
        }

        public void setScene(String question, String[] requiredCharacters, String[] banedCharacters){
            this.question = question;
            this.requiredCharacters = requiredCharacters;
            this.banedCharacters = banedCharacters;
            this.answer = "";
        }

        public void setAnswer(String answer){
            this.answer = answer;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public void setRequiredCharacters(String[] requiredCharacters) {
            this.requiredCharacters = requiredCharacters;
        }

        public void setBanedCharacters(String[] banedCharacters) {
            this.banedCharacters = banedCharacters;
        }

        public String getAnswer() {
            return answer;
        }

        public String getQuestion() {
            return question;
        }

        public String[] getBanedCharacters() {
            return banedCharacters;
        }

        public String[] getRequiredCharacters() {
            return requiredCharacters;
        }
    }

    public TimeLapse(boolean isLight, String resume, String extenDescription){
        this.isLight = isLight;
        this.resume = resume;
        this.extenDescription = extenDescription;
        subEpocs = null;
    }

    public TimeLapse(boolean isLight, String resume, String extenDescription, List<TimeLapse> subEpocs){
        this(isLight,resume,extenDescription);
        this.subEpocs = subEpocs;
    }

    public List<TimeLapse> getSubEpocs() {
        return subEpocs;
    }

    public String getResume() {
        return resume;
    }

    public boolean getIsLight() {
        return isLight;
    }

    public String getExtenDescription() {
        return extenDescription;
    }

    public void setSubEpocs(List<TimeLapse> subEpocs) {
        this.subEpocs = subEpocs;
    }

    public void setExtenDescription(String extenDescription) {
        this.extenDescription = extenDescription;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public void setIsLight(boolean light) {
        isLight = light;
    }
}
