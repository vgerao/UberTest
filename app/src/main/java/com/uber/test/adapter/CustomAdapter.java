package com.uber.test.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.uber.test.R;
import com.uber.test.model.PhotoItemUrl;
import com.uber.test.network.ImageLoaderExecutor;
import java.util.ArrayList;

/**
 * Created by dell on 7/21/2018.
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private ArrayList<PhotoItemUrl> listData;
    Context context;

    public CustomAdapter(Context context, ArrayList<PhotoItemUrl> listData) {
        this.context = context;
        this.listData = listData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        if (holder.image != null) {
            ImageLoaderExecutor.getInstance().DisplayImage(listData.get(position).getImageURL(), holder.image);
        }
    }

    public void addImages(ArrayList<PhotoItemUrl> ll){
        for(PhotoItemUrl listItem : ll){
            listData.add(listItem);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        public MyViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}
