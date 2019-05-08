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

import com.google.firebase.auth.FirebaseAuth;
import com.nazdesigns.polascope.GameStructure.TimeLapse;
import com.nazdesigns.polascope.USoT.FBCaller;

import java.util.ArrayList;
import java.util.List;

public class EditActivity extends Activity {
    private String mfbId;
    private String mParentfbId;
    private int mNewTLtype;

    private TimeLapse mTL;
    private int mTimeType;
    private EditText mResume;
    private EditText mLongText;
    private ToggleButton mLight;
    private Button mDescarta;
    private Button mGuarda;

    public static String extraId = "fbId";
    public static String timeType = "timeType";
    public static String relativeExtraId = "parentfbId";
    public static String newTLtypeName = "newTLtype";

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
                String userId = null;
                try{
                    userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }

                boolean[] checkdItems = new boolean[ids.size()];
                if (userId != null && ids.contains(userId)){
                    checkdItems[ids.indexOf(userId)] = true;
                }

                final List<String> playersSelected = new ArrayList<>();
                final String Uid = userId;

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Agrega Jugadores")
                .setMultiChoiceItems(names.toArray(new String[0]), checkdItems,
                        new DialogInterface.OnMultiChoiceClickListener() {
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
                        if (Uid != null && !playersSelected.contains(Uid)){
                            playersSelected.add(Uid);
                        }

                        Toast.makeText(context,
                                "Jugadores agregados:(" + playersSelected.size() + ")",
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
        mTimeType = getIntent().getIntExtra(timeType, 0);
        mParentfbId = getIntent().getStringExtra(relativeExtraId);
        mNewTLtype = getIntent().getIntExtra(newTLtypeName, 0);

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

                if (mfbId == null){
                    //CASO CREAR TL
                    Log.i("Edit","Crear Nuevo");

                    mTL = new TimeLapse();
                    mTL.setResume(mResume.getText().toString());
                    mTL.setBody(mLongText.getText().toString());

                    if (mTimeType != TimeLapse.GAME_TYPE){
                        //CASO CREAR TL NO GAME
                        Log.i("Edit","NO Tipo Juego");
                        mTL.setIsLight(mLight.isChecked());

                        if (mNewTLtype == 0) {
                            FBCaller.createNewTimeLapse(mTL, mParentfbId, new FBCaller.onStringCallback() {
                                @Override
                                public void onStringReturned(String result) {
                                    mfbId = result;
                                }
                            });
                        } else {
                            FBCaller.createNewTimeLapse(mTL, mParentfbId, mNewTLtype > 0, new FBCaller.onStringCallback() {
                                @Override
                                public void onStringReturned(String result) {
                                    mfbId = result;
                                }
                            });
                        }
                    } else {
                        //CASO CREAR GAME
                        Log.i("Edit","Tipo Juego");
                        mTL.setTimeType(TimeLapse.GAME_TYPE);

                        getSelectedPlayers(new OnSelectedPlayers() {
                            @Override
                            public void callback(List<String> selectedPlayers) {
                                FBCaller.createNewGame(mTL, selectedPlayers, new FBCaller.onStringCallback() {
                                    @Override
                                    public void onStringReturned(String result) {
                                        mfbId = result;
                                    }
                                });
                            }
                        });
                    }

                } else {
                    // CASO EDITAR
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
                }
                //TODO: Llamar a un popUp para salir de la Activity
            }
        });

        if (mTimeType == TimeLapse.GAME_TYPE) {
            mLight.setVisibility(View.GONE);
        }

        if (mfbId != null){
            FBCaller.getGame(mfbId, new FBCaller.onTLCallback() {
                @Override
                public void onTimeLapseResult(TimeLapse result) {
                    mTL = result;
                    mResume.setText(mTL.getResume());
                    mLongText.setText(mTL.getBody());
                    mLight.setChecked(mTL.getIsLight());

                    if (mTL.getTimeType() == TimeLapse.GAME_TYPE) {
                        mLight.setVisibility(View.GONE);
                    }
                }
            });
        }
    }
 }
