package com.pru.alternativeprocessing;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Utils {

    public static Bitmap getBitmap() throws IOException {
        URL url = new URL("https://pngbackground.com/public/uploads/preview/the-batman-pictures-mobile-dp-wallpapers-full-hd-new-11637535197tvpessq18u.jpg");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.connect();
        InputStream input = connection.getInputStream();
        return BitmapFactory.decodeStream(input);
    }
}
