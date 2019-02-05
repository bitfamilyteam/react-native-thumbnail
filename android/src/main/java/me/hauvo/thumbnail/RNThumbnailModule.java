
package me.hauvo.thumbnail;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import android.graphics.Bitmap;
import android.util.Log;
import android.media.MediaMetadataRetriever;

import java.util.UUID;
import java.io.File;
import java.io.OutputStream;
import java.io.FileOutputStream;


public class RNThumbnailModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  public RNThumbnailModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNThumbnail";
  }

  @ReactMethod
  public void get(String filePath, String outFilePath, Promise promise) {
    filePath = filePath.replace("file://","");
    outFilePath = outFilePath + "/thumb";

    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
    retriever.setDataSource(filePath);
    Bitmap image = retriever.getFrameAtTime(1000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

    try {
      File dir = new File(outFilePath);
      if (!dir.exists()) {
        dir.mkdirs();
      }

      OutputStream fOut = null;
      String fileName = "thumb-" + UUID.randomUUID().toString() + ".jpeg";
      File file = new File(outFilePath, fileName);
      file.createNewFile();
      fOut = new FileOutputStream(file);
      
      image.compress(Bitmap.CompressFormat.JPEG, 80, fOut);
      fOut.flush();
      fOut.close();

      WritableMap map = Arguments.createMap();

      map.putString("path", "file://" + outFilePath + '/' + fileName);
      map.putDouble("width", image.getWidth());
      map.putDouble("height", image.getHeight());

      promise.resolve(map);

    } catch (Exception e) {
      Log.e("E_RNThumnail_ERROR", e.getMessage());
      promise.reject("E_RNThumnail_ERROR", e);
    }
  }
}
