package marumugam.org.eb;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;

import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;

import android.widget.EditText;

import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


import marumugam.org.eb.sql.DBhelper;
import marumugam.org.eb.sql.SQLController;
import marumugam.org.eb.tabs.TabsPagerAdapter;

public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    public EditText etTitle,etDesc;
    private SQLController dbController;
    private ListView listView;
    private SimpleCursorAdapter adapter;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);


        /*TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        TabsPagerAdapter tabsAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(tabsAdapter);
        tabs.setupWithViewPager(pager);*/


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                invokeDialog();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // initialize dbcontroller
        dbController = new SQLController(this);
        dbController.open();


        listView = (ListView) findViewById(R.id.feed_list);
        listView.setEmptyView(findViewById(R.id.empty));
        // Attach The Data From DataBase Into ListView Using Crusor Adapter
        cursor = dbController.fetch();
        String[] from = new String[] { DBhelper.TODO_SUBJECT,
                DBhelper.TODO_DESC };
        int[] to = new int[] { R.id.title, R.id.content};

        adapter = new SimpleCursorAdapter(this,
                R.layout.feed_item_layout, cursor, from, to);

        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.options_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if(id == R.id.action_search){
            onSearchRequested();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

       /* if (id == R.id.nav_tags) {
            // Handle the camera action
        } else if (id == R.id.nav_favorites) {

        } else if (id == R.id.nav_stats) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    // Add Item to list
    private void invokeDialog(){

        final Dialog dialog = new Dialog(BaseActivity.this);
        dialog.setContentView(R.layout.add_item_layout);
        dialog.setTitle("Add Item");


        etTitle = (EditText) dialog.findViewById(R.id.ds_fb);
        etDesc  = (EditText) dialog.findViewById(R.id.ds_fbdesc);

        Button dialogButton = (Button) dialog.findViewById(R.id.ds_send_button);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // insert into db
                dbController.insert(etTitle.getText().toString(), etDesc.getText().toString());
                Toast.makeText(BaseActivity.this, "Thou speaketh : " + etTitle.getText().toString(), Toast.LENGTH_SHORT).show();
                cursor = dbController.fetch();
                adapter.swapCursor(cursor);
                dialog.dismiss();

            }
        });

        dialog.show();
    }


    @Override
    protected void onResume() {
        super.onResume();

        Log.d("Resume","onResume()");
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onSearchRequested() {

        Bundle appData = new Bundle();
        //appData.putBoolean(SearchResultsActivity.JARGON, true);
        startSearch(null, false, appData, false);
        return true;

    }
}
