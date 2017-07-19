package me.nettee.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public interface Decoder<In, Out> {

    Out decode(In in) throws IOException;

    class StreamTextDecoder implements Decoder<InputStream, String> {
        @Override
        public String decode(InputStream in) throws IOException {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }

    class StreamBitmapDecoder implements Decoder<InputStream, Bitmap> {
        @Override
        public Bitmap decode(InputStream in) {
            return BitmapFactory.decodeStream(in);
        }
    }

    class ByteArrayTextDecoder implements Decoder<byte[], String> {
        @Override
        public String decode(byte[] bytes) throws IOException {
            return new String(bytes);
        }
    }

    class ByteArrayBitmapDecoder implements Decoder<byte[], Bitmap> {
        @Override
        public Bitmap decode(byte[] bytes) throws IOException {
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
    }
}
