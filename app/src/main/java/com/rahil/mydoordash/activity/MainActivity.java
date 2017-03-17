package com.rahil.mydoordash.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rahil.mydoordash.R;
import com.rahil.mydoordash.RestaurantListAdapter;
import com.rahil.mydoordash.api.DoorDashApi;
import com.rahil.mydoordash.data.Restaurant;
import com.rahil.mydoordash.fragment.FavsDialogFragment;
import com.rahil.mydoordash.fragment.ProgressDialogFragment;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements ProgressDialogFragment.ProgressDialogActionListener, View.OnClickListener {

    private static final String TAG_MY_ACTIVITY = "MY_DOOR_DASH_ACTIVITY";
    private static final String TAG_PROGRESS_DIALOG = "TAG_PROGRESS_DIALOG";
    private static final String RESTAURANTS_PREF_KEY = "Restaurants";
    private static final String FAV_DIALOG = "FAV_DIALOG";
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final String BASE_URL = "https://api.doordash.com";
    Call<List<Restaurant>> mGetRestaurantAPI;
    private RecyclerView mRecyclerView;
    private DoorDashApi mDoorDashApi;
    private RestaurantListAdapter mRestaurantListAdapter;
    private Menu optionsMenu;
    private Button mLocationSelectButton;
    private SharedPreferences mPrefs;
    private List<Restaurant> mRestaurantList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(myToolbar);
        mLocationSelectButton = (Button) findViewById(R.id.locationButton);
        mLocationSelectButton.setOnClickListener(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.restaurant_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        mPrefs = getPreferences(MODE_PRIVATE);
        Gson gson = new Gson();
        String resturantsJson = mPrefs.getString(RESTAURANTS_PREF_KEY, null);
        if (resturantsJson != null) {
            Type listType = new TypeToken<List<Restaurant>>() {
            }.getType();
            mRestaurantList = gson.fromJson(resturantsJson, listType);
            mRestaurantListAdapter = new RestaurantListAdapter(mRestaurantList);
            mRecyclerView.setAdapter(mRestaurantListAdapter);
        } else {
            readRestaurantsList(37.422740, -122.139956);
        }

    }

    private void readRestaurantsList(double latitude, double longitude) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mDoorDashApi = retrofit.create(DoorDashApi.class);
        mGetRestaurantAPI = mDoorDashApi.getRestaurantListByLocation(latitude, longitude);
        showProgressDialog();
        mGetRestaurantAPI.enqueue(new Callback<List<Restaurant>>() {
            @Override
            public void onResponse(Call<List<Restaurant>> call, Response<List<Restaurant>> response) {
                if (response.isSuccessful()) {
                    mRestaurantList = response.body();
                    mRestaurantListAdapter = new RestaurantListAdapter(mRestaurantList);
                    mRecyclerView.setAdapter(mRestaurantListAdapter);

                    SharedPreferences.Editor prefsEditor = mPrefs.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(mRestaurantList);

                    Log.e(TAG_MY_ACTIVITY, "json  -" + json);
                    prefsEditor.putString(RESTAURANTS_PREF_KEY, json);
                    prefsEditor.commit();

                } else {
                    Log.e(TAG_MY_ACTIVITY, response.errorBody().toString());
                    Toast.makeText(MainActivity.this, response.errorBody().toString(), Toast.LENGTH_LONG).show();
                }
                dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<List<Restaurant>> call, Throwable t) {
                Log.e(TAG_MY_ACTIVITY, t.getMessage());
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                dismissProgressDialog();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!mRestaurantList.isEmpty()) {
            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            Gson gson = new Gson();
            String json = gson.toJson(mRestaurantList);
            prefsEditor.putString(RESTAURANTS_PREF_KEY, json);
            prefsEditor.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.item_menu, menu);
        optionsMenu = menu;
        return true;
    }


    private void showProgressDialog() {
        ProgressDialogFragment.newInstance(getString(R.string.loading_rest_list))
                .show(getFragmentManager(), TAG_PROGRESS_DIALOG);
    }

    private void dismissProgressDialog() {
        ProgressDialogFragment progressDialog = (ProgressDialogFragment) getFragmentManager().findFragmentByTag(TAG_PROGRESS_DIALOG);
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onStopClicked() {
        if (mGetRestaurantAPI != null && !mGetRestaurantAPI.isExecuted()) {
            mGetRestaurantAPI.cancel();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favorite:
                List<Restaurant> favourites = getFavoriteRestaurants();
                if (!favourites.isEmpty()) {
                    FavsDialogFragment favsDialogFragment = FavsDialogFragment.newInstance();
                    favsDialogFragment.setRestaurantList(favourites);
                    favsDialogFragment.show(getFragmentManager(), FAV_DIALOG);
                } else {
                    Toast.makeText(this, getString(R.string.fav_error), Toast.LENGTH_LONG).show();
                }
                return true;
         /*   case R.id.action_show_all:
                if (mRestaurantListAdapter != null) {
                    mRestaurantListAdapter = new RestaurantListAdapter(mOrginalRestaurantList);
                    mRecyclerView.setAdapter(mRestaurantListAdapter);
                    mOrginalRestaurantList.clear();

                }
                item.setVisible(false);
                MenuItem favItem = optionsMenu.findItem(R.id.action_favorite);
                favItem.setVisible(true);
                return true; */
        }
        return false;
    }

    private List<Restaurant> getFavoriteRestaurants() {
        List<Restaurant> favoriteRestaurants = new LinkedList<>();
        for (Iterator<Restaurant> iterator = mRestaurantList.iterator(); iterator.hasNext(); ) {
            Restaurant restaurant = iterator.next();
            if (restaurant.getFav()) {
                favoriteRestaurants.add(restaurant);
            }
        }
        Log.e("getFavoriteRestaurants", "size " + favoriteRestaurants.size());
        return favoriteRestaurants;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.locationButton:

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                    Toast.makeText(this, R.string.location_play_error, Toast.LENGTH_LONG).show();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                    Toast.makeText(this, R.string.location_play_services_install_error, Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                readRestaurantsList(place.getLatLng().latitude, place.getLatLng().longitude);
            }
        }
    }
}
