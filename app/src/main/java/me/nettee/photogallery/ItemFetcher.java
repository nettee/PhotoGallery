package me.nettee.photogallery;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ItemFetcher {

    public interface FetchResultListener {
        void onResult(List<GalleryItem> galleryItems);
    }

    private static final String TAG = "ItemFetcher";

    private PhotoSource mSource;
    private HttpRequester mHttpRequester;

    public void setSource(PhotoSource source) {
        mSource = source;
    }

    public void setHttpRequester(HttpRequester httpRequester) {
        mHttpRequester = httpRequester;
    }

    public void fetch(final FetchResultListener listener) {
        if (mHttpRequester.isAsync()) {
            fetchSync(listener);
        } else {
            fetchAsync(listener);
        }
    }

    private void fetchSync(final FetchResultListener listener) {

        final List<GalleryItem> items = new ArrayList<>();

        mHttpRequester.requestForText(mSource.getUrl(), new HttpRequester.ResponseHandler<String>() {
            @Override
            public void onSuccess(String jsonString) {
                Log.i(TAG, String.format("Received response: %s...", jsonString.substring(0, 50)));
                try {
                    mSource.parseItems(items, jsonString);
                    listener.onResult(items);
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
    }

    private void fetchAsync(final FetchResultListener listener) {
        new AsyncTask<Void, Void, List<GalleryItem>>() {
            @Override
            protected List<GalleryItem> doInBackground(Void... voids) {

                final List<GalleryItem> items = new ArrayList<>();

                String urlSpec = mSource.getUrl();
                mHttpRequester.requestForText(urlSpec, new HttpRequester.ResponseHandler<String>() {
                    @Override
                    public void onSuccess(String jsonString) {
                        Log.i(TAG, String.format("Received response: %s...", jsonString.substring(0, 50)));
                        try {
                            mSource.parseItems(items, jsonString);
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


}
