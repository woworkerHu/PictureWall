package com.example.zl.mobikedemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.example.zl.mobikedemo.utils.HttpUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by zl on 18/4/20.
 */

public class ImageLoader {
    Context context;
    Point point = new Point(100, 100);

    public ImageLoader(Context context) {
        this.context = context;
    }

    int maxCacheSize = (int) Runtime.getRuntime().maxMemory() / 8;
    LruCache<String, Bitmap> mMemoryCache = new LruCache<String, Bitmap>(maxCacheSize) {
        @Override
        protected int sizeOf(String key, Bitmap bitmap) {
            return bitmap.getByteCount();
        }
    };

    public Context getContext() {
        return context;
    }

    public void putImage(String key, Bitmap bitmap) {
        if (key == null || bitmap == null) {
            return;
        }
        mMemoryCache.put(key, bitmap);
    }

    /**
     * 如果内存中没有存储，则从SD卡中进行查找，如果还是没有，就使用网络进行下载
     *
     * @param key
     * @return
     */
    public Bitmap getImage(final String key, final CallBack callback1) throws IOException {
        final Bitmap[] bitmap = {mMemoryCache.get(key)};
        final String url = cutUrl(key);
        if (bitmap[0] == null) {
            final File jpg = new File(context.getExternalCacheDir(), url + ".jpg");
            if (!jpg.exists()) {
                jpg.createNewFile();
                Runnable runnable = new Runnable() {

                    @Override
                    public void run() {
                        final Callback callback = new Callback() {

                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.d(TAG, "onFailure: " + key);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                put2SDcache(url, response.body().byteStream());
                                bitmap[0] = decodeFromFile(jpg, getPoint().x, getPoint().y);
                                callback1.setImage(bitmap[0]);
                            }
                        };
                        HttpUtils.doGetStream(key, callback);
                    }
                };
                HttpUtils.execute(runnable);
            } else {
                 return decodeFromFile(jpg, getPoint().x, getPoint().y);
            }

        }
        return bitmap[0];
    }

    private String cutUrl(String key) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            if(c>'z' || c < 'a'){
                continue;
            }
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(int width, int height) {
        point.x = width;
        point.y = height;
    }

    private Bitmap decodeFromFile(File file, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        int inSampleSize = caculateInsampleSize(width, height, options);
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        return bitmap;
    }

    private int caculateInsampleSize(int width, int height, BitmapFactory.Options options) {
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;

        if (outWidth > width || outHeight > height) {
            int i = outWidth / width;
            int j = outHeight / height;
            return i < j ? i : j;
        } else {
            return 1;
        }
    }

    /**
     * 缓存到SD卡中
     *
     * @param url
     * @param inputStream
     */
    public void put2SDcache(String url, InputStream inputStream) {
        try {
            File jpg = new File(context.getExternalCacheDir(), url + ".jpg");
            if (!jpg.exists()) {
                jpg.createNewFile();

            }
            FileOutputStream fileOutputStream = new FileOutputStream(jpg);
            byte[] bytes = new byte[1024 * 8];
            int len = bytes.length;
            while ((len = inputStream.read(bytes, 0, len)) != -1) {
                fileOutputStream.write(bytes, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getSize() {
        return mMemoryCache.size();
    }
}
