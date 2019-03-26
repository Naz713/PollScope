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
    private String mParentfbId;
    private TimeLapse mTL;
    private EditText mResume;
    private EditText mLongText;
    private Button mDescarta;
    private Button mGuarda;

    public static String extraId = "fbId";
    public static String parentExtraId = "parentfbId";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mfbId = getIntent().getStringExtra(extraId);
        mParentfbId = getIntent().getStringExtra(parentExtraId);

        setContentView(R.layout.activity_edit);

        mResume = findViewById(R.id.edit_resume);
        mLongText = findViewById(R.id.edit_long_text);

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
                if (mfbId != null && mTL != null){
                    mTL.setResume(mResume.getText().toString());
                    mTL.setBody(mLongText.getText().toString());
                    FBCaller.saveTimeLapse(mfbId, mTL);
                } else {
                    mTL = new TimeLapse();
                    mTL.setResume(mResume.getText().toString());
                    mTL.setBody(mLongText.getText().toString());

                    // TODO: Lanzar Dialog para preguntar al usuario siguientes
                    boolean isAfter = true;
                    boolean isLight = true;

                    mTL.setLight(isLight);
                    FBCaller.createNewTimeLapse(mTL, mParentfbId, isAfter);
                }
            }
        });
        if (mfbId != null){
            mTL = FBCaller.getGame(this, mfbId);
            mResume.setText(mTL.getResume());
            mLongText.setText(mTL.getBody());
        }
    }
 }
