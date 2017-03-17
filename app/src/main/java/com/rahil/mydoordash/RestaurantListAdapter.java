package com.rahil.mydoordash;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rahil.mydoordash.data.Restaurant;

import java.util.List;

public class RestaurantListAdapter extends RecyclerView.Adapter<RestaurantListAdapter.ViewHolder> {

    private final List<Restaurant> restaurantList;
    private boolean disableFav;

    public RestaurantListAdapter(List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
    }

    public void disableFavButton() {
        disableFav = true;
    }

    @Override
    public RestaurantListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mNameTextView.setText(restaurantList.get(position).getName());
        holder.mDescTextView.setText(restaurantList.get(position).getFoodType());
        holder.mAddressTextView.setText(restaurantList.get(position).getAddress().getPrintableAddress());
        if (restaurantList.get(position).getFav()) {
            Log.e("thelistadapt", "fav found " + position);
        }
        int drawableId = restaurantList.get(position).getFav() ? android.R.drawable.star_big_on : android.R.drawable.star_big_off;
        holder.mFavImageView.setImageDrawable(holder.mFavImageView.getContext().getResources().getDrawable(drawableId));
        holder.mFavImageView.setTag(drawableId);
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mNameTextView;
        private TextView mDescTextView;
        private TextView mAddressTextView;
        private ImageView mFavImageView;

        public ViewHolder(View v) {
            super(v);
            mNameTextView = (TextView) v.findViewById(R.id.nameTextView);
            mDescTextView = (TextView) v.findViewById(R.id.descTextView);
            mAddressTextView = (TextView) v.findViewById(R.id.addressTextView);
            mFavImageView = (ImageView) v.findViewById(R.id.favImageView);
            if (!disableFav) {
                mFavImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean markFavorite = ((int) v.getTag()) == android.R.drawable.star_big_off;
                        int drawableId = markFavorite ? android.R.drawable.star_big_on : android.R.drawable.star_big_off;
                        mFavImageView.setImageDrawable(v.getContext().getResources().getDrawable(drawableId));
                        mFavImageView.setTag(drawableId);
                        Log.e("thelistadapt", "pos-" + markFavorite + " " + ViewHolder.this.getAdapterPosition());
                        restaurantList.get(ViewHolder.this.getAdapterPosition()).setFav(markFavorite);
                    }
                });
            }
        }
    }
}
