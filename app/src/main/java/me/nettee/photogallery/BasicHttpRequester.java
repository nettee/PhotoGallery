package me.nettee.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BasicHttpRequester extends HttpRequester {

    private static final String TAG = "BasicHttpRequester";

    private interface Decoder<Out> {
        Out decode(InputStream in) throws IOException;
    }

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

    private static final Decoder<Bitmap> mBitmapDecoder = new Decoder<Bitmap>() {
        @Override
        public Bitmap decode(InputStream in) {
            return BitmapFactory.decodeStream(in);
        }
    };

    private <Out> void request(String urlSpec, Decoder<Out> decoder, ResponseHandler<Out> responseHandler){

        try {
            URL url = new URL(urlSpec);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode >= 400) {
                InputStream errorStream = connection.getErrorStream();
                String errorText = mReturnTextDecoder.decode(errorStream);
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

    @Override
    public void requestForText(String urlSpec, ResponseHandler<String> responseHandler) {
        request(urlSpec, mReturnTextDecoder, responseHandler);
    }

    @Override
    public void requestForBitmap(String urlSpec, ResponseHandler<Bitmap> responseHandler) {
        request(urlSpec, mBitmapDecoder, responseHandler);
    }
}
