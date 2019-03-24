package com.newage.letstalk.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static android.os.Environment.isExternalStorageRemovable;

public class CacheStore {
    private static CacheStore INSTANCE = null;
    private HashMap<String, String> cacheMap;
    private HashMap<String, Bitmap> bitmapMap;
    private static final String cacheDir = "/Android/data/com.newage.letstalk/cache/";
    private static final String CACHE_FILENAME = ".cache";

    private CacheStore() {
        cacheMap = new HashMap<>();
        bitmapMap = new HashMap<>();

        File fullCacheDir = new File(Environment.getExternalStorageDirectory().toString(), cacheDir);
        if (!fullCacheDir.exists()) {
            Log.i("CACHE", "Directory does not exist");
            cleanCacheStart();
            return;
        }

        try {
            File file = new File(fullCacheDir.toString(), CACHE_FILENAME);
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            ObjectInputStream is = new ObjectInputStream(bis);
            cacheMap = (HashMap<String, String>) is.readObject();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cleanCacheStart() {
        cacheMap = new HashMap<>();
        File fullCacheDir = new File(Environment.getExternalStorageDirectory().toString(), cacheDir);
        boolean mkdirs = fullCacheDir.mkdirs();
        File noMedia = new File(fullCacheDir.toString(), ".nomedia");
        try {
            boolean create = noMedia.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized static void createInstance(){
        if(INSTANCE == null){
            INSTANCE = new CacheStore();
        }
    }

    public static CacheStore getInstance(){
        if(INSTANCE == null){
            createInstance();
        }
        return INSTANCE;
    }

    // Creates a unique subdirectory of the designated app cache directory. Tries to use external
    // but if not mounted, falls back on internal storage.
    public static File getDiskCacheDir(Context context, String uniqueName) {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                        || !isExternalStorageRemovable() ?
                        context.getExternalCacheDir().getPath() : context.getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }

    public void saveCacheFile(String uri, Bitmap image){
        File fullCacheDir = new File(Environment.getExternalStorageDirectory().toString(), cacheDir);
        String filelocalName = new SimpleDateFormat("ddMMyyhhmmssSS", Locale.getDefault()).format(new Date())+".PNG";
        File fileUri = new File(fullCacheDir, filelocalName);
        FileOutputStream outputStream = null;
        try{
            outputStream = new FileOutputStream(fileUri);
            image.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            cacheMap.put(uri, filelocalName);
            bitmapMap.put(uri, image);

            File file = new File(fullCacheDir.toString(), CACHE_FILENAME);
            FileOutputStream fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            ObjectOutputStream os = new ObjectOutputStream(bos);
            os.writeObject(cacheMap);
            os.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Bitmap getCacheFile(String uri){
        if(bitmapMap.containsKey(uri)) return bitmapMap.get(uri);

        if(!cacheMap.containsKey(uri)) return null;

        String localFileName = cacheMap.get(uri);
        File fullCacheDir = new File(Environment.getExternalStorageDirectory().toString(), cacheDir);
        File fileUri = new File(fullCacheDir.toString(), localFileName);
        if(!fileUri.exists()) return null;

        Bitmap bitmap = BitmapFactory.decodeFile(fileUri.toString());
        bitmapMap.put(uri, bitmap);
        return bitmap;
    }

}