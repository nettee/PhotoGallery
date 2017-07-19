package me.nettee.photogallery;

import android.graphics.Bitmap;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.IOException;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class AsyncHttpRequester {

    private static final String TAG = "AsyncHttpRequester";

    public String getReturnText(String urlSpec) throws IOException {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        final String[] returnText = {null};
        client.get(urlSpec, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, String.format("Fail to get HTTP response: [%d] %s", statusCode, responseString), throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                returnText[0] = responseString;
            }
        });
        return returnText[0];
    }

    public Bitmap getBitmap(String urlSpec) throws IOException {
        return null;
    }
}
