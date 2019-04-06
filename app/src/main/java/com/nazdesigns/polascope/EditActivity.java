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

import com.nazdesigns.polascope.GameStructure.TimeLapse;
import com.nazdesigns.polascope.USoT.FBCaller;

import java.util.ArrayList;
import java.util.Arrays;

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

    /**
     * Crea un diálogo con una lista de checkboxes
     * de selección multiple
     *
     * @return Diálogo
     */
    public String[] getSelectedPlayers() {
        final Context context = (Context) this;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        final ArrayList itemsSeleccionados = new ArrayList();

        CharSequence[] items = new CharSequence[3];

        //TODO: Llenar con jugadores y regresar los ids de los seleccionados

        items[0] = "Desarrollo Android";
        items[1] = "Diseño De Bases De Datos";
        items[2] = "Pruebas Unitarias";

        builder.setTitle("Intereses")
                .setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            // Guardar indice seleccionado
                            itemsSeleccionados.add(which);
                            Toast.makeText(context, "Checks seleccionados:(" + itemsSeleccionados.size() + ")", Toast.LENGTH_SHORT).show();
                        } else if (itemsSeleccionados.contains(which)) {
                            // Remover indice sin selección
                            itemsSeleccionados.remove(Integer.valueOf(which));
                        }
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

        return (String[]) itemsSeleccionados.toArray();

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

                    mfbId = FBCaller.createNewGame(mTL, getSelectedPlayers());
                } else if (mfbId != null && mTL != null) {
                // CASO EDIT
                    Log.i("Edit","Edit TimeLapse");
                    mTL.setResume(mResume.getText().toString());
                    mTL.setBody(mLongText.getText().toString());
                    mTL.setLight(mLight.isChecked());

                    if (mTL.getTimeType() == TimeLapse.GAME_TYPE) {
                        FBCaller.setGamePlayers(getSelectedPlayers());
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
