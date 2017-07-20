package me.nettee.photogallery;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PixabaySource implements PhotoSource {

    private static final String API_KEY = "5928733-3d080c3228c8c1a1d1ec177ae";
    private static final String BASE_URL = "https://pixabay.com/api/";
    private static final Map<String, String> PARAMS = new HashMap<String, String>() {
        {
            put("key", API_KEY);
            put("image_type", "photo");
            put("per_page", "100");
        }
    };

    @Override
    public String getUrl() {
        Uri.Builder uriBuiler = Uri.parse(BASE_URL)
                .buildUpon();
        for (Map.Entry<String, String> e : PARAMS.entrySet()) {
            uriBuiler.appendQueryParameter(e.getKey(), e.getValue());
        }
        Uri uri = uriBuiler.build();
        return uri.toString();
    }

    @Override
    public void parseItems(List<GalleryItem> items, String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray hitsJsonArray = jsonObject.getJSONArray("hits");

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
