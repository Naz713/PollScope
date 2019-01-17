package com.nazdesigns.polascope;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;

import com.nazdesigns.polascope.GameStructure.TimeLapse;

public class EditActivity extends Activity {
    private int[] mIndex;
    private TimeLapse mTL;
    private EditText mResume;
    private EditText mLongText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIndex = getIntent().getIntArrayExtra("index");
        setContentView(R.layout.activity_edit);
        mResume = findViewById(R.id.resume);
        mLongText = findViewById(R.id.long_text);

        mTL = ((PolApp) getApplication()).getBranch(mIndex);

        mResume.setText(mTL.getResume());
        mLongText.setText(mTL.getBody());
    }

    // TODO: Llamar de vuelta a PolApp para actualizar la informacion
    // cuando se actualice la informacion

 }
