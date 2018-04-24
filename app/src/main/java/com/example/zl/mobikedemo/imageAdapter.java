package com.example.zl.mobikedemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by zl on 18/4/21.
 */

public class imageAdapter extends BaseAdapter {

    ImageLoader imageLoader;
    Activity context;
    ArrayList<Info> infoArrayList;

    public imageAdapter(ImageLoader imageLoader, ArrayList<Info> arrayList) {
        this.imageLoader = imageLoader;
        this.infoArrayList = arrayList;
    }

    public void setContext(Activity context) {
        this.context = context;
//        notifyAll();
    }

    @Override
    public int getCount() {
        return infoArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Info info = infoArrayList.get(position);
        final String url = info.getUrl();
        final String title = info.getTitle();
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item, null);
            final ImageView imageView = convertView.findViewById(R.id.image);
            final TextView textView = convertView.findViewById(R.id.text);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.textView = textView;
            viewHolder.imageView =imageView;
            convertView.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        final ImageView imageView = viewHolder.imageView;

        CallBack callBack = new CallBack() {

            @Override
            public void setImage(final Bitmap bitmap) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bitmap);
                        imageView.setTag(url);
                    }
                });
            }
        };
        try {
            Bitmap image = imageLoader.getImage(url, callBack);
            if (image != null) {
                imageView.setImageBitmap(image);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return convertView;
    }
}

class ViewHolder{
    ImageView imageView;
    TextView textView;
}