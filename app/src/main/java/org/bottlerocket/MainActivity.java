package org.bottlerocket;

import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.SearchView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/*This is the main activity for app start*/
public class MainActivity extends AppCompatActivity implements StoresAdapter.StoresAdapterListener {

    private RecyclerView recyclerView;
    private StoresAdapter mAdapter;
    private SearchView searchView;
    private LinearLayout offLinePart;
    private static final String url = "http://sandbox.bottlerocketapps.com/BR_Android_CodingExam_2015_Server/stores.json";
    private static final String CACHE_FILE_NAME="cacheForStores.json";
    private static String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    ExpandableRelativeLayout expandableLayout1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        verifyLocationPermissions(this);
        //Fancy Toolbar
        //getSupportActionBar().setIcon(R.drawable.shopping_cart);
        getSupportActionBar().setTitle(R.string.toolbar_title);
       // getSupportActionBar().setIcon(R.drawable.shopping_cart);
        recyclerView = findViewById(R.id.recycler_view);

        //haha-1
        //storeList = new ArrayList<Store>();
        Commons.storeList = new ArrayList<Store>();
        mAdapter = new StoresAdapter(this, Commons.storeList, this);

        // white background notification bar
        whiteNotificationBar(recyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 36));
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        offLinePart = findViewById(R.id.OfflinePart);
        if (Commons.storeList.size() == 0){
            if (isNetworkAvailable()) //Internet available
            {
                new HttpRequestTask().execute();
                offLinePart.setVisibility(View.INVISIBLE);
            } else {//Not available
                //Read from Cache
                ObjectMapper mapper = new ObjectMapper();
                Stores storesfromCache = null;
                try {
                    storesfromCache = mapper.readValue(new File(getCacheDir() + "/" + CACHE_FILE_NAME), Stores.class);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "No Internet Connection and no data in Cache!", Toast.LENGTH_LONG).show();
                }
                if (storesfromCache == null) {  ///No data in cache
                    offLinePart.setVisibility(View.VISIBLE);
                } else { //Data in cache
                    offLinePart.setVisibility(View.INVISIBLE);
                    //Toast.makeText(getApplicationContext(), "No Internet Connection, fetched from cache!", Toast.LENGTH_LONG).show();
                    //haha-2
                    //for(Store store:storesfromCache.getStores()) storeList.add(store);
                    for (Store store : storesfromCache.getStores()) Commons.storeList.add(store);
                    mAdapter.notifyDataSetChanged();
                }
            }
    }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        //Attempt to invoke virtual method 'void android.support.v7.widget.SearchView.setSearchableInfo(android.app.SearchableInfo)' on a null object reference
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }


    private class HttpRequestTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {

                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                Stores stores = restTemplate.getForObject(url, Stores.class);
                Logger.log_d("Number of stores fetched:"+stores.getStores().length);
                //haha-3
                for(Store store:stores.getStores()) Commons.storeList.add(store);
                //for(Store store:stores.getStores()) storeList.add(store);

                //Cache module
                //Write to cache
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(new File(getCacheDir()+"/"+CACHE_FILE_NAME), stores);
                Logger.log_d("Successfully inserted into cache");


            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void t) {
            mAdapter.notifyDataSetChanged();
        }

    }

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }
        if(id==R.id.map){
            Intent intent = new Intent(this, StoresOnMap.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStoreSelected(Store stores) {
        //Toast.makeText(getApplicationContext(), "Selected: " + contact.getName() + ", " + contact.getPhone(), Toast.LENGTH_LONG).show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void verifyLocationPermissions(Activity activity) {

        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permission != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS,1
            );
        }

    }
}