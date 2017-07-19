package me.nettee.photogallery;

import android.graphics.Bitmap;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.IOException;

import cz.msebera.android.httpclient.Header;

public class AsyncHttpRequester extends HttpRequester {

    private static final String TAG = "AsyncHttpRequester";

    private <Out> void request(String urlSpec, final Decoder<byte[], Out> decoder, final ResponseHandler<Out> responseHandler) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        client.get(urlSpec, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Out out = decoder.decode(responseBody);
                    responseHandler.onSuccess(out);
                } catch (IOException e) {
                    responseHandler.onException(e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    String responseString = new Decoder.ByteArrayTextDecoder().decode(responseBody);
                    Log.e(TAG, String.format("Fail to get HTTP response: [%d] %s", statusCode, responseString), error);
                } catch (IOException e) {
                    responseHandler.onException(e);
                }
            }
        });
    }

    @Override
    public void requestForText(String urlSpec, ResponseHandler<String> responseHandler) {
        request(urlSpec, new Decoder.ByteArrayTextDecoder(), responseHandler);
    }

    @Override
    public void requestForBitmap(String urlSpec, ResponseHandler<Bitmap> responseHandler) {
        request(urlSpec, new Decoder.ByteArrayBitmapDecoder(), responseHandler);
    }
}
