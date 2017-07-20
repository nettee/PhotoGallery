package me.nettee.photogallery;

import android.os.AsyncTask;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ItemFetcher {

    private static final String TAG = "ItemFetcher";
    private PixabayStrategy mPixabayStrategy;
    private BasicHttpRequester mBasicHttpRequester;
    public ItemFetcher() {
        mPixabayStrategy = new PixabayStrategy();
        mBasicHttpRequester = new BasicHttpRequester();
    }

    public void fetch(final FetchResultListener listener) {
        fetchSync(listener);
//        fetchAsync(listener);
    }

    private void fetchSync(final FetchResultListener listener) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(mPixabayStrategy.getUrl(), new AsyncHttpResponseHandler() {

            final List<GalleryItem> items = new ArrayList<>();

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String jsonString = new String(responseBody);
                    Log.i(TAG, String.format("Received response: %s...", jsonString.substring(0, 50)));
                    mPixabayStrategy.parseItems(items, jsonString);
                    listener.onResult(items);
                } catch (JSONException e) {
                    Log.e(TAG, "Failed to parse JSON", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e(TAG, "Failed to fetch items", error);
            }
        });
    }

    private void fetchAsync(final FetchResultListener listener) {
        new AsyncTask<Void, Void, List<GalleryItem>>() {
            @Override
            protected List<GalleryItem> doInBackground(Void... voids) {

                final List<GalleryItem> items = new ArrayList<>();

                String urlSpec = mPixabayStrategy.getUrl();
                mBasicHttpRequester.requestForText(urlSpec, new HttpRequester.ResponseHandler<String>() {
                    @Override
                    public void onSuccess(String jsonString) {
                        Log.i(TAG, String.format("Received response: %s...", jsonString.substring(0, 50)));
                        try {
                            mPixabayStrategy.parseItems(items, jsonString);
                        } catch (JSONException e) {
                            Log.e(TAG, "Failed to parse JSON", e);
                        }
                    }

                    @Override
                    public void onFailure(String errorText) {
                        Log.i(TAG, "Failed to connect to server: " + errorText);
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        Log.e(TAG, "Failed to fetch items", throwable);
                    }
                });
                return items;
            }

            @Override
            protected void onPostExecute(List<GalleryItem> galleryItems) {
                listener.onResult(galleryItems);
            }
        }.execute();
    }

    public interface FetchResultListener {
        void onResult(List<GalleryItem> galleryItems);
    }


}
