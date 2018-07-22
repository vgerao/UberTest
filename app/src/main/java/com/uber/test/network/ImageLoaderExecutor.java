package com.uber.test.network;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import com.uber.test.R;

/**
 * Created by dell on 7/21/2018.
 */
public class ImageLoaderExecutor {
    private Map<ImageView, String> imageViews=Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    private ExecutorService executorService;
    private final int stub_id= R.mipmap.ic_launcher;
    private final String TAG = ImageLoaderExecutor.class.getName();
    private static ImageLoaderExecutor instance;

    private ImageLoaderExecutor(){
        executorService=Executors.newFixedThreadPool(5);
    }

    public static ImageLoaderExecutor getInstance(){
        if (instance == null){
            instance = new ImageLoaderExecutor();
        }
        return instance;
    }

    public void DisplayImage(String url, ImageView imageView)
    {
        imageViews.put(imageView, url);
            queuePhoto(url, imageView);
            imageView.setImageResource(stub_id);
    }
    private void queuePhoto(String url, ImageView imageView)
    {
        PhotoToLoad p=new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoader(p));
    }
    //Task for the queue
    private class PhotoToLoad
    {
        public String url;
        public ImageView imageView;
        public PhotoToLoad(String u, ImageView i){
            url=u;
            imageView=i;
        }
    }

    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;
        PhotosLoader(PhotoToLoad photoToLoad){
            this.photoToLoad=photoToLoad;
        }

        @Override
        public void run() {
            if(imageViewReused(photoToLoad))
                return;
            Bitmap bitmap = downloadBitmap(photoToLoad.url);
            if (photoToLoad != null) {
                if (photoToLoad.imageView != null) {
                    if (bitmap != null) {
                        photoToLoad.imageView.setImageBitmap(bitmap);
                    } else {
                        Drawable placeholder = photoToLoad.imageView.getContext().getResources().getDrawable(R.mipmap.ic_launcher);
                        photoToLoad.imageView.setImageDrawable(placeholder);
                    }
                }
            }
        }
    }

    boolean imageViewReused(PhotoToLoad photoToLoad){
        String tag=imageViews.get(photoToLoad.imageView);
        if(tag==null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }

    private Bitmap downloadBitmap(String url) {
        HttpURLConnection urlConnection = null;
        try {
            URL uri = new URL(url);
            Log.i(TAG,""+ url);
            urlConnection = (HttpURLConnection) uri.openConnection();

            int statusCode = urlConnection.getResponseCode();
            if (statusCode != 200) {
                return null;
            }

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            }
        } catch (Exception e) {
            urlConnection.disconnect();
            Log.w("ImageDownloader", "Error downloading image from " + url);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }
}
