package com.example.mkim123.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by mkim123 on 3/17/2016.
 */
public class ImageAdapter extends ArrayAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private ArrayList<String> imageUrls;

    public ImageAdapter(Context context, int layout, ArrayList<String> imageUrls) {
        super(context, layout, imageUrls);
        mContext = context;
        this.imageUrls = imageUrls;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.poster_thumbnail, parent, false);
        }
        GridView p = (GridView) parent;
        int newWidth = p.getWidth() / p.getNumColumns();

        Picasso.with(mContext).load(imageUrls.get(position))
                .resize(newWidth, 0)
                .into((ImageView) convertView);
        return convertView;
    }
}
