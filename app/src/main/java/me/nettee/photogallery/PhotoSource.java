package me.nettee.photogallery;

import org.json.JSONException;

import java.util.List;

public interface PhotoSource {

    String getUrl();
    void parseItems(List<GalleryItem> items, String jsonString) throws JSONException;

}
