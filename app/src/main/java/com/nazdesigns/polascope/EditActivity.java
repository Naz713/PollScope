package com.nazdesigns.polascope;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.nazdesigns.polascope.GameStructure.TimeLapse;
import com.nazdesigns.polascope.USoT.FBCaller;

public class EditActivity extends Activity {
    private String mfbId;
    private String mParentfbId;
    private boolean mInsertAbove;

    private TimeLapse mTL;
    private EditText mResume;
    private EditText mLongText;
    private ToggleButton mLight;
    private Button mDescarta;
    private Button mGuarda;

    public static String extraId = "fbId";
    public static String parentExtraId = "parentfbId";
    public static String insertAbove = "insertAbove";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mfbId = getIntent().getStringExtra(extraId);
        mParentfbId = getIntent().getStringExtra(parentExtraId);
        mInsertAbove = getIntent().getBooleanExtra(EditActivity.insertAbove,true);

        setContentView(R.layout.activity_edit);

        mResume = findViewById(R.id.edit_resume);
        mLongText = findViewById(R.id.edit_long_text);
        mLight = findViewById(R.id.isLightButton);

        mDescarta = findViewById(R.id.buton_descarta);
        mDescarta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTL != null){
                    mResume.setText(mTL.getResume());
                    mLongText.setText(mTL.getBody());
                    mLight.setChecked(mTL.isLight());
                }
            }
        });

        mGuarda = findViewById(R.id.buton_guarda);
        mGuarda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mfbId != null && mTL != null){
                    mTL.setResume(mResume.getText().toString());
                    mTL.setBody(mLongText.getText().toString());
                    mTL.setLight(mLight.isChecked());

                    FBCaller.saveTimeLapse(mfbId, mTL);
                } else {
                    mTL = new TimeLapse();
                    mTL.setResume(mResume.getText().toString());
                    mTL.setBody(mLongText.getText().toString());
                    mTL.setLight(mLight.isChecked());

                    FBCaller.createNewTimeLapse(mTL, mParentfbId, mInsertAbove);
                }
            }
        });
        if (mfbId != null){
            mTL = FBCaller.getGame(this, mfbId);
            mResume.setText(mTL.getResume());
            mLongText.setText(mTL.getBody());
            mLight.setChecked(mTL.isLight());
        }
    }
 }
