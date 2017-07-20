package me.nettee.photogallery;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BasicHttpRequester {

    private static final String TAG = "BasicHttpRequester";

    private <Out> void request(String urlSpec, Decoder<InputStream, Out> decoder, HttpRequester.ResponseHandler<Out> responseHandler){

        try {
            URL url = new URL(urlSpec);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode >= 400) {
                InputStream errorStream = connection.getErrorStream();
                String errorText = new Decoder.StreamTextDecoder().decode(errorStream);
                responseHandler.onFailure(errorText);
            } else {
                InputStream in = connection.getInputStream();
                Out out = decoder.decode(in);
                responseHandler.onSuccess(out);
            }
        } catch (IOException e) {
            responseHandler.onException(e);
        }
    }

    public void requestForText(String urlSpec, HttpRequester.ResponseHandler<String> responseHandler) {
        request(urlSpec, new Decoder.StreamTextDecoder(), responseHandler);
    }

    public void requestForBitmap(String urlSpec, HttpRequester.ResponseHandler<Bitmap> responseHandler) {
        request(urlSpec, new Decoder.StreamBitmapDecoder(), responseHandler);
    }

}
