package com.nazdesigns.polascope.Utilities;

import android.content.Context;
import android.content.Intent;

import com.nazdesigns.polascope.EditActivity;

public class Common {
    public static void startEditActivity(Context context, String mId){
        /*
         * Start Edit Activity
         */
        Intent intent = new Intent(context, EditActivity.class);
        intent.putExtra(EditActivity.extraId, mId);
        context.startActivity(intent);
    }

    public static void startEditActivity(Context context, String mId, boolean insertAbove){
        /*
         * Start Edit Activity to create a new TimeLapse
         */
        Intent intent = new Intent(context, EditActivity.class);
        intent.putExtra(EditActivity.parentExtraId, mId);
        intent.putExtra(EditActivity.insertAbove, insertAbove);
        context.startActivity(intent);
    }
}
