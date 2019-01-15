package com.nazdesigns.polascope;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nazdesigns.polascope.GameStructure.TimeLapse;
import com.nazdesigns.polascope.USoT.DBCaller;
import com.nazdesigns.polascope.USoT.FBCaller;

import java.util.List;

public class GameActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private List<TimeLapse> mData;
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
        /**
        Llamamos a la base de datos
         */
        DBCaller db = new FBCaller(mUserId);
        mData = db.getAllGames();

        RecyclerFragment recyclerFragment = new RecyclerFragment();
        Bundle args = new Bundle();
        args.putString("text",getString(R.string.games_list_msg));
        args.putParcelableArray("list", (Parcelable[]) mData.toArray());
        recyclerFragment.setArguments(args);

        // Add the fragment to the 'fragment_container' FrameLayout
        //TODO: set transaction
        getSupportFragmentManager().beginTransaction()
                .add(R.id.text_recycler_view, recyclerFragment).commit();
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
}
