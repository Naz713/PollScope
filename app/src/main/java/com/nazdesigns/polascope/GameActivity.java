package com.nazdesigns.polascope;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Arrays;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private LinearTextAdapter mAdapter;
    private List<String> mData;
    private AppBarLayout appBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        appBar = findViewById(R.id.app_bar);
        Toolbar toolbar = appBar.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = findViewById(R.id.text_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        String text = getResources().getString(R.string.large_text);
        mData = Arrays.asList(text.split("\n\n"));
        mAdapter = new LinearTextAdapter(mData);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_game, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.dark_theme) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setLogo(R.mipmap.ic_dark);
            return true;
        }
        if (id == R.id.light_theme) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setLogo(R.mipmap.ic_light);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
