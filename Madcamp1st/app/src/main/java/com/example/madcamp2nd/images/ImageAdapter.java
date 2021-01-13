package com.example.madcamp2nd.images;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madcamp2nd.R;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private final Fragment_Images fragment_images;
    List<String> temp_gallery;

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageButton imageButton;
        public TextView name;

        public ImageViewHolder(View imageView) {
            super(imageView);

            imageButton = imageView.findViewById(R.id.imageButton_imagebuttonAndTextview);
        }
    }

    public ImageAdapter(Fragment_Images fragment_images, Image[] images) {
        this.fragment_images = fragment_images;
    }

    public ImageAdapter(Fragment_Images fragment_images, List<String> temp_gallery) {
        this.fragment_images = fragment_images;
        this.temp_gallery = temp_gallery;
    }


    @NonNull
    @Override
    public ImageAdapter.ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View imageView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.imagebutton_and_textview, parent, false);

        return new ImageViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {

        String data = temp_gallery.get(position);
        byte[] bytePlainOrg = Base64.decode(data, 0);
        ByteArrayInputStream instream =  new ByteArrayInputStream(bytePlainOrg);
        Bitmap bm = BitmapFactory.decodeStream(instream);
        holder.imageButton.setImageBitmap(bm);

        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment_images.zoomImageFromThumb(holder.imageButton, bm);
            }
        });
    }

    @Override
    public int getItemCount() {
        return temp_gallery.size();
    }
}