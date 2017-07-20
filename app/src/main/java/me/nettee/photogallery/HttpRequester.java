package me.nettee.photogallery;

import android.graphics.Bitmap;

public abstract class HttpRequester {

    public interface ResponseHandler<Result> {
        void onSuccess(Result result);
        void onFailure(String errorText);
        void onException(Throwable throwable);
    }

    public abstract boolean isAsync();
    public abstract void requestForText(String urlSpec, ResponseHandler<String> responseHandler);
    public abstract void requestForBitmap(String urlSpec, ResponseHandler<Bitmap> responseHandler);

}
