package com.nazdesigns.polascope;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

public class EditActivity extends Activity implements LinearTextAdapter.EditListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
     public void onResumeEdited(String resume) {

     }

     @Override
     public void onLongTextEdited(String longResume) {

     }
 }
