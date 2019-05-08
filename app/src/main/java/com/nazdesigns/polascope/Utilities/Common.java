package com.nazdesigns.polascope.Utilities;

import android.content.Context;
import android.content.Intent;
import android.widget.EditText;

import com.nazdesigns.polascope.EditActivity;

public class Common {
    public static void startEditActivity(Context context, String mId, int timeType){
        /*
         * Start Edit Activity to Edit any type of TL
         */
        Intent intent = new Intent(context, EditActivity.class);
        intent.putExtra(EditActivity.extraId, mId);
        intent.putExtra(EditActivity.timeType, timeType);
        context.startActivity(intent);
    }

    public static void startCreateGameActivity(Context context, int timeType){
        /*
         * Start Edit Activity to create a new Game
         */
        Intent intent = new Intent(context, EditActivity.class);
        intent.putExtra(EditActivity.timeType, timeType);
        context.startActivity(intent);
    }

    public static void startCreateActivity(Context context, String mId, int newTLtype){
        /*
         * Start Edit Activity to create a new TimeLapse (no Game)
         * newTLtype indicates where to insert it or if is new
         */
        Intent intent = new Intent(context, EditActivity.class);
        intent.putExtra(EditActivity.relativeExtraId, mId);
        intent.putExtra(EditActivity.newTLtypeName, newTLtype);
        context.startActivity(intent);
    }

}
