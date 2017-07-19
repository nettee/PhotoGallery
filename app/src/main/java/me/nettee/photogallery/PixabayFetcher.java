package me.nettee.photogallery;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PixabayFetcher {

    private static final String TAG = "PixabayFetcher";

    private static final String API_KEY = "5928733-3d080c3228c8c1a1d1ec177ae";
    private static final String BASE_URL = "https://pixabay.com/api/";
    private static final Map<String, String> PARAMS = new HashMap<String, String>() {
        {
            put("key", API_KEY);
            put("image_type", "photo");
            put("per_page", "100");
        }
    };

    private HttpRequester mHttpRequester;

    public PixabayFetcher() {
        mHttpRequester = new BasicHttpRequester();
    }

    public List<GalleryItem> fetchItems() {

        final List<GalleryItem> items = new ArrayList<>();

        Uri.Builder uriBuiler = Uri.parse(BASE_URL)
                .buildUpon();
        for (Map.Entry<String, String> e : PARAMS.entrySet()) {
            uriBuiler.appendQueryParameter(e.getKey(), e.getValue());
        }
        Uri uri = uriBuiler.build();

        mHttpRequester.requestForText(uri.toString(), new HttpRequester.ResponseHandler<String>() {
            @Override
            public void onSuccess(String jsonString) {
                Log.i(TAG, String.format("Received JSON: %s...", jsonString.substring(0, 50)));
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    parseItems(items, jsonObject);
                } catch (JSONException e) {
                    Log.e(TAG, "Failed to parse JSON", e);
                }
            }

            @Override
            public void onFailure(String errorText) {
                Log.i(TAG, "Failed to connect to Pixabay: " + errorText);
            }

            @Override
            public void onException(Throwable throwable) {
                Log.e(TAG, "Failed to fetch items", throwable);
            }
        });

        return items;
    }

    private void parseItems(List<GalleryItem> items, JSONObject jsonBody) throws JSONException {
        Log.i(TAG, "parsing items");
        JSONArray hitsJsonArray = jsonBody.getJSONArray("hits");
        
        for (int i = 0; i < hitsJsonArray.length(); i++) {
            JSONObject hitJsonObject = hitsJsonArray.getJSONObject(i);

            String id = hitJsonObject.getString("id");
            String tags = hitJsonObject.getString("tags");
            String url = hitJsonObject.getString("previewURL");

            GalleryItem item = new GalleryItem();
            item.setId(id);
            item.setCaption(tags);
            item.setUrl(url);

            items.add(item);
        }
    }
}
