package com.nazdesigns.polascope;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
                .add(R.id.text_recycler_view, recyclerFragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.log_out) {
            mAuth.signOut();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof RecyclerFragment) {
            RecyclerFragment listFragment = (RecyclerFragment) fragment;
            listFragment.setListener(this);
        }
    }

    @Override
    public void onClickListElement(int childfbId) {
        RecyclerFragment recyclerFragment = new RecyclerFragment();
        Bundle args = new Bundle();
        args.putInt("fbId", childfbId);
        recyclerFragment.setArguments(args);
        //TODO: set transaction
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.text_recycler_view, recyclerFragment)
                .addToBackStack(null)
                .commit();
    }
}
