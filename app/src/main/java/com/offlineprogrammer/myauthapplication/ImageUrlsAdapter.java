package com.offlineprogrammer.myauthapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ImageUrlsAdapter extends RecyclerView.Adapter<ImageUrlsAdapter.ViewHolder> {
    private static final String TAG = "ImageUrlsAdapter";
    private ArrayList<ImageUrl> imageUrls;
    private Context context;

    public ImageUrlsAdapter(Context context, ArrayList<ImageUrl> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;

    }

    @Override
    public ImageUrlsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_itemview, viewGroup, false);
        return new ViewHolder(view);
    }

    public void add(ImageUrl item, int position) {
        imageUrls.add(position, item);
        Log.i(TAG, "add: " + item.toString());
        notifyItemInserted(position);
        //notifyDataSetChanged();
        //notifyItemRangeChanged(0, getItemCount());
    }

    /**
     * gets the image url from adapter and passes to Glide API to load the image
     *
     * @param viewHolder
     * @param i
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Glide.with(context).load(imageUrls.get(i).getImageUrl()).into(viewHolder.img);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView img;

        public ViewHolder(View view) {
            super(view);
            img = view.findViewById(R.id.imageView);
        }
    }
}


