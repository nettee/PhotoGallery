package me.nettee.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.Tag;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class BasicHttpRequester extends HttpRequester {

    private static final String TAG = "BasicHttpRequester";

    private static final Decoder<Bitmap> mBitmapDecoder = new Decoder<Bitmap>() {
        @Override
        public Bitmap decode(InputStream in) {
            return BitmapFactory.decodeStream(in);
        }
    };

    private static final Decoder<String> mReturnTextDecoder = new Decoder<String>() {
        @Override
        public String decode(InputStream in) throws IOException {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    };

    private interface Decoder<Out> {
        Out decode(InputStream in) throws IOException;
    }

    private <Out> Out getAndDecode(String urlSpec, Decoder<Out> decoder) throws IOException {

        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Open InputStream to connection
        connection.connect();

        int responseCode = connection.getResponseCode();
        if (responseCode >= 400) {
            InputStream errorStream = connection.getErrorStream();
            String errorString = mReturnTextDecoder.decode(errorStream);
            Log.e(TAG, "Error HTTP response status: " + errorString);
            return null;
        }

        InputStream in = connection.getInputStream();

        // Download and decode
        Out out = decoder.decode(in);
        return out;
    }

    @Override
    public Bitmap getBitmap(String urlSpec) throws IOException {
        return getAndDecode(urlSpec, mBitmapDecoder);
    }

    @Override
    public String getReturnText(String urlSpec) throws IOException {


        return getAndDecode(urlSpec, mReturnTextDecoder);
    }
}
