package com.nazdesigns.polascope;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.nazdesigns.polascope.GameStructure.TimeLapse;
import com.nazdesigns.polascope.USoT.FBCaller;

public class EditActivity extends Activity {
    private String mfbId;
    private TimeLapse mTL;
    private EditText mResume;
    private EditText mLongText;
    private Button mDescarta;
    private Button mGuarda;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mfbId = getIntent().getStringExtra("fbId");

        setContentView(R.layout.activity_edit);

        mResume = findViewById(R.id.resume);
        mLongText = findViewById(R.id.long_text);

        mDescarta = findViewById(R.id.buton_descarta);
        mDescarta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResume.setText(mTL.getResume());
                mLongText.setText(mTL.getBody());
            }
        });

        mGuarda = findViewById(R.id.buton_guarda);
        mGuarda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTL.setResume(mResume.getText().toString());
                mTL.setBody(mLongText.getText().toString());
                FBCaller.saveTimeLapse(mfbId, mTL);
            }
        });
        mTL = FBCaller.getGame(this, mfbId);

        mResume.setText(mTL.getResume());
        mLongText.setText(mTL.getBody());
    }
 }
