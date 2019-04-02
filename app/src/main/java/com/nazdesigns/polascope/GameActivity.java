package com.nazdesigns.polascope;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nazdesigns.polascope.USoT.FBCaller;

public class GameActivity extends AppCompatActivity implements LinearTextAdapter.onListListener {

    private FirebaseAuth mAuth;
    private String mUserId;
    private AppBarLayout appBar;

    /*
    Responsable de presentar la informacion traida de TimeLapse atravez de FireBase
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * Checamos si tenenmos un Firebase user registrado, sino llamamos a Login para registrarlo
         */
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (null == user){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            mUserId = user.getUid();
        }
        /**
         * Inicalizamos el layout
         */
        setContentView(R.layout.activity_game);
        appBar = findViewById(R.id.app_bar);
        Toolbar toolbar = appBar.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerFragment recyclerFragment = new RecyclerFragment();

        // Add the fragment to the 'fragment_container' FrameLayout
        //TODO: set transaction
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, recyclerFragment)
                .commit();

  }

    @Override
    public void onClickListElement(String childfbId) {
        RecyclerFragment recyclerFragment = new RecyclerFragment();
        Bundle args = new Bundle();
        args.putString(RecyclerFragment.fbId, childfbId);
        recyclerFragment.setArguments(args);
        //TODO: set transaction
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, recyclerFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }
}
