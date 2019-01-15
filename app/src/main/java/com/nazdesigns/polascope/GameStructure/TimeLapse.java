package com.nazdesigns.polascope.GameStructure;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class TimeLapse implements Parcelable {
    public static int resumeMaxLenght = 86;
    public static int PERIOD_TYPE = 7;
    public static int EVENT_TYPE = 13;
    public static int SCENE_TYPE = 21;

    protected String myId;
    protected int timeType;
    protected String resume;
    protected boolean isLight;
    protected String body;
    protected List<TimeLapse> subEpochs;

    public TimeLapse(String id, int timeType, boolean isLight, String resume){
        this.myId = id;
        this.timeType = timeType;
        this.resume = resume;
        this.isLight = isLight;
        this.body = null;
        this.subEpochs = null;
    }

    protected TimeLapse(Parcel par){
        this.myId = par.readString();
        this.timeType = par.readInt();
        this.resume = par.readString();
        boolean[] isL = new boolean[1];
        par.readBooleanArray(isL);
        this.isLight = isL[0];
        this.body = par.readString();
        this.subEpochs = par.createTypedArrayList(TimeLapse.CREATOR);
    }

    public static final Parcelable.Creator<TimeLapse> CREATOR
            = new Parcelable.Creator<TimeLapse>() {
        public TimeLapse createFromParcel(Parcel in) {
            return new TimeLapse(in);
        }

        public TimeLapse[] newArray(int size) {
            return new TimeLapse[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(myId);
        dest.writeInt(timeType);
        dest.writeString(resume);
        dest.writeBooleanArray(new boolean[] {isLight});
        dest.writeString(body);
        dest.writeTypedList(subEpochs);

    }

    @Override
    public int describeContents() {
        return 0;
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
