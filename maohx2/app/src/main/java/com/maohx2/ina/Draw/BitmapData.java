package com.maohx2.ina.Draw;

import android.graphics.Bitmap;

/**
 * Created by ina on 2017/10/08.
 */

class BitmapData{

    Bitmap bitmap;
    String image_name;



    BitmapData(){}

    Bitmap getBitmap(){
        return bitmap;
    }

    void setBitmap(Bitmap _bitmap){
        bitmap = _bitmap;
    }


    String getImageName(){
        return image_name;
    }

    void setImageName(String _image_name){
        image_name = _image_name;
    }



/*
    static final Bitmap loadBitmapAsset(String fileName, Context context) throws IOException {
        final AssetManager assetManager = context.getAssets();
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(assetManager.open(fileName));
            return BitmapFactory.decodeStream(bis);
        } finally {
            try {
                bis.close();
            } catch (Exception e) {
                //IOException, NullPointerException
            }
        }
    }
    */
}