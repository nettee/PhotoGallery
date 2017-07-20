package me.nettee.photogallery;

import android.graphics.Bitmap;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class AsyncHttpRequester extends HttpRequester {

    private static final String TAG = "AsyncHttpRequester";

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public void requestForText(String urlSpec, final ResponseHandler<String> responseHandler) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(urlSpec, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String responseString = new String(responseBody);
                responseHandler.onSuccess(responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String responseString = new String(responseBody);
                responseHandler.onFailure(responseString);
            }
        });
    }

    @Override
    public void requestForBitmap(String urlSpec, ResponseHandler<Bitmap> responseHandler) {
        throw new AssertionError();
    }
}
