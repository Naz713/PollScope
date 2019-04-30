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
        void callback(List<String> selectedPlayers);
    }

    /**
     * Crea un diálogo con una lista de checkboxes
     * de selección multiple
     *
     * @return Diálogo
     */
    public void getSelectedPlayers(final OnSelectedPlayers callback) {
        final Context context = (Context) this;

        FBCaller.getAllPlayers(new FBCaller.onListListCallback() {
            @Override
            public void onArrayReturned(final List<String> ids, List<String> names) {

                final List<String> playersSelected = new ArrayList<>();

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Agrega Jugadores")
                .setMultiChoiceItems((CharSequence[]) names.toArray(), null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            playersSelected.add(ids.get(which));
                        } else {
                            playersSelected.remove(ids.get(which) );
                        }
                    }
                }).setPositiveButton(R.string.guarda_button_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context,
                                "Jugadores seleccionados:(" + playersSelected.size() + ")",
                                Toast.LENGTH_SHORT).show();
                        Log.i("Edit", playersSelected.toString());
                        callback.callback(playersSelected);
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
        });


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
                    mLight.setChecked(mTL.getIsLight());
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
                        public void callback(List<String> selectedPlayers) {
                            FBCaller.createNewGame(mTL, selectedPlayers, new FBCaller.onStringCallback() {
                                @Override
                                public void onStringReturned(String result) {
                                    //TODO:
                                    mfbId = result;
                                }
                            });
                        }
                    });

                } else if (mfbId != null && mTL != null) {
                // CASO EDIT
                    Log.i("Edit","Edit TimeLapse");
                    mTL.setResume(mResume.getText().toString());
                    mTL.setBody(mLongText.getText().toString());
                    mTL.setIsLight(mLight.isChecked());

                    if (mTL.getTimeType() == TimeLapse.GAME_TYPE) {
                        getSelectedPlayers(new OnSelectedPlayers() {
                            @Override
                            public void callback(List<String> selectedPlayers) {
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
                    mTL.setIsLight(mLight.isChecked());

                    if (mIsNew) {
                        FBCaller.createNewTimeLapse(mTL, mParentfbId, new FBCaller.onStringCallback() {
                            @Override
                            public void onStringReturned(String result) {
                                //TODO:
                            }
                        });
                    } else {
                        FBCaller.createNewTimeLapse(mTL, mParentfbId, mInsertAbove, new FBCaller.onStringCallback() {
                            @Override
                            public void onStringReturned(String result) {
                                //TODO:
                            }
                        });
                    }
                }
            }
        });

        if (mfbId != null){
            FBCaller.getGame(mfbId, new FBCaller.onTLCallback() {
                @Override
                public void onTimeLapseResult(TimeLapse result) {
                    //TODO:
                    mTL = result;
                }
            });
            mResume.setText(mTL.getResume());
            mLongText.setText(mTL.getBody());
            mLight.setChecked(mTL.getIsLight());
        }
        if (mfbId == null && mParentfbId == null) {
            mLight.setVisibility(View.GONE);
        }
    }
 }
