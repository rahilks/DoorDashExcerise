package com.rahil.mydoordash.fragment;


import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.rahil.mydoordash.R;
import com.rahil.mydoordash.RestaurantListAdapter;
import com.rahil.mydoordash.data.Restaurant;

import java.util.List;

public class FavsDialogFragment extends DialogFragment {

    List<Restaurant> restaurantList;
    private AlertDialog dialog;
    private RecyclerView mRecyclerView;

    public static FavsDialogFragment newInstance() {
        FavsDialogFragment fragment = new FavsDialogFragment();
        return fragment;
    }

    public void setRestaurantList(List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.fragment_favs_dialog, null);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.favs_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));

        RestaurantListAdapter restaurantListAdapter = new RestaurantListAdapter(restaurantList);
        restaurantListAdapter.disableFavButton();
        mRecyclerView.setAdapter(restaurantListAdapter);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle(R.string.favs_title);
        dialogBuilder.setView(view);
        dialogBuilder.setNegativeButton(R.string.close, null);
        dialog = dialogBuilder.create();
        return dialog;
    }
}
