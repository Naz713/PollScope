package com.nazdesigns.polascope;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.database.core.utilities.Utilities;
import com.nazdesigns.polascope.GameStructure.TimeLapse;
import com.nazdesigns.polascope.USoT.FBCaller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditActivity extends Activity {
    private String mfbId;
    private String mParentfbId;
    private boolean mInsertAbove;
    private boolean mIsNew;

    private TimeLapse mTL;
    private EditText mResume;
    private EditText mLongText;
    private ToggleButton mLight;
    private Button mDescarta;
    private Button mGuarda;

    public static String extraId = "fbId";
    public static String parentExtraId = "parentfbId";
    public static String insertAbove = "insertAbove";
    public static String isNew = "isNew";

    private interface OnSelectedPlayers{
        void callback(String[] selectedPlayers);
    }

    /**
     * Crea un diálogo con una lista de checkboxes
     * de selección multiple
     *
     * @return Diálogo
     */
    public void getSelectedPlayers(final OnSelectedPlayers callback) {
        final Context context = (Context) this;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        final ArrayList playersSelected = new ArrayList();

        final String[][] items = FBCaller.getAllPlayers();

        builder.setTitle("Elige a los Jugadores")
        .setMultiChoiceItems(items[1], null,
                new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    // Guardar indice seleccionado
                    playersSelected.add(items[0][which]);
                    Toast.makeText(context,
                            "Jugadores seleccionados:(" + playersSelected.size() + ")",
                            Toast.LENGTH_SHORT).show();
                } else if (playersSelected.contains(items[0][which])) {
                    // Remover indice sin selección
                    playersSelected.remove(items[0][which]);
                    Toast.makeText(context,
                            "Jugadores seleccionados:(" + playersSelected.size() + ")",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }).setPositiveButton(R.string.guarda_button_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String[] selectedPlayers = (String[]) playersSelected.toArray(new String[0]);
                Log.i("Edit", selectedPlayers.toString());
                callback.callback(selectedPlayers);
            }
        }).setNegativeButton(R.string.cancela_button_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("Edit","Canceled Pressed");
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mfbId = getIntent().getStringExtra(extraId);
        mParentfbId = getIntent().getStringExtra(parentExtraId);
        mInsertAbove = getIntent().getBooleanExtra(insertAbove,true);
        mIsNew = getIntent().getBooleanExtra(isNew,false);

        setContentView(R.layout.activity_edit);

        mResume = findViewById(R.id.edit_resume);
        mLongText = findViewById(R.id.edit_long_text);
        mLight = findViewById(R.id.isLightButton);

        mDescarta = findViewById(R.id.buton_descarta);
        mDescarta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Edit","Click Descarta");
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
                Log.i("Edit","Click Guardar");
                if (mfbId == null && mParentfbId == null) {
                // CASO CREAR JUEGO
                    Log.i("Edit","Crear Nuevo Juego");
                    mTL = new TimeLapse();
                    mTL.setResume(mResume.getText().toString());
                    mTL.setBody(mLongText.getText().toString());
                    mTL.setTimeType(TimeLapse.GAME_TYPE);

                    getSelectedPlayers(new OnSelectedPlayers() {
                        @Override
                        public void callback(String[] selectedPlayers) {
                            mfbId = FBCaller.createNewGame(mTL, selectedPlayers);
                        }
                    });
                } else if (mfbId != null && mTL != null) {
                // CASO EDIT
                    Log.i("Edit","Edit TimeLapse");
                    mTL.setResume(mResume.getText().toString());
                    mTL.setBody(mLongText.getText().toString());
                    mTL.setLight(mLight.isChecked());

                    if (mTL.getTimeType() == TimeLapse.GAME_TYPE) {
                        getSelectedPlayers(new OnSelectedPlayers() {
                            @Override
                            public void callback(String[] selectedPlayers) {
                                FBCaller.addGamePlayers(mfbId, selectedPlayers);
                            }
                        });
                    }

                    FBCaller.saveTimeLapse(mfbId, mTL);
                } else {
                // CASO CREAR NUEVO
                    Log.i("Edit","Crear TimeLapse");
                    mTL = new TimeLapse();
                    mTL.setResume(mResume.getText().toString());
                    mTL.setBody(mLongText.getText().toString());
                    mTL.setLight(mLight.isChecked());

                    if (mIsNew) {
                        FBCaller.createNewTimeLapse(mTL, mParentfbId);
                    } else {
                        FBCaller.createNewTimeLapse(mTL, mParentfbId, mInsertAbove);
                    }
                }
            }
        });

        if (mfbId != null){
            mTL = FBCaller.getGame(this, mfbId);
            mResume.setText(mTL.getResume());
            mLongText.setText(mTL.getBody());
            mLight.setChecked(mTL.isLight());
        }
        if (mfbId == null && mParentfbId == null) {
            mLight.setVisibility(View.GONE);
        }
    }
 }
