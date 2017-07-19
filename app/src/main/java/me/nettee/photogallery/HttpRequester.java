package me.nettee.photogallery;

import android.graphics.Bitmap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class HttpRequester {

    public abstract Bitmap getBitmap(String urlSpec) throws IOException;

    public abstract String getReturnText(String urlSpec) throws IOException;

}
