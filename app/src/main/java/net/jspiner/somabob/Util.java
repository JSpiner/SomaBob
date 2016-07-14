package net.jspiner.somabob;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import net.jspiner.somabob.Service.HttpService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import retrofit.RestAdapter;

/**
 * Copyright 2016 JSpiner. All rights reserved.
 *
 * @author JSpiner (jspiner@naver.com)
 * @project SomaBob
 * @since 2016. 7. 12.
 */
public class Util {

    public static final String TAG = Util.class.getSimpleName();

    private static HttpService httpService;

    //Singleton HttpService
    public static HttpService getHttpSerivce() {

        if(httpService==null) {

            RestAdapter restAdapter =
                    new RestAdapter.Builder()
                            .setEndpoint("http://somabob.azurewebsites.net/api/")
                            .build();
            httpService = restAdapter.create(HttpService.class);
        }
        return httpService;
    }

    public static File resizeImage(File file){
        if(file==null) return file;

        Log.d(TAG, "resize start");

        File dir= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        Bitmap bmpPic= BitmapFactory.decodeFile(file.getPath());
        Log.d(TAG,"resize loaded");

        int MAX_IMAGE_SIZE = 200 * 1024; // max final file size
        File finalPath = new File(dir+"/resize.png");
        if ((bmpPic.getWidth() >= 1024) && (bmpPic.getHeight() >= 1024)) {
            BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
            bmpOptions.inSampleSize = 1;
            while ((bmpPic.getWidth() >= 1024) && (bmpPic.getHeight() >= 1024)) {
                bmpOptions.inSampleSize++;
                bmpPic = BitmapFactory.decodeFile(file.getPath(), bmpOptions);
            }
            Log.d(TAG, "Resize: " + bmpOptions.inSampleSize);
        }

        int compressQuality = 104; // quality decreasing by 5 every loop. (start from 99)
        int streamLength = MAX_IMAGE_SIZE;

        while (streamLength >= MAX_IMAGE_SIZE) {
            ByteArrayOutputStream bmpStream = new ByteArrayOutputStream();
            compressQuality -= 5;
            Log.d(TAG, "Quality: " + compressQuality);
            bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream);
            byte[] bmpPicByteArray = bmpStream.toByteArray();
            streamLength = bmpPicByteArray.length;
            Log.d(TAG, "Size: " + streamLength);
        }
        try {
            Log.d(TAG,"path : "+finalPath);
            FileOutputStream bmpFile = new FileOutputStream(finalPath);
            bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpFile);
            bmpFile.flush();
            bmpFile.close();
        } catch (Exception e) {
            Log.e(TAG, "Error on saving file" + e.getMessage());
        }

        return finalPath;
    }

}
